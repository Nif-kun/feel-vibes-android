package com.example.feelvibes.home.profile

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentProfileBinding
import com.example.feelvibes.dialogs.AuthorizationRequestDialog
import com.example.feelvibes.dialogs.ConfirmationAlertDialog
import com.example.feelvibes.dialogs.EditProfileDialog
import com.example.feelvibes.dialogs.SimpleAlertDialog
import com.example.feelvibes.utils.FVFireStoreHandler
import com.example.feelvibes.utils.FireBaseStorageHandler
import com.example.feelvibes.utils.PickMedia
import com.example.feelvibes.utils.ShortLib
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.HomeViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : FragmentBind<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    // Note:
    //  -[vbNull]: A NullPointerException occurs on VB due to spamming of page. Specifically load delay.

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var accountViewModel : AccountViewModel

    private val fireBaseStorageHandler = FireBaseStorageHandler()
    private val authorizationRequestDialog = AuthorizationRequestDialog()
    private val confirmLogoutDialog = ConfirmationAlertDialog()
    private val pickMedia = PickMedia()

    private var firstName: String? = null
    private var lastName: String? = null
    private var username: String? = null
    private var bio: String? = null
    private var currentUser: FirebaseUser? = null

    private var loadingListener: ((Int) -> Unit )? = null
    private var loadingProgress = 0
    private val maxLoading = 4 // increase depending on the number of things needed to be loaded
    private var cancelLoadWarning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }

    override fun onReady() {
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (accountViewModel.currentNavStackId != null) {
                    val navId = accountViewModel.currentNavStackId
                    accountViewModel.currentNavStackId = null
                    findNavController().navigate(navId!!)
                }
            }
        })
        onBackEvent()
        buildTabs()
        homeViewModel.layoutState = HomeViewModel.Layouts.PROFILE
        mainActivity.hideToolBar(isHome = true, includeLayout = true)
        pickMedia.setup(this)
        updateViews()
        onAuthorizationDismissedEvent()
    }

    private fun buildTabs(){
        binding.profileViewPager.adapter = ProfileViewPageAdapter(this)
        binding.createTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.profileViewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Change nav tab based on page
        binding.profileViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.createTabLayout.getTabAt(position)?.select()
            }
        })
    }

    private fun updateViews() {
        FVFireStoreHandler.checkAuth { currentUser ->
            var userIdEmpty = false
            this.currentUser = currentUser
            if (accountViewModel.selectedUserId == null && currentUser == null) {
                userIdEmpty = true
                accountViewModel.currentUser = null
                authorizationRequestDialog.popNav = { findNavController().navigate(R.id.action_profileFragment_to_homeFragment) }
                authorizationRequestDialog.show(mainActivity.supportFragmentManager, "AuthorizationRequestDialog")
            } else if (currentUser != null) {
                if (accountViewModel.selectedUserId == null) {
                    accountViewModel.selectedUserId = currentUser.uid
                    accountViewModel.currentUser = currentUser
                }
                try { // [vbNull]
                    if (accountViewModel.selectedUserId == currentUser.uid) {
                        onEditEvent(currentUser.uid)
                        onChangeProfileEvent()
                        onChangeBannerEvent()
                        onLogOutEvent()
                    } else {
                        binding.editBtn.visibility = View.GONE
                        binding.logoutBtn.visibility = View.GONE
                        binding.followBtn.visibility = View.VISIBLE
                        onFollowEvent()
                    }
                    updateFullName()
                    updateUsername()
                    updateBio()
                    loadProfilePicture()
                    loadProfileBanner()
                } catch (e:Exception) {
                    Log.d("ProfileFragment", "An error occurred on updateViews. (Has current user)")
                    e.printStackTrace()
                }
            } else {
                binding.editBtn.visibility = View.GONE
                binding.logoutBtn.visibility = View.GONE
                updateFullName()
                updateUsername()
                updateBio()
                loadProfilePicture()
                loadProfileBanner()
            }
            try { // [vbNull]
                if (!userIdEmpty)
                    onLoadingComplete()
            } catch (e:Exception) {
                Log.d("ProfileFragment", "An error occurred on updateViews. (Outside of onLoadingComplete)")
                e.printStackTrace()
            }
        }
    }

    private fun onAuthorizationDismissedEvent() {
        authorizationRequestDialog.onDismissListener = {
            updateViews()
        }
    }

    private fun onLoadingComplete() {
        loadingListener = {
            loadingProgress+=it
            if (loadingProgress >= maxLoading) {
                transitionShimmerOut()
            }
        }
        binding.root.postDelayed({
            if (loadingProgress < maxLoading) {
                if (!cancelLoadWarning)
                    warnLoadError()
            }
        }, 60000)
    }

    private fun transitionShimmerOut() {
        try {
            binding.displayDataShimmerInclude.displayDataLayout.stopShimmer()
            binding.displayDataShimmerInclude.displayDataLayout.visibility = View.INVISIBLE
            binding.displayDataLayout.visibility = View.VISIBLE
            binding.profilePic.visibility = View.VISIBLE
            binding.profileBanner.visibility = View.VISIBLE
            binding.profileViewPager.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.d("ProfileFragment", "An error occurred on transitionShimmerOut")
            e.printStackTrace()
        }
    }

    private fun warnLoadError() {
        val dialog = SimpleAlertDialog()
        dialog.title = mainActivity.getString(R.string.timeout_error_title)
        dialog.text = mainActivity.getString(R.string.timeout_error_text)
        dialog.popNav = { findNavController().navigate(R.id.action_profileFragment_to_homeFragment) }
        dialog.show(mainActivity.supportFragmentManager, "LoadErrorDialog")
    }

    @SuppressLint("SetTextI18n")
    private fun onEditEvent(userId: String) {
        binding.editBtn.setOnClickListener {
            val editProfileDialog = EditProfileDialog()
            editProfileDialog.userId = userId
            editProfileDialog.firstName = firstName ?: ""
            editProfileDialog.lastName = lastName ?: ""
            editProfileDialog.username = username ?: ""
            editProfileDialog.bio = bio ?: ""
            editProfileDialog.confirmListener = { firstName, lastName, username, bio ->
                if (firstName.length >= 3) {
                    binding.fullNameView.text = firstName
                    this.firstName = firstName
                }
                if (lastName.length >= 3) {
                    if (binding.fullNameView.text.isNotEmpty()) {
                        binding.fullNameView.text = "${binding.fullNameView.text} $lastName"
                        this.lastName = lastName
                    } else {
                        binding.fullNameView.text = lastName
                        this.lastName = lastName
                    }
                }
                if (username.length >= 3) {
                    binding.usernameView.text = username
                    this.username = username
                }
                if (bio.isNotEmpty()) {
                    binding.bioView.text = bio
                    this.bio = bio
                }
                Toast.makeText(requireActivity(), "Profile updated!", Toast.LENGTH_SHORT).show()
            }
            editProfileDialog.show(requireActivity().supportFragmentManager, "EditProfileDialog")
        }
        /*
        binding.editBtn.setOnClickListener {
            if (binding.userNameAndBioViewLayout.visibility == View.VISIBLE) {
                username?.let {
                    binding.usernameEdit.setText(it)
                }
                bio?.let {
                    binding.bioEdit.setText(it)
                }
                binding.userNameAndBioViewLayout.visibility = View.GONE
                binding.editBtn.visibility = View.GONE
                binding.followViewLayout.visibility = View.GONE
                binding.userNameAndBioEditLayout.visibility = View.VISIBLE
            }
        }

        binding.cancelEditBtn.setOnClickListener {
            binding.userNameAndBioEditLayout.visibility = View.GONE
            binding.editBtn.visibility = View.VISIBLE
            binding.userNameAndBioViewLayout.visibility = View.VISIBLE
            binding.followViewLayout.visibility = View.VISIBLE
        }

        binding.saveEditBtn.setOnClickListener {
            val newUsername = binding.usernameEdit.text.toString()
            val newBio = binding.bioEdit.text.toString()
            if (newUsername.isNotEmpty()) {
                if (newUsername.length >= 3) {
                    if (newUsername != username) {
                        updateUsername(newUsername)
                    }
                } else {
                    Toast.makeText(requireActivity(), "Username must be at least 3 characters!", Toast.LENGTH_SHORT).show()
                }
            }
            if (newBio != bio) {
                updateBio(newBio)
            }
            binding.userNameAndBioEditLayout.visibility = View.GONE
            binding.editBtn.visibility = View.VISIBLE
            binding.userNameAndBioViewLayout.visibility = View.VISIBLE
            binding.followViewLayout.visibility = View.VISIBLE
        }
        */
    }

    private fun onFollowEvent() {
        if (this.currentUser != null && accountViewModel.selectedUserId != null) {

            // Set following/follower count
            FVFireStoreHandler.getFollowings(accountViewModel.selectedUserId!!) { followings, exception ->
                if (followings.isNotEmpty()) {
                    binding.followingNumView.text = ShortLib.simplifyNumFormat(followings.size)
                }
                exception?.printStackTrace()
            }
            FVFireStoreHandler.getFollowers(accountViewModel.selectedUserId!!) { followers, exception ->
                if (followers.isNotEmpty()) {
                    binding.followerNumView.text = ShortLib.simplifyNumFormat(followers.size)
                }
                exception?.printStackTrace()
            }

            // Check if already followed
            FVFireStoreHandler.isFollowing(this.currentUser!!.uid, accountViewModel.selectedUserId!!) { following, exception ->
                if (following) {
                    binding.followBtn.isChecked = true
                } else {
                    exception?.printStackTrace()
                }
            }

            // On follow/unfollow event listeners
            binding.followBtn.addOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    button.text = getString(R.string.unfollow)
                    button.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.white, null))
                    FVFireStoreHandler.followUser(this.currentUser!!.uid, accountViewModel.selectedUserId!!)
                } else {
                    button.text = getString(R.string.follow)
                    button.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.purple_200, null))
                    FVFireStoreHandler.unfollowUser(this.currentUser!!.uid, accountViewModel.selectedUserId!!)
                }
            }
        }
    }

    private fun onBackEvent() {
        binding.backBtn.setOnClickListener {
            if (accountViewModel.currentNavStackId != null) {
                val navId = accountViewModel.currentNavStackId
                accountViewModel.currentNavStackId = null
                findNavController().navigate(navId!!)
            } else {
                Log.d("onBackEvent", "An error occurred, currentNavStackId is null!")
                findNavController().popBackStack()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateFullName() {
        val db = FirebaseFirestore.getInstance()
        val documentDisplayDataRef = accountViewModel.selectedUserId?.let {
            db.collection("usersDisplayData").document(it)
        }
        documentDisplayDataRef?.get()?.addOnSuccessListener { documentSnapshot ->
            try {
                if (documentSnapshot.exists()) {
                    documentSnapshot.getString("firstName")?.let { firstName ->
                        this.firstName = firstName
                        loadingListener?.invoke(1)
                        binding.fullNameView.text = firstName
                        binding.fullNameView.visibility = View.VISIBLE
                        documentDisplayDataRef.get().addOnSuccessListener { documentSnapshot ->
                            try {
                                if (documentSnapshot.exists()) {
                                    documentSnapshot.getString("lastName")?.let { lastName ->
                                        this.lastName = lastName
                                        loadingListener?.invoke(1)
                                        binding.fullNameView.text = "${binding.fullNameView.text} $lastName"
                                    }
                                }
                            } catch (e: Exception) {
                                Log.d("ProfileFragment", "An error occurred on updateFullName. (Unknown: refer to updateUsername exception)")
                                e.printStackTrace()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ProfileFragment", "An error occurred on updateFullName. (Unknown: refer to updateUsername exception)")
                e.printStackTrace()
            }
        }
    }

    private fun updateUsername(username: String? = null) {
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("usersDisplayData").whereEqualTo("username", username)
        val documentDisplayDataRef = accountViewModel.selectedUserId?.let {
            db.collection("usersDisplayData").document(it)
        }
        val documentRef = accountViewModel.selectedUserId?.let {
            db.collection("users").document(it)
        }
        if (username != null) {
            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result?.documents?.isEmpty() == true) {
                        // Username does not exist, proceed to storing.
                        documentDisplayDataRef?.set(mutableMapOf("username" to username), SetOptions.merge())?.addOnSuccessListener {
                            this.username = username
                            binding.usernameView.text = username
                        }?.addOnFailureListener {
                            it.printStackTrace()
                            Toast.makeText(requireActivity(), "Username failed to save...", Toast.LENGTH_SHORT).show()
                        }
                        documentRef?.set(mutableMapOf("username" to username), SetOptions.merge())?.addOnSuccessListener {
                            this.username = username
                            binding.usernameView.text = username
                        }?.addOnFailureListener {
                            it.printStackTrace()
                            Toast.makeText(requireActivity(), "Username failed to save...", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Username exists, ending registration
                        Toast.makeText(mainActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle the exception from the query
                    task.exception?.printStackTrace()
                    Toast.makeText(mainActivity, "Username failed to save...", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            documentDisplayDataRef?.get()?.addOnSuccessListener { documentSnapshot ->
                try {
                    if (documentSnapshot.exists()) {
                        documentSnapshot.getString("username")?.let {
                            this.username = it
                            loadingListener?.invoke(1)
                            binding.usernameView.text = it
                        }
                    }
                } catch (e: Exception) {
                    Log.d("ProfileFragment", "An error occurred on updateUsername. (Has username)")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun updateBio(bio: String? = null) {
        val db = FirebaseFirestore.getInstance()
        val documentDisplayDataRef = accountViewModel.selectedUserId?.let {
            db.collection("usersDisplayData").document(it)
        }
        if (bio != null) {
            documentDisplayDataRef?.set(hashMapOf("bio" to bio), SetOptions.merge())?.addOnSuccessListener {
                binding.bioView.text = bio
            }?.addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(requireActivity(), "Bio failed to save...", Toast.LENGTH_SHORT).show()
            }
        } else {
            documentDisplayDataRef?.get()?.addOnSuccessListener { documentSnapshot ->
                try {
                    if (documentSnapshot.exists()) {
                        val documentBio = documentSnapshot.getString("bio")
                        if (documentBio != null) {
                            this.bio = documentBio
                            loadingListener?.invoke(1)
                            binding.bioView.text = documentBio
                        } else {
                            loadingListener?.invoke(1)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("ProfileFragment", "An error occurred on updateBio. (Bio is null)")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadProfilePicture() {
        val storageRef = FirebaseStorage.getInstance().getReference("profilePictures")
        val imageRef = storageRef.child("${accountViewModel.selectedUserId}.png")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            try { // [vbNull]
                Glide.with(this)
                    .load(uri)
                    .into(binding.profilePic);
            } catch (e: Exception) {
                Log.d("ProfileFragment", "An error occurred on loadProfilePicture. (Download url success)")
                e.printStackTrace()
            }
            loadingListener?.invoke(1)
        }.addOnFailureListener {
            Log.d("ProfileFragment", "An error occurred on loadProfilePicture. (Download url failed)")
            it.printStackTrace()
            loadingListener?.invoke(1)
        }
    }

    private fun onChangeProfileEvent() {
        binding.profilePic.setOnClickListener {
            if (accountViewModel.currentUser != null) {
                pickMedia.launch()
                pickMedia.pickedListener = { uri ->
                    val userId = accountViewModel.currentUser!!.uid
                    val storageRef = FirebaseStorage.getInstance().getReference("profilePictures")
                    val imageRef = storageRef.child("$userId.png")
                    fireBaseStorageHandler.putFile(mainActivity, imageRef, userId, uri)
                    fireBaseStorageHandler.fileStoredListener = {
                        try { // [vbNull]
                            binding.profilePic.setImageURI(uri)
                        } catch (e: Exception) {
                            Log.d("ProfileFragment", "An error occurred on onChangeProfileEvent.")
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun loadProfileBanner() {
        val storageRef = FirebaseStorage.getInstance().getReference("profileBanners")
        val imageRef = storageRef.child("${accountViewModel.selectedUserId}")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            try {
                Glide.with(this)
                    .load(uri)
                    .into(binding.profileBanner)
            } catch (e: Exception) {
                Log.d("ProfileFragment", "An error occurred on loadProfileBanner. (Download url success)")
                e.printStackTrace()
            }
            loadingListener?.invoke(1)
        }.addOnFailureListener {
            Log.d("ProfileFragment", "An error occurred on loadProfileBanner. (Download url failed)")
            it.printStackTrace()
            loadingListener?.invoke(1)
        }
    }

    private fun onChangeBannerEvent() {
        binding.profileBanner.setOnClickListener {
            pickMedia.launch()
            pickMedia.pickedListener = { uri ->
                val userId = accountViewModel.currentUser!!.uid
                val storageRef = FirebaseStorage.getInstance().getReference("profileBanners")
                val imageRef = storageRef.child(userId)
                fireBaseStorageHandler.putFile(mainActivity, imageRef, userId, uri)
                fireBaseStorageHandler.fileStoredListener = {
                    try { // [vbNull]
                        Glide.with(this)
                            .load(uri)
                            .into(binding.profileBanner)
                    } catch (e: Exception) {
                        Log.d("ProfileFragment", "An error occurred on onChangeBannerEvent.")
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun onLogOutEvent() {
        confirmLogoutDialog.title = "Logout Account"
        confirmLogoutDialog.text = "You will lose access to certain features of the app, are you sure?"
        binding.logoutBtn.setOnClickListener {
            if (accountViewModel.currentUser != null) {
                confirmLogoutDialog.show(mainActivity.supportFragmentManager, "ConfirmLogoutDialog")
            }
        }
        confirmLogoutDialog.confirmListener = {
            accountViewModel.currentUser = null
            mainActivity.mAuth.signOut()
            homeViewModel.newsfeedPostAdapterBuffer = null
            homeViewModel.refreshTime = null
            findNavController().navigate(R.id.action_global_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cancelLoadWarning = true
        if (!accountViewModel.viewingItem)
            accountViewModel.selectedUserId = null
    }
}
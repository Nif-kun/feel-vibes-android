package com.example.feelvibes.home.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentProfileBinding
import com.example.feelvibes.dialogs.AuthorizationRequestDialog
import com.example.feelvibes.dialogs.ConfirmationAlertDialog
import com.example.feelvibes.dialogs.SimpleAlertDialog
import com.example.feelvibes.utils.FireBaseStorageHandler
import com.example.feelvibes.utils.PickMedia
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.HomeViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.android.material.tabs.TabLayout
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

    private var username: String? = null
    private var bio: String? = null

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
        buildTabs()
        homeViewModel.layoutState = HomeViewModel.Layouts.PROFILE
        mainActivity.hideToolBar(isHome = true, includeLayout = true)
        pickMedia.setup(this)
        onBackEvent()
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
        mainActivity.checkAuth { currentUser ->
            var userIdEmpty = false
            if (accountViewModel.selectedUserId == null && currentUser == null) {
                userIdEmpty = true
                accountViewModel.currentUser = null
                authorizationRequestDialog.popBack = true
                authorizationRequestDialog.show(mainActivity.supportFragmentManager, "AuthorizationRequestDialog")
            } else if (currentUser != null) {
                if (accountViewModel.selectedUserId == null) {
                    accountViewModel.selectedUserId = currentUser.uid
                    accountViewModel.currentUser = currentUser
                }
                try { // [vbNull]
                    if (accountViewModel.selectedUserId == currentUser.uid) {
                        onEditEvent()
                        onChangeProfileEvent()
                        onChangeBannerEvent()
                        onLogOutEvent()
                    } else {
                        binding.editBtn.visibility = View.GONE
                        binding.logoutBtn.visibility = View.GONE
                        binding.followBtn.visibility = View.VISIBLE
                        onFollowEvent()
                    }
                    updateUsername()
                    updateBio()
                    loadProfilePicture()
                    loadProfileBanner()
                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }
            try { // [vbNull]
                if (!userIdEmpty)
                    onLoadingComplete()
            } catch (e:Exception) {
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
            e.printStackTrace()
        }
    }

    private fun warnLoadError() {
        val dialog = SimpleAlertDialog()
        dialog.title = mainActivity.getString(R.string.timeout_error_title)
        dialog.text = mainActivity.getString(R.string.timeout_error_text)
        dialog.popBack = true
        dialog.show(mainActivity.supportFragmentManager, "LoadErrorDialog")
    }

    private fun onEditEvent() {
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

    }

    private fun onFollowEvent() {
        // Check if user or other
        // then check it if followed or not to change botton chekc state
        binding.followBtn.addOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                button.text = getString(R.string.unfollow)
                button.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.white, null))
            } else {
                button.text = getString(R.string.follow)
                button.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.purple_200, null))
            }
        }
    }

    private fun onBackEvent() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
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
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadProfilePicture() {
        val storageRef = FirebaseStorage.getInstance().getReference("profilePictures")
        val imageRef = storageRef.child("${accountViewModel.currentUser?.uid}.png")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            try { // [vbNull]
                Glide.with(this)
                    .load(uri)
                    .into(binding.profilePic);
            } catch (e: Exception) {
                e.printStackTrace()
            }
            loadingListener?.invoke(1)
        }.addOnFailureListener {
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
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun loadProfileBanner() {
        val storageRef = FirebaseStorage.getInstance().getReference("profileBanners")
        val imageRef = storageRef.child("${accountViewModel.currentUser?.uid}")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            try {
                Glide.with(this)
                    .load(uri)
                    .into(binding.profileBanner)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            loadingListener?.invoke(1)
        }.addOnFailureListener {
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
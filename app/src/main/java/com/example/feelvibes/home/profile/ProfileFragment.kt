package com.example.feelvibes.home.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentProfileBinding
import com.example.feelvibes.layout.AuthorizationRequestLayout
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.HomeViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class ProfileFragment : FragmentBind<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var accountViewModel : AccountViewModel
    private val authorizationRequestLayout = AuthorizationRequestLayout()

    private var username: String? = null
    private var bio: String? = null

    private var loadingListener: ((Int) -> Unit )? = null
    private var loadingProgress = 0
    private val maxLoading = 1 // increase depending on the number of things needed to be loaded

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }

    override fun onReady() {
        homeViewModel.layoutState = HomeViewModel.Layouts.PROFILE
        mainActivity.hideToolBar(isHome = true, includeLayout = true)
        authorizationRequestLayout.setup(this, binding.authorizationReqInclude)

        onBackEvent()
        if (accountViewModel.selectedUserId != null) {
            if (accountViewModel.selectedUserId == accountViewModel.currentUser?.uid) {
                onEditEvent()
                onChangeProfileEvent()
                onChangeBannerEvent()
            } else {
                binding.editBtn.visibility = View.GONE
                binding.followBtn.visibility = View.VISIBLE
                onFollowEvent()
            }
            updateUsername()
            updateBio()
            onLoadingComplete()
        } else {
            findNavController().popBackStack()
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
                warnLoadError()
            }
        }, 60000)
    }

    private fun transitionShimmerOut() {
        binding.displayDataShimmerInclude.displayDataLayout.stopShimmer()
        binding.displayDataShimmerInclude.displayDataLayout.visibility = View.INVISIBLE
        binding.displayDataLayout.visibility = View.VISIBLE
        binding.profilePic.visibility = View.VISIBLE
        binding.profileBanner.visibility = View.VISIBLE
    }

    private fun warnLoadError() {

    }

    private fun onEditEvent() {
        binding.editBtn.setOnClickListener {
            // Show dialog
        }
    }

    private fun onChangeProfileEvent() {
        binding.profilePic.setOnClickListener {

        }
    }

    private fun onChangeBannerEvent() {
        binding.profileBanner.setOnClickListener {

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
        val documentRef = accountViewModel.selectedUserId?.let {
            db.collection("usersDisplayData").document(it)
        }
        if (username != null && username.length > 3) {
            documentRef?.set(mutableMapOf("username" to username), SetOptions.merge())?.addOnSuccessListener {
                binding.usernameView.text = username
            }?.addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(requireActivity(), "Username failed to save...", Toast.LENGTH_SHORT).show()
            }
        } else {
            documentRef?.get()?.addOnSuccessListener { documentSnapshot ->
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
        val documentRef = accountViewModel.selectedUserId?.let {
            db.collection("usersDisplayData").document(it)
        }
        if (bio != null && bio.length > 160) {
            documentRef?.set(mutableMapOf("bio" to bio), SetOptions.merge())?.addOnSuccessListener {
                binding.bioView.text = bio
            }?.addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(requireActivity(), "Bio failed to save...", Toast.LENGTH_SHORT).show()
            }
        } else {
            documentRef?.get()?.addOnSuccessListener { documentSnapshot ->
                try {
                    if (documentSnapshot.exists()) {
                        documentSnapshot.getString("bio")?.let {
                            this.bio = it
                            loadingListener?.invoke(1)
                            binding.bioView.text = it
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun updateProfilePicture() {
        //loadingListener?.invoke(1)
    }

    private fun updateProfileBanner() {
        //loadingListener?.invoke(1)
    }


}
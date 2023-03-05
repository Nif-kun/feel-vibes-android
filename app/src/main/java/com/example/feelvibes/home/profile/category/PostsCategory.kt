package com.example.feelvibes.home.profile.category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPostsCategoryBinding
import com.example.feelvibes.home.profile.recycler.PostRecyclerEvent
import com.example.feelvibes.home.profile.recycler.PostsRecyclerAdapter
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.model.PostModel
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.firebase.firestore.FirebaseFirestore

class PostsCategory : FragmentBind<FragmentPostsCategoryBinding>(FragmentPostsCategoryBinding::inflate), PostRecyclerEvent {

    private lateinit var accountViewModel : AccountViewModel
    private lateinit var libraryViewModel: LibraryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        accountViewModel.viewingItem = false
    }

    override fun onReady() {
        getPostModels {
            updateAdapter(it)
        }
    }

    private fun getPostModels(callback: ((ArrayList<PostModel>)->Unit)) {
        val userId = accountViewModel.selectedUserId
        if (userId != null) {
            queryPosts(userId) { list ->
                val postModels = arrayListOf<PostModel>()
                for (id in list) {
                    postModels.add(PostModel(userId, id))
                }
                callback(postModels)
            }
        } else {
            callback(arrayListOf())
        }
    }

    private fun queryPosts(userId: String, callback: ((List<String>)->Unit)) {
        val docRef = FirebaseFirestore.getInstance().collection("usersPost").document(userId)
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val fieldNames = documentSnapshot.data?.keys?.toList() ?: emptyList()
                callback(fieldNames)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(listOf())
            }
    }

    private fun updateAdapter(postModels: ArrayList<PostModel>) {
        binding.postsRecyclerView.adapter = PostsRecyclerAdapter(
            requireActivity(),
            this,
            postModels
        )
    }

    override fun onMusicClick(
        url: String,
        track: String?,
        title: String,
        duration: String,
        artist: String?,
        album: String?,
        genre: String?
    ) {
        val tempPlaylist = PlaylistModel("$title ($duration)")
        tempPlaylist.add(MusicModel(url, track, title, duration, artist, album, genre))
        libraryViewModel.selectedPlaylist = tempPlaylist
        accountViewModel.viewingItem = true
        findNavController().navigate(R.id.action_global_playerFragment)
    }

    override fun onDesignClick(name: String, author: String, foregroundUrl: String?, backgroundUrl: String?) {

    }

    override fun onChordsClick(name: String, author: String, chords: String?) {

    }

    override fun onLyricsClick(name: String, author: String, lyrics: String?) {

    }

}
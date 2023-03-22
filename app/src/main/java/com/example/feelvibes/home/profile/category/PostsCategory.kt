package com.example.feelvibes.home.profile.category

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.create.CreateFragment
import com.example.feelvibes.databinding.FragmentPostsCategoryBinding
import com.example.feelvibes.dialogs.CommentsDialog
import com.example.feelvibes.dialogs.ConfirmationAlertDialog
import com.example.feelvibes.home.recycler.PostRecyclerEvent
import com.example.feelvibes.home.recycler.PostsRecyclerAdapter
import com.example.feelvibes.model.*
import com.example.feelvibes.utils.ExternalStorageHandler
import com.example.feelvibes.utils.FVFireStoreHandler
import com.example.feelvibes.utils.FireBaseStorageHandler
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PostsCategory : FragmentBind<FragmentPostsCategoryBinding>(FragmentPostsCategoryBinding::inflate),
    PostRecyclerEvent {

    private lateinit var accountViewModel : AccountViewModel
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var createViewModel: CreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
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
        try {
            binding.postsRecyclerView.adapter = PostsRecyclerAdapter(
                requireActivity(),
                this,
                postModels,
                accountViewModel.currentUser?.uid,
                createViewModel
            )
        } catch (e:Exception) {
            e.printStackTrace()
        }

    }

    override fun onUserClick(userId: String) {
        /* TODO | Disabling this for now because of this reason:
            when checking a profile inside of profile,
            going back gives the same user recently opened since the previous ones were not saved
        accountViewModel.selectedUserId = userId
        accountViewModel.currentNavStackId = R.id.action_global_profileFragment
        findNavController().navigate(R.id.action_global_profileFragment)
        */
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

    override fun onMusicDownload(url: String, title: String) {
        ExternalStorageHandler.downloadAudioFile(requireActivity(), url, title)
        Toast.makeText(requireContext(), "Starting download...", Toast.LENGTH_SHORT).show()
    }

    override fun onDesignClick(id: String, name: String, foregroundUrl: String?, backgroundUrl: String?) {
        val safeForegroundUrl = foregroundUrl ?: ""
        val safeBackgroundUrl = backgroundUrl ?: ""
        val designModel = DesignModel(id, name, "#FFFFFF", safeBackgroundUrl, safeForegroundUrl)
        createViewModel.selectedDesignModel = designModel
        accountViewModel.viewingItem = true
        findNavController().navigate(R.id.action_global_designViewerFragment)
    }

    override fun onDesignDownload(designModel: DesignModel) {
        designModel.saveImagesToInternal(requireActivity(), true)
        if (createViewModel.designCollection.isEmpty())
            createViewModel.designCollection.populateFromStored(requireActivity())
        if (!createViewModel.designCollection.has(designModel)) {
            createViewModel.designCollection.add(designModel)
            createViewModel.designCollection.saveToStored(requireActivity())
            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onChordsClick(id: String, name: String, chords: String?) {
        val textModel = TextModel(id, name, chords ?: "Error: text is missing!")
        createViewModel.selectedTextModel = textModel
        createViewModel.currentCreateTab = CreateFragment.CHORDS
        accountViewModel.viewingItem = true
        findNavController().navigate(R.id.action_global_textViewerFragment)
    }

    override fun onChordsDownload(textModel: TextModel) {
        if (createViewModel.chordsCollection.isEmpty())
            createViewModel.chordsCollection.populateFromStored(requireActivity())
        if (!createViewModel.chordsCollection.has(textModel)) {
            createViewModel.chordsCollection.add(textModel)
            createViewModel.chordsCollection.saveToStored(requireActivity())
            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLyricsClick(id: String, name: String, lyrics: String?) {
        val textModel = TextModel(id, name, lyrics ?: "Error: text is missing!")
        createViewModel.selectedTextModel = textModel
        createViewModel.currentCreateTab = CreateFragment.LYRICS
        accountViewModel.viewingItem = true
        findNavController().navigate(R.id.action_global_textViewerFragment)
    }

    override fun onLyricsDownload(textModel: TextModel) {
        if (createViewModel.lyricsCollection.isEmpty())
            createViewModel.lyricsCollection.populateFromStored(requireActivity())
        if (!createViewModel.lyricsCollection.has(textModel)) {
            createViewModel.lyricsCollection.add(textModel)
            createViewModel.lyricsCollection.saveToStored(requireActivity())
            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeletePostClick(userId: String, postId: String, postFolderId: String) {
        // Do delete here.
        val deleteDialog = ConfirmationAlertDialog()
        deleteDialog.title = "Remove Post"
        deleteDialog.text = "You are about to delete your post, are you sure?"
        deleteDialog.confirmListener = {
            val folderPath = "userPostFiles/$userId/$postFolderId"
            FireBaseStorageHandler.deleteFolder(FirebaseStorage.getInstance(), folderPath) { deleted, folderDeleteException ->
                if (deleted) {
                    Log.d("PostDelete", "Folder has been deleted!")
                } else {
                    Log.d("PostDelete", "Folder deletion failed! (Ignore if success occurred first.)")
                    folderDeleteException?.printStackTrace()
                }
            }
            FVFireStoreHandler.deletePost(userId, postId) { success, postDeleteException ->
                if (success) {
                    getPostModels {
                        updateAdapter(it)
                    }
                    Toast.makeText(requireContext(), "Post has been deleted!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("PostDelete", "Post deletion failed!")
                    postDeleteException?.printStackTrace()
                }
            }
        }
        deleteDialog.show(requireActivity().supportFragmentManager, "DeletePostConfirmation")
    }

    override fun onCommentClick(
        ownerId: String,
        postId: String,
        userId: String?,
        comments: HashMap<*, *>?
    ) {
        val commentsDialog = CommentsDialog(ownerId, postId, userId, comments)
        commentsDialog.onQueryListener = { // Occurs when a post comments have been modified
            onQueueRefresh()
        }
        commentsDialog.show(requireActivity().supportFragmentManager, "CommentsDialog")
    }

    override fun onQueueRefresh() {
        getPostModels {
            updateAdapter(it)
        }
    }

}
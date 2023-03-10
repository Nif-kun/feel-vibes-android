package com.example.feelvibes.home

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.feelvibes.R
import com.example.feelvibes.create.CreateFragment
import com.example.feelvibes.databinding.FragmentHomeBinding
import com.example.feelvibes.dialogs.*
import com.example.feelvibes.home.recycler.PostRecyclerEvent
import com.example.feelvibes.home.recycler.PostsRecyclerAdapter
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.recycler.adapters.PlaylistRecyclerAdapter
import com.example.feelvibes.model.*
import com.example.feelvibes.utils.ExternalStorageHandler
import com.example.feelvibes.utils.FVFireStoreHandler
import com.example.feelvibes.utils.FireBaseStorageHandler
import com.example.feelvibes.utils.ShortLib
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.HomeViewModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : FragmentBind<FragmentHomeBinding>(FragmentHomeBinding::inflate), PostRecyclerEvent {

    // Note:
    //  -[vbNull]: A NullPointerException occurs on VB due to spamming of page. Specifically load delay.

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var accountViewModel : AccountViewModel
    private lateinit var libraryViewModel : LibraryViewModel
    private lateinit var createViewModel : CreateViewModel

    private var musicItem: MusicModel? = null
    private var designItem: DesignModel? = null
    private var chordsItem: TextModel? = null
    private var lyricsItem: TextModel? = null

    private var tempMusicUri: Uri? = null
    private var tempDesignForegroundUri: Uri? = null
    private var tempDesignBackgroundUri: Uri? = null

    private var inputBuffer: CharSequence = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
    }

    override fun onReady() {
        homeViewModel.layoutState = HomeViewModel.Layouts.HOME
        mainActivity.renameToolBar("Home")
        mainActivity.unpadMainView()
        mainActivity.showToolBar(isHome = true)
        onProfileClickedEvent()
        showAppBarLayout()
        queryNewsfeed {
            if (it != null)
                updateAdapter(it)
        }
    }

    private fun showAppBarLayout() {
        mainActivity.checkAuth { user ->
            if (user != null) {
                try { // [vbNull]
                    accountViewModel.currentUser = user
                    binding.appBarLayout.visibility = View.VISIBLE
                    setupFollowingRecyclerAdapter(user)
                    loadProfilePicture(user)
                    onPostInputEvent()
                    onAddMusicEvent()
                    onAddDesignEvent()
                    onAddChordsEvent()
                    onAddLyricsEvent()
                    onPostEvent(user)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupFollowingRecyclerAdapter(user: FirebaseUser) {
        // Don't show if none else show
    }

    // Edit this
    private fun updateAdapter(postModels: ArrayList<PostModel>) {
        binding.postRecyclerView.adapter = PostsRecyclerAdapter(
            requireActivity(),
            this,
            postModels,
            accountViewModel.currentUser?.uid,
            createViewModel
        )
    }

    private fun queryNewsfeed(callback: (ArrayList<PostModel>?) -> Unit) {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = currentDateTime.format(formatter)
        val documentId = formatted.replace("-", "")
        val postRef = FirebaseFirestore.getInstance().collection("newsfeed").document(documentId)
        postRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val postModels = arrayListOf<PostModel>()
                val keys = documentSnapshot.data?.keys
                if (keys != null) {
                    for (userId in keys) {
                        val postIds = documentSnapshot[userId]
                        if (postIds is ArrayList<*>) {
                            for (id in postIds) {
                                if (id is String) {
                                    postModels.add(PostModel(userId, id))
                                }
                            }
                        }
                    }
                }
                postModels.reverse()
                callback(postModels)
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            it.printStackTrace()
            callback(null)
        }
    }

    private fun onPostInputEvent() {
        binding.postInput.setOnFocusChangeListener { _, hasFocus ->
            val mParams = binding.postInput.layoutParams as LinearLayout.LayoutParams
            if (hasFocus) {
                mParams.apply { height = ShortLib.dpToPx(200f, mainActivity).toInt() }
                binding.postInput.layoutParams = mParams
                binding.postInput.gravity = Gravity.START
            } else {
                val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.postInput.windowToken, 0)
                if (binding.postInput.lineCount > 3) {
                    mParams.apply { height = ShortLib.dpToPx(200f, mainActivity).toInt() }
                } else if (binding.postInput.lineCount > 1) {
                    mParams.apply { height = ShortLib.dpToPx((50f * binding.postInput.lineCount), mainActivity).toInt() }
                } else {
                    mParams.apply { height = ShortLib.dpToPx(50f, mainActivity).toInt() }
                    binding.postInput.gravity = Gravity.CENTER_VERTICAL
                }
                binding.postInput.layoutParams = mParams
                if (binding.postInput.text.isNullOrBlank()) {
                    binding.postInput.setText("")
                }
            }
        }
        binding.postInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrBlank()) {
                    if (binding.postInput.lineCount > 10) {
                        binding.postInput.removeTextChangedListener(this)
                        binding.postInput.setText(inputBuffer)
                        binding.postInput.addTextChangedListener(this)
                        binding.postInput.setSelection(binding.postInput.text.length)
                    } else {
                        inputBuffer = binding.postInput.text.toString()
                    }
                    binding.postButton.visibility = View.VISIBLE
                }
                if (!hasContent()) {
                    binding.postButton.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used in this example
            }
        })
    }

    private fun onPostEvent(user: FirebaseUser) {
        binding.postButton.setOnClickListener {
            if (hasContent()) {
                Toast.makeText(mainActivity, "Uploading...", Toast.LENGTH_SHORT).show()
                posting(true)
                // If has music or design, then upload first
                // Check if success or not, if not, don't post in FireStore
                val userId = user.uid
                val userRef = FirebaseFirestore.getInstance().collection("usersPost").document(userId)
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm:ss")
                val formatted = currentDateTime.format(formatter)
                val sanitizedFormatted = formatted.replace("-", "").replace(":", "").replace("/", "")
                val postId = sanitizedFormatted+userId
                val contentData = mutableMapOf<String, Any>()
                val postData = mutableMapOf<String, Any>(postId to contentData)

                contentData["time"] = formatted
                if (binding.postInput.text.isNotEmpty())
                    contentData["text"] = binding.postInput.text.toString()
                if (chordsItem != null)
                    contentData["chords"] = hashMapOf("name" to chordsItem!!.name, "text" to chordsItem!!.text)
                if (lyricsItem != null)
                    contentData["lyrics"] = hashMapOf("name" to lyricsItem!!.name, "text" to lyricsItem!!.text)

                if (musicItem != null || designItem != null) {
                    fileUpload(userId, sanitizedFormatted) { success ->
                        if (success == true) {
                            if (tempMusicUri != null) {
                                contentData["music"] = hashMapOf(
                                    "url" to tempMusicUri.toString(),
                                    "track" to musicItem!!.track,
                                    "title" to musicItem!!.title,
                                    "duration" to musicItem!!.duration,
                                    "artist" to musicItem!!.artist,
                                    "album" to musicItem!!.album,
                                    "genre" to musicItem!!.genre
                                )
                            }
                            if (designItem != null) {
                                val designData = mutableMapOf<String, String>()
                                designData["name"] = designItem!!.name
                                if (tempDesignForegroundUri != null)
                                    designData["foreground"] = tempDesignForegroundUri.toString()
                                if (tempDesignBackgroundUri != null)
                                    designData["background"] = tempDesignBackgroundUri.toString()
                                contentData["design"] = designData
                            }

                            // Upload file
                            userRef.set(postData, SetOptions.merge()).addOnCompleteListener {
                                newsfeedUpload(userId, postId)
                                Toast.makeText(mainActivity, "Posted!", Toast.LENGTH_SHORT).show()
                                tempMusicUri = null
                                tempDesignForegroundUri = null
                                tempDesignBackgroundUri = null
                                resetPostViews()
                                posting(false)
                            }.addOnFailureListener {
                                warnLoadError()
                                tempMusicUri = null
                                tempDesignForegroundUri = null
                                tempDesignBackgroundUri = null
                                posting(false)
                            }

                        } else {
                            warnLoadError()
                            tempMusicUri = null
                            tempDesignForegroundUri = null
                            tempDesignBackgroundUri = null
                            posting(false)
                        }
                    }
                } else {
                    userRef.set(postData, SetOptions.merge()).addOnCompleteListener {
                        newsfeedUpload(userId, postId)
                        Toast.makeText(mainActivity, "Posted!", Toast.LENGTH_SHORT).show()
                        tempMusicUri = null
                        tempDesignForegroundUri = null
                        tempDesignBackgroundUri = null
                        resetPostViews()
                        posting(false)
                    }.addOnFailureListener {
                        tempMusicUri = null
                        tempDesignForegroundUri = null
                        tempDesignBackgroundUri = null
                        posting(false)
                    }
                }
            }
        }
    }

    private fun fileUpload(userId:String, sanitizedFormatted:String, callback:(Boolean?)->Unit) {
        var progress = 0
        var maxProgress = 0
        // maxProgress is setup like this because a lot of code below is asynchronous. I'd rather not risk it.
        if (musicItem != null)
            maxProgress++
        if (designItem?.foregroundImagePath?.isNotEmpty() == true)
            maxProgress++
        if (designItem?.backgroundImagePath?.isNotEmpty() == true)
            maxProgress++

        if (musicItem != null || designItem != null) {
            val storageRef = FirebaseStorage.getInstance().getReference("userPostFiles")

            musicItem?.let { model ->
                val musicUri = Uri.fromFile(File(model.path))
                val fileRef = storageRef.child("$userId/$sanitizedFormatted/${musicUri.lastPathSegment!!}")
                FireBaseStorageHandler.putFile(fileRef, musicUri) { success ->
                    if (success) {
                        FireBaseStorageHandler.getDownloadUrl(fileRef) { uri ->
                            if (uri != null) {
                                progress++
                                tempMusicUri = uri
                                if (progress >= maxProgress)
                                    callback(true)
                            } else {
                                callback(false)
                            }
                        }
                    } else {
                        callback(false)
                    }
                }
            }

            designItem?.let { model ->
                if (!model.getForegroundPath(mainActivity).isNullOrBlank()) {
                    val foregroundUri = Uri.fromFile(File(model.getForegroundPath(mainActivity)!!))
                    val fileRef = storageRef.child("$userId/$sanitizedFormatted/${foregroundUri.lastPathSegment!!}")
                    FireBaseStorageHandler.putFile(fileRef, foregroundUri) { success ->
                        if (success) {
                            FireBaseStorageHandler.getDownloadUrl(fileRef) { uri ->
                                if (uri != null) {
                                    progress++
                                    tempDesignForegroundUri = uri
                                    if (progress >= maxProgress)
                                        callback(true)
                                } else {
                                    callback(false)
                                }
                            }
                        } else {
                            callback(false)
                        }
                    }
                }
                if (!model.getBackgroundPath(mainActivity).isNullOrBlank()) {
                    val backgroundUri = Uri.fromFile(File(model.getBackgroundPath(mainActivity)!!))
                    val fileRef = storageRef.child("$userId/$sanitizedFormatted/${backgroundUri.lastPathSegment!!}")
                    FireBaseStorageHandler.putFile(fileRef, backgroundUri) { success ->
                        if (success) {
                            FireBaseStorageHandler.getDownloadUrl(fileRef) { uri ->
                                if (uri != null) {
                                    progress++
                                    tempDesignBackgroundUri = uri
                                    if (progress >= maxProgress)
                                        callback(true)
                                } else {
                                    callback(false)
                                }
                            }
                        } else {
                            callback(false)
                        }
                    }
                }
            }
        }
        if (progress != maxProgress) {
            try { // [vbNull]
                binding.root.postDelayed({
                    if (progress < maxProgress) {
                        callback(false)
                    }
                }, 120000) // 2Min (120K)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            callback(null)
        }

    }

    private fun newsfeedUpload(userId: String, postId: String) {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = currentDateTime.format(formatter)
        val sanitizedFormatted = formatted.replace("-", "")
        val newsfeedRef = FirebaseFirestore.getInstance().collection("newsfeed").document(sanitizedFormatted)
        val postList = arrayListOf<String>()
        newsfeedRef.get().addOnSuccessListener { documentSnapshot ->
            val oldList = documentSnapshot.get(userId)
            if (oldList is ArrayList<*>) {
                postList.addAll(oldList.filterIsInstance<String>())
                postList.add(postId)
                newsfeedRef.set(hashMapOf(userId to postList), SetOptions.merge()).addOnFailureListener {
                    it.printStackTrace()
                }
            } else {
                postList.add(postId)
                newsfeedRef.set(hashMapOf(userId to postList), SetOptions.merge()).addOnFailureListener {
                    it.printStackTrace()
                }
            }

            // update adapter
            queryNewsfeed {
                if (it != null)
                    updateAdapter(it)
            }
        }.addOnFailureListener {
            it.printStackTrace()
            postList.add(postId)
            newsfeedRef.set(hashMapOf(userId to postList), SetOptions.merge()).addOnFailureListener { e ->
                e.printStackTrace()
            }
        }
    }

    private fun warnLoadError() {
        val dialog = SimpleAlertDialog()
        dialog.title = mainActivity.getString(R.string.timeout_error_title)
        dialog.text = mainActivity.getString(R.string.timeout_error_text_a)
        dialog.show(mainActivity.supportFragmentManager, "LoadErrorDialog")
    }

    private fun resetPostViews() {
        musicItem = null
        designItem = null
        chordsItem = null
        lyricsItem = null
        val untitled = resources.getString(R.string.untitled)
        binding.postInput.text.clear()
        binding.postInput.clearFocus()
        binding.musicItem.visibility = View.GONE
        binding.musicTitle.text = untitled
        binding.designItem.visibility = View.GONE
        binding.designTitle.text = untitled
        binding.chordsItem.visibility = View.GONE
        binding.chordsTitle.text = untitled
        binding.lyricsItem.visibility = View.GONE
        binding.lyricsTitle.text = untitled
        if (binding.postButton.visibility == View.VISIBLE && !hasContent())
            binding.postButton.visibility = View.GONE
    }

    private fun onAddMusicEvent() {
        binding.musicButton.setOnClickListener {
            val dialog = MusicItemSelectionDialog()
            dialog.show(mainActivity.supportFragmentManager, "MusicItemSelectionDialog")
            dialog.selectedListener = { musicModel ->  
                binding.musicItem.visibility = View.VISIBLE
                binding.musicTitle.text = musicModel.title
                musicItem = musicModel
                if (binding.postButton.visibility == View.GONE)
                    binding.postButton.visibility = View.VISIBLE
            }
        }
        binding.musicRemove.setOnClickListener {
            binding.musicItem.visibility = View.GONE
            binding.musicTitle.text = resources.getString(R.string.untitled)
            musicItem = null
            if (binding.postButton.visibility == View.VISIBLE && !hasContent())
                binding.postButton.visibility = View.GONE
        }
    }

    private fun onAddDesignEvent() {
        binding.designButton.setOnClickListener {
            val dialog = DesignItemSelectionDialog()
            dialog.show(mainActivity.supportFragmentManager, "DesignItemSelectionDialog")
            dialog.selectedListener = { projectModel ->
                binding.designItem.visibility = View.VISIBLE
                binding.designTitle.text = projectModel.name
                designItem = projectModel as DesignModel
                if (binding.postButton.visibility == View.GONE)
                    binding.postButton.visibility = View.VISIBLE
            }
        }
        binding.designRemove.setOnClickListener {
            binding.designItem.visibility = View.GONE
            binding.designTitle.text = resources.getString(R.string.untitled)
            designItem = null
            if (binding.postButton.visibility == View.VISIBLE && !hasContent())
                binding.postButton.visibility = View.GONE
        }
    }

    private fun onAddChordsEvent() {
        binding.chordsButton.setOnClickListener {
            val dialog = ChordsItemSelectionDialog()
            dialog.show(mainActivity.supportFragmentManager, "ChordsItemSelectionDialog")
            dialog.selectedListener = { projectModel ->
                binding.chordsItem.visibility = View.VISIBLE
                binding.chordsTitle.text = projectModel.name
                chordsItem = projectModel as TextModel
                if (binding.postButton.visibility == View.GONE)
                    binding.postButton.visibility = View.VISIBLE
            }
        }
        binding.chordsRemove.setOnClickListener {
            binding.chordsItem.visibility = View.GONE
            binding.chordsTitle.text = resources.getString(R.string.untitled)
            chordsItem = null
            if (binding.postButton.visibility == View.VISIBLE && !hasContent())
                binding.postButton.visibility = View.GONE
        }
    }

    private fun onAddLyricsEvent() {
        binding.lyricsButton.setOnClickListener {
            val dialog = LyricsItemSelectionDialog()
            dialog.show(mainActivity.supportFragmentManager, "LyricsItemSelectionDialog")
            dialog.selectedListener = { projectModel ->
                binding.lyricsItem.visibility = View.VISIBLE
                binding.lyricsTitle.text = projectModel.name
                lyricsItem = projectModel as TextModel
                if (binding.postButton.visibility == View.GONE)
                    binding.postButton.visibility = View.VISIBLE
            }
        }
        binding.lyricsRemove.setOnClickListener {
            binding.lyricsItem.visibility = View.GONE
            binding.lyricsTitle.text = resources.getString(R.string.untitled)
            lyricsItem = null
            if (binding.postButton.visibility == View.VISIBLE && !hasContent())
                binding.postButton.visibility = View.GONE
        }
    }

    private fun loadProfilePicture(user: FirebaseUser) {
        val storageRef = FirebaseStorage.getInstance().getReference("profilePictures")
        val imageRef = storageRef.child("${user.uid}.png")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            try { // [vbNull]
                Glide.with(this)
                    .load(uri)
                    .into(binding.profilePic);
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    private fun onProfileClickedEvent() {
        mainActivity.profileClickedListener = {
            accountViewModel.selectedUserId = accountViewModel.currentUser?.uid
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun hasContent(): Boolean {
        val hasText = binding.postInput.text.isNotEmpty()
        return hasText || musicItem != null || designItem != null || chordsItem != null || lyricsItem != null
    }

    override fun onUserClick(userId: String) {
        accountViewModel.selectedUserId = userId
        findNavController().navigate(R.id.action_global_profileFragment)
    }

    private fun posting(isPosting: Boolean) {
        if (isPosting) {
            binding.postButton.isEnabled = false
            binding.postInput.isEnabled = false
            binding.musicRemove.visibility = View.GONE
            binding.designRemove.visibility = View.GONE
            binding.chordsRemove.visibility = View.GONE
            binding.lyricsRemove.visibility = View.GONE
        } else {
            binding.postButton.isEnabled = true
            binding.postInput.isEnabled = true
            binding.musicRemove.visibility = View.VISIBLE
            binding.designRemove.visibility = View.VISIBLE
            binding.chordsRemove.visibility = View.VISIBLE
            binding.lyricsRemove.visibility = View.VISIBLE
        }
    }

    /// --- Damn, I hate how long this is... but not as long as my diiiick ayyyyyeeee... Fuck I'm tired.
    // This whole below is a mess since it's a copy paste from PostCategory of profile. It should've been modular.
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
                    FVFireStoreHandler.deleteNewsfeed(userId, postId){ deleted, exception ->
                        if (deleted) {
                            queryNewsfeed {
                                if (it != null)
                                    updateAdapter(it)
                            }
                        } else
                            exception?.printStackTrace()
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

}
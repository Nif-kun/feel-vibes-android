package com.example.feelvibes.library

import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.example.feelvibes.R
import com.example.feelvibes.databinding.CreatePlaylistDialogBinding
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentDialogBind

class LibraryCreatePlaylistDialog :
    FragmentDialogBind<CreatePlaylistDialogBinding>(CreatePlaylistDialogBinding::inflate) {

    private var previousName : String? = null

    object WarnState {
        const val DEFAULT = 0
        const val EMPTY_TITLE = 1
        const val SAME_TITLE = 2
    }

    private lateinit var libraryViewModel : LibraryViewModel
    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>
    private var selectImageClicked = false
    private var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        setupPickMedia()
    }

    override fun onReady() {
        binding.createPlaylistTopRow.visibility = View.GONE // Disabling cover art for now until [setupPickMedia : A1] is resolved.
        hideImageView()
        hideWarning()
        dialogTitle(libraryViewModel.selectedPlaylist == null) // Should occur when Edit in LibraryBottomSheet is clicked
        populateInputs()
        setupDialogButtons()
    }

    fun dialogTitle(isCreate: Boolean = true) {
        if (isCreate) {
            binding.createPlaylistDialogTitle.setText(R.string.create_playlist)
        } else {
            binding.createPlaylistDialogTitle.setText(R.string.edit_playlist)
        }
    }

    private fun setupPickMedia() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            imageUri = if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                if (selectImageClicked) {
                    binding.createPlaylistImageView.setImageURI(uri)

                    // TODO [setupPickMedia : A1]
                    //  Need to find the real uri path to the file fkin damnit

                    showImageView()
                    selectImageClicked = false
                }
                uri
            } else {
                Log.d("PhotoPicker", "No media selected")
                null
            }
        }
    }

    private fun setupDialogButtons() {
        setupSelectImageButton()
        setupCancelButton()
        setupConfirmButton()
    }

    fun showImageView() {
        binding.createPlaylistImageView.visibility = View.VISIBLE
    }

    fun hideImageView() {
        binding.createPlaylistImageView.visibility = View.GONE
    }

    fun showWarning() {
        binding.createPlaylistWarning.visibility = View.VISIBLE
    }

    fun hideWarning() {
        binding.createPlaylistWarning.visibility = View.GONE
    }

    fun warningState(state : Int) {
        when(state) {
            WarnState.EMPTY_TITLE -> {
                showWarning()
                binding.createPlaylistWarning.text = resources.getText(R.string.playlist_requires_a_title)
            }
            WarnState.SAME_TITLE -> {
                showWarning()
                binding.createPlaylistWarning.text = resources.getText(R.string.playlist_title_already_taken)
            }
            else -> {
                hideWarning()
            }
        }
    }

    private fun setupSelectImageButton() {
        // check if media access is allowed, ask if not.
        // if allowed, open up gallery to select an image
        // once selected, call showImageView()

        binding.createPlaylistImageBtn.setOnClickListener {
            if (ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable()) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                selectImageClicked = true
            }
        }
    }

    private fun setupCancelButton() {
        binding.createPlaylistCancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setupConfirmButton() {
        binding.createPlaylistConfirmBtn.setOnClickListener {

            val title = binding.createPlaylistEditText.text.toString()
            val playlistTitles = ArrayList<String>()
            // Populates playlistTitles
            for (playlist in libraryViewModel.customCollection.list) {
                if (previousName?.equals(playlist.name, true) == true) { // Skips already set name.
                    continue
                } else {
                    playlistTitles.add(playlist.name.lowercase())
                }
            }
            val thumbnailBitmap : Bitmap? = if (binding.createPlaylistImageView.visibility == View.VISIBLE) {
                binding.createPlaylistImageView.drawable.toBitmap(480, 480)
            } else {
                null
            }

            if (title.isEmpty()) {
                warningState(WarnState.EMPTY_TITLE)
            } else if (playlistTitles.contains(title.lowercase())) {
                warningState(WarnState.SAME_TITLE)
            } else {
                if (libraryViewModel.selectedPlaylist == null) {
                    confirmCreate(title, thumbnailBitmap)
                } else {
                    confirmEdit(title, thumbnailBitmap)
                }
            }
        }
    }

    private fun populateInputs() {
        if (libraryViewModel.selectedPlaylist != null) {
            previousName = libraryViewModel.selectedPlaylist?.name
            binding.createPlaylistEditText.setText(previousName)
        }
    }

    private fun confirmCreate(title : String, thumbnailBitmap : Bitmap?) {
        warningState(WarnState.DEFAULT)
        val incremented = libraryViewModel.customCollection.add(PlaylistModel(
            title,
            PlaylistModel.Type.PLAYLIST,
            thumbnailBitmap,
            ArrayList(),
            null,
            imageUri,
        ))
        if (incremented) {
            libraryViewModel.selectedAdapter?.notifyItemInserted(libraryViewModel.customCollection.list.size-1)
            libraryViewModel.customCollection.saveToStored(
                requireActivity(),
                arrayListOf(resources.getString(R.string.favorites), resources.getString(R.string.create_playlist)))
        }
        dismiss()
    }

    private fun confirmEdit(title : String, thumbnailBitmap: Bitmap?) {
        libraryViewModel.selectedPlaylist?.name = title
        libraryViewModel.selectedPlaylist?.thumbnail = thumbnailBitmap
        libraryViewModel.customCollection.saveToStored(
            requireActivity(),
            arrayListOf(resources.getString(R.string.favorites), resources.getString(R.string.create_playlist)))
        if (libraryViewModel.selectedAdapterPos > -1) {
            libraryViewModel.selectedAdapter?.notifyItemChanged(libraryViewModel.selectedAdapterPos)
        }
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        libraryViewModel.selectedAdapter = null
        libraryViewModel.selectedAdapterPos = -1
        libraryViewModel.selectedPlaylist = null
    }

}
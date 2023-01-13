package com.example.feelvibes.library

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import com.example.feelvibes.R
import com.example.feelvibes.databinding.CreatePlaylistDialogBinding
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentDialogBind

class LibraryCreatePlaylistDialog :
    FragmentDialogBind<CreatePlaylistDialogBinding>(CreatePlaylistDialogBinding::inflate) {


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
        setupDialogButtons()
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
            val thumbnailBitmap : Bitmap? = if (binding.createPlaylistImageView.visibility == View.VISIBLE) {
                binding.createPlaylistImageView.drawable.toBitmap(480, 480)
            } else {
                null
            }
            libraryViewModel.customCollection.list.forEach { playlistTitles.add(it.name) }
            if (title.isEmpty()) {
                warningState(WarnState.EMPTY_TITLE)
            } else if (playlistTitles.contains(title)) {
                warningState(WarnState.SAME_TITLE)
            } else {
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
        }
    }



}
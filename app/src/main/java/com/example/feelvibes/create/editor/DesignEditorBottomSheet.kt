package com.example.feelvibes.create.editor

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentDesignEditorBottomSheetBinding
import com.example.feelvibes.dialogs.DualSelectionDialog
import com.example.feelvibes.dialogs.PresetSelectionDialog
import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.utils.InternalStorageHandler
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind
import pl.droidsonroids.gif.GifDrawable

class DesignEditorBottomSheet : FragmentBottomSheetDialogBind<FragmentDesignEditorBottomSheetBinding>(
    FragmentDesignEditorBottomSheetBinding::inflate
) {

    object ImageSelected{
        const val NONE = 0
        const val BACKGROUND = 1
        const val FOREGROUND = 2
    }

    object ImageType {
        const val NONE = "NULL"
        const val JPEG = "jpg"
        const val PNG = "png"
        const val GIF = "gif"
    }

    private lateinit var createViewModel: CreateViewModel
    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>
    private var imageSelected = ImageSelected.NONE
    private var imageUri : Uri? = null
    private var backgroundImageType = ImageType.NONE
    private var foregroundImageType = ImageType.NONE
    private var backgroundImageUri: Uri? = null
    private var foregroundImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
        setupPickMedia()
    }

    override fun onReady() {
        setupTitleInput()
        onDrawerButtonEvent()
        onSelectBackgroundEvent()
        onSelectForegroundEvent()
        onSaveEvent()
    }

    fun getImageType(mimeType: String): String {
        if (mimeType.contains("jpg", true) || mimeType.contains("jpeg", true)) {
            Log.d("PhotoPicker", "Image type set to JPEG")
            return ImageType.JPEG
        } else if (mimeType.contains("png", true)) {
            Log.d("PhotoPicker", "Image type set to PNG")
            return ImageType.PNG
        } else if (mimeType.contains("gif", true)) {
            Log.d("PhotoPicker", "Image type set to GIF")
            return ImageType.GIF
        }
        return ImageType.PNG
    }

    private fun setupPickMedia() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            imageUri = if (uri != null) {
                applySelectedImage(uri)
                uri
            } else {
                Log.d("PhotoPicker", "No media selected")
                null
            }
        }
    }

    private fun applySelectedImage(uri: Uri, forceType: String? = null) {
        Log.d("PhotoPicker", "Selected URI: $uri")
        val contentResolver = mainActivity.contentResolver
        val mimeType = contentResolver?.getType(uri)
        val imageType = mimeType?.let { getImageType(it) } ?: forceType ?: ImageType.PNG
        Log.d("PhotoPicker", "Extension: $mimeType")

        when (imageSelected) {
            ImageSelected.BACKGROUND -> {
                if (imageType.equals(ImageType.GIF, true)) {
                    val gifFromUri = GifDrawable(contentResolver, uri)
                    createViewModel.selectedBackgroundBitmap = null
                    createViewModel.selectedBackgroundDrawable = gifFromUri
                    backgroundImageUri = uri
                    Log.d("PhotoPicker", "Saving image as GIF")
                } else {
                    val source = ImageDecoder.createSource(mainActivity.contentResolver, uri)
                    createViewModel.selectedBackgroundDrawable = null
                    backgroundImageUri = null
                    createViewModel.selectedBackgroundBitmap = ImageDecoder.decodeBitmap(source)
                    Log.d("PhotoPicker", "Saving image as JPEG/PNG")
                }
                backgroundImageType = imageType
            }
            ImageSelected.FOREGROUND -> {
                if (imageType.equals(ImageType.GIF, true)) {
                    val gifFromUri = GifDrawable(contentResolver, uri)
                    createViewModel.selectedForegroundBitmap = null
                    createViewModel.selectedForegroundDrawable = gifFromUri
                    foregroundImageUri = uri
                    Log.d("PhotoPicker", "Saving image as GIF")
                } else {
                    val source = ImageDecoder.createSource(mainActivity.contentResolver, uri)
                    createViewModel.selectedForegroundDrawable = null
                    foregroundImageUri = null
                    createViewModel.selectedForegroundBitmap = ImageDecoder.decodeBitmap(source)
                    Log.d("PhotoPicker", "Saving image as JPEG/PNG")
                }
                foregroundImageType = imageType
            }
        }
        imageSelected = ImageSelected.NONE
    }

    private fun setupTitleInput() {
        if (createViewModel.selectedDesignModel != null) {
            binding.titleInput.setText(createViewModel.selectedDesignModel!!.name)
        }
    }

    private fun onDrawerButtonEvent() {
        binding.drawerButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onSelectBackgroundEvent() {
        binding.selectBackgroundBtn.setOnClickListener {
            val dualSelectionDialog = DualSelectionDialog()
            dualSelectionDialog.title = "Select Background"
            dualSelectionDialog.selectionLabelA = "Preset"
            dualSelectionDialog.selectionLabelB = "Gallery"
            dualSelectionDialog.onSelectionListener = { selection ->
                when (selection) {
                    1 -> {
                        imageSelected = ImageSelected.BACKGROUND
                        val presetSelectionDialog = PresetSelectionDialog()
                        val packageName = mainActivity.packageName
                        val bgList = ArrayList<Int>()
                        bgList.add(R.drawable.wobblebg)
                        bgList.add(R.drawable.catbg)
                        bgList.add(R.drawable.umbrellabg)
                        bgList.add(R.drawable.personbg)
                        bgList.add(R.drawable.interchangebg)
                        bgList.add(R.drawable.musicboxbg)
                        for ((index, id) in bgList.withIndex()) {
                            val uri = Uri.parse("android.resource://${packageName}/${id}")
                            presetSelectionDialog.presetSelectionsUri[index] = uri
                        }
                        presetSelectionDialog.onSelectionListener = { uri ->
                            applySelectedImage(uri, ImageType.GIF)
                            if (createViewModel.designEditorFragment != null)
                                createViewModel.designEditorFragment!!.onResume()
                        }
                        presetSelectionDialog.show(mainActivity.supportFragmentManager, "PresetSelectionDialog")
                    }
                    2 -> {
                        imageSelected = ImageSelected.BACKGROUND
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
            }
            dualSelectionDialog.show(mainActivity.supportFragmentManager, "DualSelectionDialog")
        }
    }

    private fun onSelectForegroundEvent() {
        binding.selectForegroundBtn.setOnClickListener {
            val dualSelectionDialog = DualSelectionDialog()
            dualSelectionDialog.title = "Select Foreground"
            dualSelectionDialog.selectionLabelA = "Preset"
            dualSelectionDialog.selectionLabelB = "Gallery"
            dualSelectionDialog.onSelectionListener = { selection ->
                when (selection) {
                    1 -> {
                        imageSelected = ImageSelected.FOREGROUND
                        val presetSelectionDialog = PresetSelectionDialog()
                        val packageName = mainActivity.packageName
                        val bgList = ArrayList<Int>()
                        bgList.add(R.drawable.wobblebg)
                        bgList.add(R.drawable.catbg)
                        bgList.add(R.drawable.umbrellabg)
                        bgList.add(R.drawable.personbg)
                        bgList.add(R.drawable.interchangebg)
                        bgList.add(R.drawable.musicboxbg)
                        for ((index, id) in bgList.withIndex()) {
                            val uri = Uri.parse("android.resource://${packageName}/${id}")
                            presetSelectionDialog.presetSelectionsUri[index] = uri
                        }
                        presetSelectionDialog.onSelectionListener = { uri ->
                            applySelectedImage(uri, ImageType.GIF)
                            if (createViewModel.designEditorFragment != null)
                                createViewModel.designEditorFragment!!.onResume()
                        }
                        presetSelectionDialog.show(mainActivity.supportFragmentManager, "PresetSelectionDialog")
                    }
                    2 -> {
                        imageSelected = ImageSelected.FOREGROUND
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
            }
            dualSelectionDialog.show(mainActivity.supportFragmentManager, "DualSelectionDialog")
        }
    }

    private fun onSaveEvent() {
        binding.saveBtn.setOnClickListener {
            var titleExists = false
            for (design in createViewModel.designCollection.list) {
                if (design.name.equals(binding.titleInput.text.toString(), ignoreCase = true))
                    titleExists = true
            }
            val hasTitle = binding.titleInput.text.isNotEmpty()
            if (hasTitle) {
                if (createViewModel.selectedDesignModel != null) { // Overwrite
                    createViewModel.selectedDesignModel!!.name = binding.titleInput.text.toString()
                    if (backgroundImageType != ImageType.NONE)
                        createViewModel.selectedDesignModel!!.backgroundImagePath = createViewModel.selectedDesignModel!!.id+"_bg.$backgroundImageType"
                    if (foregroundImageType != ImageType.NONE)
                        createViewModel.selectedDesignModel!!.foregroundImagePath = createViewModel.selectedDesignModel!!.id+"_fg.$foregroundImageType"
                    saveBackground(true)
                    saveForeground(true)
                    createViewModel.designCollection.saveToStored(mainActivity)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                } else if (!titleExists) { // Create
                    var bgFilePath = ""
                    var fgFilePath = ""
                    if (createViewModel.selectedBackgroundBitmap != null || createViewModel.selectedBackgroundDrawable != null) {
                        bgFilePath = binding.titleInput.text.toString()+"_bg.$backgroundImageType"
                    }
                    if (createViewModel.selectedForegroundBitmap != null || createViewModel.selectedForegroundDrawable != null) {
                        fgFilePath = binding.titleInput.text.toString()+"_fg.$foregroundImageType"
                    }
                    val model = DesignModel(
                        binding.titleInput.text.toString(), // may need to change id once databasing is implemented.
                        binding.titleInput.text.toString(),
                        backgroundColor = "#FFFFFF",
                        bgFilePath,
                        fgFilePath)
                    createViewModel.selectedDesignModel = model
                    createViewModel.designCollection.list.add(model)
                    saveBackground()
                    saveForeground()
                    createViewModel.designCollection.saveToStored(mainActivity)
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Title already exists!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Requires a title!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBackground(overwrite: Boolean = false) {
        if (createViewModel.selectedBackgroundBitmap != null || createViewModel.selectedBackgroundDrawable != null) {
            // Cleanup before saving, instead of overwriting of possibly leaving a file.
            InternalStorageHandler.deleteImage(
                mainActivity,
                createViewModel.selectedDesignModel!!.id+"_bg",
                arrayListOf("gif", "jpg", "jpeg", "png"))

            if (overwrite) {
                val fileName = createViewModel.selectedDesignModel!!.id+"_bg.$backgroundImageType"
                if (backgroundImageType.equals(ImageType.GIF, true)) {
                    InternalStorageHandler.copyFileFromUri(
                        context = mainActivity,
                        uri = backgroundImageUri!!,
                        fileName = fileName)
                } else {
                    InternalStorageHandler.saveImage(
                        activity = mainActivity,
                        filename = fileName,
                        bmp = createViewModel.selectedBackgroundBitmap!!)
                }
                createViewModel.selectedDesignModel!!.backgroundImagePath = fileName
            } else {
                if (backgroundImageType.equals(ImageType.GIF, true)) {
                    InternalStorageHandler.copyFileFromUri(
                        context = mainActivity,
                        uri = backgroundImageUri!!,
                        fileName = binding.titleInput.text.toString() + "_bg.$backgroundImageType")
                } else {
                    InternalStorageHandler.saveImage(
                        activity = mainActivity,
                        binding.titleInput.text.toString() + "_bg.$backgroundImageType",
                        bmp = createViewModel.selectedBackgroundBitmap!!)
                }
            }
        }
    }

    private fun saveForeground(overwrite: Boolean = false) {
        if (createViewModel.selectedForegroundBitmap != null  || createViewModel.selectedForegroundDrawable != null) {
            // Cleanup before saving, instead of overwriting of possibly leaving a file.
            InternalStorageHandler.deleteImage(
                mainActivity,
                createViewModel.selectedDesignModel!!.id+"_fg",
                arrayListOf("gif", "jpg", "jpeg", "png"))

            if (overwrite) {
                val fileName = createViewModel.selectedDesignModel!!.id+"_fg.$foregroundImageType"
                if (foregroundImageType.equals(ImageType.GIF, true)) {
                    InternalStorageHandler.copyFileFromUri(
                        context = mainActivity,
                        uri = foregroundImageUri!!,
                        fileName = fileName)
                } else {
                    InternalStorageHandler.saveImage(
                        activity = mainActivity,
                        filename = fileName,
                        bmp = createViewModel.selectedForegroundBitmap!!)
                    createViewModel.selectedDesignModel!!.foregroundImagePath = fileName
                }
            } else {
                if (foregroundImageType.equals(ImageType.GIF, true)) {
                    InternalStorageHandler.copyFileFromUri(
                        context = mainActivity,
                        uri = foregroundImageUri!!,
                        fileName = binding.titleInput.text.toString() + "_fg.$foregroundImageType")
                } else {
                    InternalStorageHandler.saveImage(
                        activity = mainActivity,
                        filename = binding.titleInput.text.toString() + "_fg.$foregroundImageType",
                        bmp = createViewModel.selectedForegroundBitmap!!)
                }
            }
        }
    }
}
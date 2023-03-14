package com.example.feelvibes.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.feelvibes.R
import com.example.feelvibes.databinding.CommentsDialogBinding
import com.example.feelvibes.home.recycler.CommentRecyclerAdapter
import com.example.feelvibes.home.recycler.CommentRecyclerEvent
import com.example.feelvibes.model.CommentModel
import com.example.feelvibes.model.ReplyModel
import com.example.feelvibes.utils.FVFireStoreHandler
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind

class CommentsDialog(
    private val ownerId:String,
    private val postId: String,
    var userId:String?,
    commentsMap: HashMap<*,*>?
) : FragmentBottomSheetDialogBind<CommentsDialogBinding>(CommentsDialogBinding::inflate), CommentRecyclerEvent {

    private lateinit var accountViewModel : AccountViewModel

    var onDismissListener : (() -> Unit)? = null
    var onQueryListener : (() -> Unit)? = null
    private var modified = false

    private val comments = FVFireStoreHandler.parcelizeComments(commentsMap)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onReady() {
        super.onReady()
        onCloseClick()
        loadUser()
        onCommentClick()
        setupRecyclerAdapter()
    }

    private fun setupRecyclerAdapter() {
        binding.commentsRecyclerView.adapter = CommentRecyclerAdapter(
            requireActivity(),
            this,
            ownerId,
            postId,
            userId,
            comments
        )
    }

    private fun loadUser() {
        if (userId != null) {
            FVFireStoreHandler.getProfilePicture(userId!!) { uri ->
                if (uri != null)
                    Glide.with(this).load(uri).into(binding.profilePic)
            }
        }
    }

    private fun onCloseClick() {
        binding.closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun onCommentClick() {
        binding.commentBtn.setOnClickListener {
            if (userId != null) {
                val keyboardEditableDialog = KeyboardEditableDialog()
                keyboardEditableDialog.onSendListener = { comment ->
                    if (comment != null) {
                        FVFireStoreHandler.addPostComment(ownerId, postId, userId!!, comment) { success, model, exception ->
                            if (success && model != null) {
                                comments.add(model)
                                modified = true
                                setupRecyclerAdapter()
                            } else {
                                exception?.printStackTrace()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Comment is empty!", Toast.LENGTH_SHORT).show()
                    }
                }
                keyboardEditableDialog.show(requireActivity().supportFragmentManager, "KeyboardEditableDialog")
            } else {
                val authorizationRequestDialog = AuthorizationRequestDialog()
                authorizationRequestDialog.onDismissListener = {
                    FVFireStoreHandler.checkAuth {
                        if (it != null) {
                            userId = it.uid
                            setupRecyclerAdapter()
                        }
                    }
                }
                authorizationRequestDialog.show(requireActivity().supportFragmentManager, "AuthorizationRequestDialog")
            }
        }
    }

    override fun onUserClick(userId: String) {
        accountViewModel.selectedUserId = userId
        findNavController().navigate(R.id.action_global_profileFragment)
        dismiss()
    }

    override fun onCommentDelete(commentModel: CommentModel) {
        comments.remove(commentModel)
        modified = true
        setupRecyclerAdapter()
    }

    override fun onCommentLiked(commentModel: CommentModel, userId: String) {
        comments.find { it.id == commentModel.id }?.likes?.add(userId)
        modified = true
    }

    override fun onCommentUnliked(commentModel: CommentModel, userId: String) {
        comments.find { it.id == commentModel.id }?.likes?.remove(userId)
        modified = true
    }

    override fun onReplyClick(commentModel: CommentModel, replies: ArrayList<ReplyModel>) {
        // Disabling this for now
        /*
        val repliesDialog = RepliesDialog(ownerId, postId, commentModel.id, userId, replies)
        repliesDialog.onQueryListener = {
            comments.find { it.id == commentModel.id }?.replies = replies
            modified = true
            onQueueRefresh()
        }
        repliesDialog.onDismissListener = {
            binding.commentsDialogLayout.visibility = View.VISIBLE
        }
        repliesDialog.show(requireActivity().supportFragmentManager, "RepliesDialog")
        binding.commentsDialogLayout.visibility = View.INVISIBLE*/
    }

    override fun onQueueRefresh() {
        setupRecyclerAdapter()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (modified)
            onQueryListener?.invoke()
        onDismissListener?.invoke()
    }

}
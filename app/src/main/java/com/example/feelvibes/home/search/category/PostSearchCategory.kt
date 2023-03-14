package com.example.feelvibes.home.search.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.feelvibes.R
import com.example.feelvibes.dialogs.CommentsDialog
import com.example.feelvibes.home.recycler.PostRecyclerEvent
import com.example.feelvibes.home.recycler.PostsRecyclerAdapter
import com.example.feelvibes.model.PostModel
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

abstract class PostSearchCategory<VB : ViewBinding>(
    bindingInflater: (inflater: LayoutInflater) -> VB
): FragmentBind<VB>(bindingInflater), PostRecyclerEvent {

    private lateinit var accountViewModel : AccountViewModel
    private lateinit var createViewModel: CreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
    }

    override fun onReady() {
        queryPosts {
            if (it != null) {
                if (it.isNotEmpty()) {
                    val noPostWarn = binding.root.findViewById<TextView>(R.id.noPostWarn)
                    noPostWarn.visibility = View.GONE
                }
                updateAdapter(it)
            }
        }
    }

    private fun updateAdapter(postModels: ArrayList<PostModel>) {
        val postsRecyclerView = binding.root.findViewById<RecyclerView>(R.id.postsRecyclerView)
        postsRecyclerView.adapter = PostsRecyclerAdapter(
            requireActivity(),
            this,
            postModels,
            accountViewModel.currentUser?.uid,
            createViewModel
        )
    }

    private fun queryPosts(callback: (ArrayList<PostModel>?) -> Unit) {
        val collectionRef = FirebaseFirestore.getInstance().collection("usersPost")
        collectionRef.get()
            .addOnSuccessListener { documents ->
                callback(filterPosts(documents))
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null)
            }
    }

    fun filterQuery(querySnapshot: QuerySnapshot): MutableMap<String, MutableMap<String, Any>> {
        val data = mutableMapOf<String, MutableMap<String, Any>>()
        for (document in querySnapshot) {
            data[document.id] = document.data
        }
        return data
    }

    abstract fun filterPosts(querySnapshot: QuerySnapshot): ArrayList<PostModel>

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
        queryPosts {
            if (it != null) {
                if (it.isNotEmpty()) {
                    val noPostWarn = binding.root.findViewById<TextView>(R.id.noPostWarn)
                    noPostWarn.visibility = View.GONE
                }
                updateAdapter(it)
            }
        }
    }

}
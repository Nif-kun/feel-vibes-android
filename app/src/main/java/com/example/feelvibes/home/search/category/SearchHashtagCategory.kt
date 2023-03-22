package com.example.feelvibes.home.search.category

import com.example.feelvibes.databinding.FragmentSearchHashtagCategoryBinding
import com.example.feelvibes.model.PostModel
import com.google.firebase.firestore.QuerySnapshot

class SearchHashtagCategory : PostSearchCategory<FragmentSearchHashtagCategoryBinding>(FragmentSearchHashtagCategoryBinding::inflate) {

    override fun filterPosts(querySnapshot: QuerySnapshot): ArrayList<PostModel> {
        val postModels = arrayListOf<PostModel>()
        val documents = filterQuery(querySnapshot)
        for (document in documents) {
            for (post in document.value) {
                val content = post.value
                if (content is HashMap<*, *>) {
                    val text = content["text"]
                    if (text is String && text.contains(mainActivity.getSearchBar().text.toString(), true)) {
                        postModels.add(PostModel(document.key, post.key))
                    }
                }
            }
        }
        return postModels
    }

}
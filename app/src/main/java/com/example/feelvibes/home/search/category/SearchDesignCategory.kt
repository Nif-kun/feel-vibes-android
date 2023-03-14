package com.example.feelvibes.home.search.category

import com.example.feelvibes.databinding.FragmentPostSearchDesignBinding
import com.example.feelvibes.model.PostModel
import com.google.firebase.firestore.QuerySnapshot

class SearchDesignCategory : PostSearchCategory<FragmentPostSearchDesignBinding>(FragmentPostSearchDesignBinding::inflate) {

    override fun filterPosts(querySnapshot: QuerySnapshot): ArrayList<PostModel> {
        val postModels = arrayListOf<PostModel>()
        val documents = filterQuery(querySnapshot)
        for (document in documents) {
            for (post in document.value) {
                val content = post.value
                if (content is HashMap<*, *>) {
                    val music = content["design"]
                    if (music is HashMap<*, *>) {
                        val name = music["name"]
                        if (name is String && name.contains(mainActivity.getSearchBar().text.toString(), true)) {
                            postModels.add(PostModel(document.key, post.key))
                        }
                    }
                }
            }
        }
        return postModels
    }

}
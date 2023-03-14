package com.example.feelvibes.home.search.category

import com.example.feelvibes.databinding.FragmentPostSearchMusicBinding
import com.example.feelvibes.home.recycler.PostRecyclerEvent
import com.example.feelvibes.model.PostModel
import com.google.firebase.firestore.QuerySnapshot

class SearchMusicCategory : PostSearchCategory<FragmentPostSearchMusicBinding>(FragmentPostSearchMusicBinding::inflate), PostRecyclerEvent {

    override fun filterPosts(querySnapshot: QuerySnapshot): ArrayList<PostModel> {
        val postModels = arrayListOf<PostModel>()
        val documents = filterQuery(querySnapshot)
        for (document in documents) {
            for (post in document.value) {
                val content = post.value
                if (content is HashMap<*, *>) {
                    val music = content["music"]
                    if (music is HashMap<*, *>) {
                        val title = music["title"]
                        if (title is String && title.contains(mainActivity.getSearchBar().text.toString(), true)) {
                            postModels.add(PostModel(document.key, post.key))
                        }
                    }
                }
            }
        }
        return postModels
    }

}
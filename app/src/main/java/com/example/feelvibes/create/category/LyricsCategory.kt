package com.example.feelvibes.create.category

import com.example.feelvibes.create.CreateCategoryFragment
import com.example.feelvibes.databinding.FragmentLyricsCategoryBinding
import com.example.feelvibes.interfaces.RecyclerItemClick


class LyricsCategory :
    CreateCategoryFragment<FragmentLyricsCategoryBinding>(FragmentLyricsCategoryBinding::inflate),
    RecyclerItemClick {

    override fun onItemClick(pos: Int) {
        TODO("Not yet implemented")
    }

}
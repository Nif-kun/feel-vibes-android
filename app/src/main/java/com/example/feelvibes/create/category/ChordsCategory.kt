package com.example.feelvibes.create.category

import com.example.feelvibes.create.CreateCategoryFragment
import com.example.feelvibes.databinding.FragmentChordsCategoryBinding
import com.example.feelvibes.interfaces.RecyclerItemClick

class ChordsCategory :
    CreateCategoryFragment<FragmentChordsCategoryBinding>(FragmentChordsCategoryBinding::inflate),
    RecyclerItemClick {

    override fun onItemClick(pos: Int) {
        TODO("Not yet implemented")
    }

}
package com.example.feelvibes.interfaces

interface RecyclerItemClick {

    fun onItemClick(pos : Int) {}
    fun onMoreClick(pos : Int) {}
    fun onItemClick(pos : Int, id: Int) {}

}
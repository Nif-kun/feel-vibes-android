package com.example.feelvibes

import androidx.lifecycle.ViewModel
import com.example.feelvibes.model.MusicModel

class MainActivityViewModel : ViewModel() {

    val musicDataList = ArrayList<MusicModel>()

    fun updateMusicDataList(list : ArrayList<MusicModel>){
        musicDataList.clear()
        musicDataList.addAll(list)
    }

}
package com.example.feelvibes.library.category

import androidx.lifecycle.ViewModel

class CategoryViewModelHandler {

    open class CategoryViewModel : ViewModel() {
        var currentTabItem = -1
        var recycleViewScrollOffset = -1
    }

    class PlaylistViewModel : CategoryViewModel()
    class ArtistViewModel : CategoryViewModel()
    class AlbumViewModel : CategoryViewModel()
    class TagViewModel : CategoryViewModel()

}
package com.example.feelvibes.library

import com.example.feelvibes.utils.CategoryViewModelHandler

class LibraryCategoryHandler : CategoryViewModelHandler() {

    class PlaylistViewModel : CategoryViewModel()
    class ArtistViewModel : CategoryViewModel()
    class AlbumViewModel : CategoryViewModel()
    class TagViewModel : CategoryViewModel()

}
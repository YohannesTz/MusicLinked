package com.github.yohannes.musiclinked.ui.screens.home

import com.github.yohannes.musiclinked.data.models.SongModel

data class SongsListState(
    val songsList: List<SongModel> = emptyList(),
    val isLoading: Boolean = true
)
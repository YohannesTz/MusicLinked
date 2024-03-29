package com.github.yohannes.musiclinked.data.models

import android.graphics.Bitmap
import android.net.Uri

data class SongModel(
    val id : Long? = -1,
    var title: String? = "",
    val duration: Long? = -1,
    val data: String? = "",
    val dateAdded : String? = "",
    val artist : String? = "",
    val uri : Uri? = null,
    val albumId : Long? = -1,
    val size: String? = "",
    val bitrate : String? ="",
    val image : Bitmap? = null,
    val trackNumber: String? = "",
    val year: Int? = -1,
    val dateModified: Long? = -1,
    val artistId: Long? = -1,
    val artistName: String? = "",
    val composer: String? = "",
    val albumArtist: String?= ""
)
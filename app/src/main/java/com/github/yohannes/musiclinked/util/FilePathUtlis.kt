package com.github.yohannes.musiclinked.util

import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_MUSIC
import android.provider.MediaStore
import java.io.File

object FilePathUtlis {
    private val MUSICS_INTERNAL_STORAGE: File =
        Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC)

    fun getMusicsUri(): Uri {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    fun getAlbumsUri(): String {
        return "content://media/external/audio/albumart"
    }

    fun getPlayListsUri(): Uri {
        return MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
    }
}

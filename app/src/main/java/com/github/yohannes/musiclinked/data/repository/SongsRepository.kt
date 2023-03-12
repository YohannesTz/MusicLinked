package com.github.yohannes.musiclinked.data.repository

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import javax.inject.Inject
import android.provider.MediaStore.Audio.AudioColumns
import android.util.Log
import com.github.yohannes.musiclinked.data.models.SongModel
import com.github.yohannes.musiclinked.util.FileUtils
import com.github.yohannes.musiclinked.util.ImageUtils

class SongsRepository @Inject constructor(private val context: Context) {

    @SuppressLint("Range")
    fun createSongFromCursor(cursor: Cursor): SongModel {
        val title = cursor.getString(cursor.getColumnIndex(AudioColumns.TITLE))
        val duration = cursor.getLong(cursor.getColumnIndex(AudioColumns.DURATION))
        val data = cursor.getString(cursor.getColumnIndex(AudioColumns.DATA))
        val id = cursor.getLong(cursor.getColumnIndex(AudioColumns._ID))
        val dateAdded = cursor.getString(cursor.getColumnIndex(AudioColumns.DATE_ADDED))
        val artist = cursor.getString(cursor.getColumnIndex(AudioColumns.ARTIST))
        val year = cursor.getInt(cursor.getColumnIndex(AudioColumns.YEAR))
        val dateModified = cursor.getLong(cursor.getColumnIndex(AudioColumns.DATE_MODIFIED))
        val artistId = cursor.getLong(cursor.getColumnIndex(AudioColumns.ARTIST_ID))
        val artistName = cursor.getString(cursor.getColumnIndex(AudioColumns.ARTIST))
        val uri = ContentUris
            .withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
            )
        val albumId = cursor.getLong(cursor.getColumnIndex(AudioColumns.ALBUM_ID))
        val size = cursor.getString(cursor.getColumnIndex(AudioColumns.SIZE))

        val image = ImageUtils.albumArtUriToBitmap(context, albumId)

        var bitrate = ""
        if (data != "") {
            val metadata = MediaMetadataRetriever()
            metadata.setDataSource(data)
            bitrate = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE).toString()

        }

        return SongModel(
            title = title,
            duration = duration,
            data = data,
            dateAdded = dateAdded,
            artist = artist,
            id = id,
            uri = uri,
            albumId = albumId,
            size = size,
            bitrate = bitrate,
            image = image,
            trackNumber = "",
            year = year,
            dateModified = dateModified,
            artistId = artistId,
            artistName = artistName,
            composer = "",
            albumArtist = ""
        )
    }

    @SuppressLint("Range")
    private fun getSongsFromStorage(): ArrayList<SongModel> {
        val songsAreInStorage = ArrayList<SongModel>()
        val cursor = FileUtils.fetchFiles(
            fileType = FileUtils.FILE_TYPES.MUSIC,
            context = context
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    songsAreInStorage.add(createSongFromCursor(cursor))
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        Log.e("songsAreInStorage", songsAreInStorage.size.toString())
        return songsAreInStorage
    }

    fun getArrayListOfSongs(): ArrayList<SongModel> {
        return getSongsFromStorage()
    }

    fun getListOfSongs(): List<SongModel> {
        return getSongsFromStorage()
    }
}
package com.github.yohannes.musiclinked.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.github.yohannes.musiclinked.R
import java.io.FileDescriptor

object ImageUtils {

    fun loadImageToImageView(context: Context, imageView: ImageView, image: Bitmap) {
        Glide.with(context).load(image).circleCrop().into(imageView)
    }

    fun albumArtUriToBitmap(context: Context, album_id: Long?): Bitmap? {
        var bm: Bitmap? = null
        val options = BitmapFactory.Options()
        try {
            val sArtworkUri =
                Uri.parse(FilePathUtlis.getAlbumsUri())


            val uri = ContentUris.withAppendedId(sArtworkUri, album_id!!)
            val pfd =
                context.contentResolver.openFileDescriptor(uri, "r")
            if (pfd != null) {
                val fileDescriptor: FileDescriptor? = pfd.fileDescriptor
                bm = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            }
            pfd?.close()
        } catch (_: java.lang.Exception) {
        }

        return bm
    }

    fun getDefaultAlbumArt(context: Context): Bitmap {
        /*return BitmapFactory.decodeResource(
            context.resources,
            R.mipmap.ic_music
        )*/
        return ContextCompat.getDrawable(context, R.drawable.baseline_photo)?.toBitmap()!!
    }
}
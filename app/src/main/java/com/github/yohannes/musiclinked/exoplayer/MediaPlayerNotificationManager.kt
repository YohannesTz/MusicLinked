package com.github.yohannes.musiclinked.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.yohannes.musiclinked.R
import com.github.yohannes.musiclinked.util.Constants
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.*

internal class MediaPlayerNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

    private val notificationManager: PlayerNotificationManager
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)

        val builder = PlayerNotificationManager.Builder(
            context,
            Constants.PLAYBACK_NOTIFICATION_ID,
            Constants.PLAYBACK_NOTIFICATION_CHANNEL_ID
        )


        with(builder) {
            setMediaDescriptionAdapter(DescriptionAdapter(mediaController, context))
            setNotificationListener(notificationListener)
            setChannelNameResourceId(R.string.notification_channel)
            setChannelDescriptionResourceId(R.string.notification_channel_description)
        }

        notificationManager = builder.build()

        with(notificationManager) {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.ic_baseline_music_note_24)
            setUseRewindAction(false)
            setUseFastForwardAction(false)
            setColorized(true)
            setColor(R.color.black)
            setUseRewindAction(false)
        }
    }


    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotification(player: Player) {
        notificationManager.setPlayer(player)
    }


    inner class
    DescriptionAdapter(
        private val controller: MediaControllerCompat,
        private val passedContext: Context
    ) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        private var currentIconUri: Uri? = null
        private var currentBitmap: Bitmap? = null

        override fun getCurrentContentTitle(player: Player): CharSequence =
            controller.metadata.description.title.toString()

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player): CharSequence? =
            controller.metadata.description.subtitle

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = controller.metadata.description.iconUri
            return if (currentIconUri != iconUri || currentBitmap == null) {

                // Cache the bitmap for the current song so that successive calls to
                // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
                currentIconUri = iconUri
                serviceScope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                currentBitmap
            }
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                Glide.with(context).applyDefaultRequestOptions(glideOptions)
                    .asBitmap()
                    .load(uri)
                    .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                    .get()
            }
        }

    }
}

const val NOTIFICATION_LARGE_ICON_SIZE = 144 // px

private val glideOptions = RequestOptions()
    .fallback(R.mipmap.ic_music)
    .diskCacheStrategy(DiskCacheStrategy.DATA)
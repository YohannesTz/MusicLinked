package com.github.yohannes.musiclinked.exoplayer

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.github.yohannes.musiclinked.data.models.SongModel
import com.github.yohannes.musiclinked.services.PlayerService
import com.github.yohannes.musiclinked.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import hoods.com.audioplayer.media.exoplayer.currentPosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MediaPlayerServiceConnection @Inject constructor(
    @ApplicationContext context: Context
) {
    private val _playBackState: MutableStateFlow<PlaybackStateCompat?> =
        MutableStateFlow(null)
    val playBackState: StateFlow<PlaybackStateCompat?>
        get() = _playBackState

    private val _isConnected: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val isConnected: StateFlow<Boolean>
        get() = _isConnected

    val currentPlayingAudio = mutableStateOf<SongModel?>(null)
    lateinit var mediaControllerCompat: MediaControllerCompat

    private val mediaBrowserServiceCallback =
        MediaBrowserConnectionCallBack(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(context, PlayerService::class.java),
        mediaBrowserServiceCallback,
        null
    ).apply {
        Log.e("attempt", "to connect.")
        connect()
    }

    private var audioList = listOf<SongModel>()
    val rootMediaId: String
        get() = mediaBrowser.root

    val transportControl: MediaControllerCompat.TransportControls
        get() = mediaControllerCompat.transportControls

    fun playMusic(musicList: List<SongModel>) {
        Log.e("playMusic:", "Is mediaBrowser connected? ${mediaBrowser.isConnected}")
        audioList = musicList
        mediaBrowser.sendCustomAction(Constants.START_MEDIA_PLAY_ACTION, null, null)
    }

    fun fastForward(seconds: Int = 10) {
        playBackState.value?.currentPosition?.let {
            transportControl.seekTo(it + seconds * 1000)
        }
    }

    fun rewind(seconds: Int = 10) {
        playBackState.value?.currentPosition?.let {
            transportControl.seekTo(it - seconds * 1000)
        }
    }

    fun skipToNext() {
        transportControl.skipToNext()
    }

    fun skipToPrev() {
        transportControl.skipToPrevious()
    }

    fun subscribe(
        parentId: String,
        callBack: MediaBrowserCompat.SubscriptionCallback
    ) {
        mediaBrowser.subscribe(parentId, callBack)
    }

    fun unSubscribe(
        parentId: String,
        callBack: MediaBrowserCompat.SubscriptionCallback
    ) {
        mediaBrowser.unsubscribe(parentId, callBack)
    }

    fun refreshMediaBrowserChildren() {
        mediaBrowser.sendCustomAction(
            Constants.REFRESH_MEDIA_PLAY_ACTION,
            null,
            null
        )
    }

    private inner class MediaBrowserConnectionCallBack(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.e("connection", "yes!")
            _isConnected.value = true
            mediaControllerCompat = MediaControllerCompat(
                context,
                mediaBrowser.sessionToken
            ).apply {
                registerCallback(MediaControllerCallBack())
            }
        }

        override fun onConnectionSuspended() {
            Log.e("connection", "suspended")
            _isConnected.value = false
        }

        override fun onConnectionFailed() {
            Log.e("connection", "failed")
            _isConnected.value = false
        }
    }

    private inner class MediaControllerCallBack : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            _playBackState.value = state
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            currentPlayingAudio.value = metadata?.let { data ->
                audioList.find {
                    it.id.toString() == data.description.mediaId
                }
            }
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            mediaBrowserServiceCallback.onConnectionSuspended()
        }
    }
}
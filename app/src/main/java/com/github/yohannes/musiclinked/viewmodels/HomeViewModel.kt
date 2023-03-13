package com.github.yohannes.musiclinked.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.yohannes.musiclinked.data.models.SongModel
import com.github.yohannes.musiclinked.data.repository.SongsRepository
import com.github.yohannes.musiclinked.exoplayer.MediaPlayerServiceConnection
import com.github.yohannes.musiclinked.services.PlayerService
import com.github.yohannes.musiclinked.ui.screens.home.SongsListState
import com.github.yohannes.musiclinked.util.Constants
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import hoods.com.audioplayer.media.exoplayer.currentPosition
import hoods.com.audioplayer.media.exoplayer.isPlaying
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    serviceConnection: MediaPlayerServiceConnection
) : ViewModel() {

    private val _state = mutableStateOf(SongsListState())
    val state: State<SongsListState> = _state

    val currentPlayingAudio = serviceConnection.currentPlayingAudio
    private val isConnected = serviceConnection.isConnected

    private lateinit var rootMediaId: String
    var currentPlayBackPosition by mutableStateOf(0L)
    private var updatePosition = true
    private val playbackState = serviceConnection.playBackState
    val isAudioPlaying: Boolean
        get() = playbackState.value?.isPlaying == true

    private val subscriptionCallback = object
        : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
        }
    }
    private val serviceConnection = serviceConnection.also {
        if (state.value.songsList.isNotEmpty()) {
            updatePlayBack()
        }
    }

    private val currentDuration: Long
        get() = PlayerService.currentDuration

    var currentAudioProgress = mutableStateOf(0f)
    init {
        viewModelScope.launch {
  //          withContext(Dispatchers.Main) {
                _state.value = state.value.copy(
                    isLoading = true
                )
//            }

            /*withContext(Dispatchers.IO) {
                val list = getListOfSongs()
                _state.value = state.value.copy(
                    songsList = list
                )
            }*/

            val list = withContext(Dispatchers.IO) {
                getListOfSongs()
            }

            _state.value = state.value.copy(
                songsList = list,
                isLoading = false
            )

            isConnected.collect {
                Log.e("isConnected?", "$it")
                if (it) {
                    rootMediaId = serviceConnection.rootMediaId
                    serviceConnection.playBackState.value?.apply {
                        currentPlayBackPosition = position
                    }
                    serviceConnection.subscribe(rootMediaId, subscriptionCallback)
                }
            }
        }
    }

    private fun getListOfSongs(): List<SongModel> {
        return songsRepository.getListOfSongs()
    }

    fun playAudio(currentAudio: SongModel) {
        Log.e("songsList", state.value.songsList.size.toString())
        serviceConnection.playMusic(state.value.songsList)
        if (currentAudio.id == currentPlayingAudio.value?.id) {
            if (isAudioPlaying) {
                serviceConnection.transportControl.pause()
            } else {
                serviceConnection.transportControl.play()
            }
        } else {
            serviceConnection.transportControl.playFromMediaId(
                currentAudio.id.toString(),
                null
            )
        }
        updatePlayBack()
    }

    fun stopPlayBack() {
        serviceConnection.transportControl.stop()
    }

    fun fastForward() {
        serviceConnection.fastForward()
    }

    fun rewind() {
        serviceConnection.rewind()
    }

    fun skipToNext() {
        serviceConnection.skipToNext()
    }

    fun skipToPrev() {
        serviceConnection.skipToPrev()
    }

    fun seekTo(value: Float) {
        serviceConnection.transportControl.seekTo(
            (currentDuration * value / 100f).toLong()
        )
    }

    fun loopOne() {
        serviceConnection.transportControl.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
    }

    fun loopAll() {
        serviceConnection.transportControl.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
    }

    fun loopNone() {
        serviceConnection.transportControl.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
    }

    fun shuffle() {
        serviceConnection.transportControl.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
    }

    private fun updatePlayBack() {
        viewModelScope.launch {
            val position = playbackState.value?.currentPosition

            if (currentPlayBackPosition != position && position != null) {
                currentPlayBackPosition = position
            }

            if (currentDuration > 0) {
                currentAudioProgress.value = (
                        currentPlayBackPosition.toFloat() / currentDuration.toFloat() * 100f)
            }
            delay(Constants.PLAYBACK_UPDATE_INTERVAL)
            if (updatePosition) {
                updatePlayBack()
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        serviceConnection.unSubscribe(
            Constants.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {}
        )
        updatePosition = false
    }
}
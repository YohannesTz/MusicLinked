package com.github.yohannes.musiclinked.ui.screens.home

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.yohannes.musiclinked.R
import com.github.yohannes.musiclinked.data.models.SongModel
import com.github.yohannes.musiclinked.ui.screens.home.components.IconBtn
import com.github.yohannes.musiclinked.ui.screens.home.components.SongListItem
import com.github.yohannes.musiclinked.ui.theme.MusicLinkedTheme
import com.github.yohannes.musiclinked.viewmodels.HomeViewModel
import java.util.*
import kotlin.math.floor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val state = homeViewModel.state.value

    val scaffoldState = rememberBottomSheetScaffoldState()

    val animatedHeight by animateDpAsState(
        targetValue = if (homeViewModel.currentPlayingAudio.value == null) 0.dp
        else BottomSheetScaffoldDefaults.SheetPeekHeight
    )

    BottomSheetScaffold(
        sheetContent = {
            homeViewModel.currentPlayingAudio.value?.let { song ->
                PlayerBottomSheet(
                    progress = homeViewModel.currentAudioProgress.value,
                    onProgressChange = {
                        homeViewModel.seekTo(it)
                    },
                    songModel = song,
                    onStart = {
                        homeViewModel.playAudio(it)
                    },
                    onNext = {
                        homeViewModel.skipToNext()
                    },
                    onPrevious = {
                        homeViewModel.skipToPrev()
                    },
                    onLoop = {
                        homeViewModel.loopAll()
                    },
                    onRepeat = {
                        homeViewModel.loopOne()
                    },
                    onShuffle = {
                        homeViewModel.shuffle()
                    },
                    isPlaying = homeViewModel.isAudioPlaying,
                    elapsedTime = homeViewModel.currentPlayBackPosition
                )
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = animatedHeight
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Log.e("isLoading", state.isLoading.toString())
            Log.e("state.ListSize", state.songsList.size.toString())
            if (state.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading...",
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.h6
                    )
                }

            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.songsList.size) { index ->
                        SongListItem(
                            songModel = state.songsList[index],
                            onClick = { homeViewModel.playAudio(it) })
                    }
                }
            }
        }
    }
}

private fun timeStampToDuration(position: Long): String {
    val totalSeconds = floor(position / 1E3).toInt()
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds - (minutes * 60)

    return if (position < 0) "--:--"
    else "%d:%02d".format(minutes, remainingSeconds)
}

private fun formatDuration(position: Long): String {
    val mFormatBuilder = StringBuilder()
    val mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
    val totalSeconds: Long = position / 1000
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600

    mFormatBuilder.setLength(0)
    return if (hours > 0) {
        mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    } else {
        mFormatter.format("%02d:%02d", minutes, seconds).toString()
    }
}

@Preview(showBackground = false)
@Composable
fun BottomBarPreview() {
    MusicLinkedTheme {
        PlayerBottomSheet(
            progress = 1f,
            onProgressChange = {},
            songModel = SongModel(),
            onStart = {},
            onNext = {},
            onPrevious = {},
            isPlaying = false,
            elapsedTime = 0,
            onLoop = {},
            onRepeat = {},
            onShuffle = {}
        )
    }
}

@Composable
fun PlayerBottomSheet(
    progress: Float,
    elapsedTime: Long,
    onProgressChange: (Float) -> Unit,
    songModel: SongModel,
    onStart: (SongModel) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onLoop: () -> Unit,
    onRepeat: () -> Unit,
    onShuffle: () -> Unit,
    isPlaying: Boolean
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp)
        ) {
            if (songModel.image != null) {
                Image(
                    bitmap = songModel.image.asImageBitmap(),
                    contentDescription = "App Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_photo),
                    contentDescription = "App Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = songModel.title.toString(), style = MaterialTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = songModel.artist.toString(),
                    style = MaterialTheme.typography.caption,
                    color = Color.DarkGray
                )
            }
        }
        Slider(
            modifier = Modifier
                .height(25.dp),
            value = progress,
            onValueChange = { onProgressChange.invoke(it) },
            valueRange = 0f..100f
        )
        Row {
            Text(text = formatDuration(elapsedTime), fontSize = 12.sp)
            Spacer(modifier = Modifier.weight(2f))
            Text(text = formatDuration(songModel.duration!!.toLong()), fontSize = 12.sp)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconBtn(resIcon = R.drawable.baseline_loop_24, onClick = {
                onLoop.invoke()
            })
            IconBtn(resIcon = R.drawable.baseline_skip_previous_24, onClick = {
                onPrevious.invoke()
            })
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colors.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconBtn(
                    tint = Color.White,
                    resIcon = if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24,
                    onClick = {
                        onStart.invoke(songModel)
                    }
                )
            }
            IconBtn(resIcon = R.drawable.baseline_skip_next_24, onClick = {
                onNext.invoke()
            })
            IconBtn(resIcon = R.drawable.baseline_repeat_24, onClick = {
                onRepeat.invoke()
            })
            IconBtn(resIcon = R.drawable.baseline_shuffle_24, onClick = {
                onShuffle.invoke()
            })
        }

    }
}
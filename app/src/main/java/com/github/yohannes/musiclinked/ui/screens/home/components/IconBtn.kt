package com.github.yohannes.musiclinked.ui.screens.home.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.yohannes.musiclinked.R


@Composable
fun IconBtn(
    resIcon: Int,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colors.primary,
    selected: Boolean = true,
    selectedIcon: Int = resIcon,
    onClick: () -> Unit = {},
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            painter = if (selected) {
                painterResource(id = selectedIcon)
            } else {
                painterResource(id = resIcon)
            },
            contentDescription = null,
            tint = tint
        )
    }
}


@Composable
@Preview
fun PreviewIconBtn() {
    IconBtn(
        resIcon = R.drawable.baseline_play_arrow_24,
    )
}
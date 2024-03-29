package com.github.yohannes.musiclinked.ui.screens.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.yohannes.musiclinked.R


@Composable
fun IconBtn(
    resIcon: Int,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colors.primary,
    selected: Boolean = true,
    selectedIcon: Int = resIcon,
    onClick: () -> Unit = {},
    size: Dp = 24.dp
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            painter = if (selected) {
                painterResource(id = selectedIcon)
            } else {
                painterResource(id = resIcon)
            },
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size)
        )
    }
}

/*@Composable
fun IconToggleButtonSample(checked: Boolean = false, icon: Int, onChecked: (Boolean) -> Unit) {

    IconToggleButton(checked = checked, onCheckedChange = onChecked) {
        val tint by animateColorAsState(if (checked) Color(0xFFEC407A) else Color(0xFFB0BEC5))
        Icon(icon, contentDescription = "Localized description", tint = tint)
    }
}*/


@Composable
@Preview
fun PreviewIconBtn() {
    IconBtn(
        resIcon = R.drawable.baseline_play_arrow_24,
    )
}
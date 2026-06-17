package com.roxy.flare.compose

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxy.flare.FlareIconType
import com.roxy.flare.FlareMessage
import com.roxy.flare.FlareTheme
import com.roxy.flare.FlareType
import kotlin.math.roundToInt

@Composable
fun FlareAlert(
    message: FlareMessage,
    onActionClicked: () -> Unit,
    onDismissRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    val action = message.action

    // Swipe to dismiss state variables
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val swipeThreshold = 300f

    // Smooth return transition when swipe is not long enough to dismiss
    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDragging) dragOffsetX else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "SwipeSnapBack"
    )

    // Progress Bar countdown state
    val progressAnimatable = remember { Animatable(1f) }
    val durationMillis = message.duration.durationMillis

    // Pause timer state when user is dragging or touching
    LaunchedEffect(isDragging) {
        if (durationMillis > 0) {
            if (isDragging) {
                progressAnimatable.stop() // Pause countdown
            } else {
                // Resume countdown from current state to 0
                val remainingDuration = (progressAnimatable.value * durationMillis).toLong()
                if (remainingDuration > 0) {
                    progressAnimatable.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(
                            durationMillis = remainingDuration.toInt(),
                            easing = LinearEasing
                        )
                    )
                    onDismissRequested()
                }
            }
        }
    }

    // Initialize haptic feedback on show
    val view = LocalView.current
    LaunchedEffect(message.id) {
        if (message.haptic) {
            try {
                view.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS)
            } catch (e: Exception) {
                // Fallback
            }
        }
    }

    // Determine font family
    val fontFamily = remember(message.fontResId) {
        message.fontResId?.let { fontId ->
            try {
                FontFamily(Font(fontId))
            } catch (e: Exception) {
                FontFamily.Default
            }
        } ?: FontFamily.Default
    }

    // Determine visual colors
    val theme = com.roxy.flare.FlareConfig.get().theme
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (theme) {
        FlareTheme.DARK -> true
        FlareTheme.LIGHT -> false
        FlareTheme.AUTO -> isSystemDark
    }

    val baseColorLong = message.customColor ?: message.type.defaultColorLong
    val backgroundColor = Color(baseColorLong)
    val contentColor = Color.White // universal high contrast

    val cornerRadius = (message.cornerRadiusDp ?: 12f).dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
            .graphicsLayer {
                // Fade view as it's swiped away
                alpha = (1f - (Math.abs(animatedOffsetX) / 600f)).coerceIn(0.1f, 1f)
            }
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(cornerRadius))
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        if (Math.abs(dragOffsetX) > swipeThreshold) {
                            onDismissRequested()
                        }
                        dragOffsetX = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffsetX = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffsetX += dragAmount.x
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Icon Section
                when (val iconType = message.icon) {
                    is FlareIconType.None -> {}
                    is FlareIconType.Default -> {
                        Box(modifier = Modifier.size(24.dp)) {
                            when (message.type) {
                                is FlareType.SUCCESS -> Image(
                                    painter = rememberVectorPainter(FlareComposeIcons.Success),
                                    contentDescription = "Success",
                                    modifier = Modifier.size(24.dp)
                                )
                                is FlareType.ERROR -> Image(
                                    painter = rememberVectorPainter(FlareComposeIcons.Error),
                                    contentDescription = "Error",
                                    modifier = Modifier.size(24.dp)
                                )
                                is FlareType.WARNING -> Image(
                                    painter = rememberVectorPainter(FlareComposeIcons.Warning),
                                    contentDescription = "Warning",
                                    modifier = Modifier.size(24.dp)
                                )
                                is FlareType.INFO -> Image(
                                    painter = rememberVectorPainter(FlareComposeIcons.Info),
                                    contentDescription = "Info",
                                    modifier = Modifier.size(24.dp)
                                )
                                is FlareType.LOADING -> LoadingSpinner(
                                    color = Color.White,
                                    size = 24.dp
                                )
                                is FlareType.CUSTOM -> Image(
                                    painter = rememberVectorPainter(FlareComposeIcons.Info),
                                    contentDescription = "Custom Info",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    is FlareIconType.Custom -> {
                        val painter = rememberCustomIconPainter(icon = iconType.icon)
                        if (painter != null) {
                            Image(
                                painter = painter,
                                contentDescription = "Custom Icon",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }

                // 2. Text Message
                Text(
                    text = message.message,
                    color = contentColor,
                    fontSize = 15.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Medium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // 3. Action Button
                if (action != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onActionClicked() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = action.label,
                            color = contentColor,
                            fontSize = 13.sp,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. Progress Countdown Bar
            if (message.showProgressBar && durationMillis > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(color = Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progressAnimatable.value)
                            .height(3.dp)
                            .background(color = Color.White.copy(alpha = 0.7f))
                    )
                }
            }
        }
    }
}

/**
 * Resolves various custom icon types to a Compose Painter.
 */
@Composable
private fun rememberCustomIconPainter(icon: Any): Painter? {
    return when (icon) {
        is ImageVector -> rememberVectorPainter(icon)
        is Painter -> icon
        is Int -> painterResource(id = icon)
        is Bitmap -> remember(icon) { BitmapPainter(icon.asImageBitmap()) }
        is ImageBitmap -> remember(icon) { BitmapPainter(icon) }
        else -> null
    }
}

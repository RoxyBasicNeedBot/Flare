package com.roxy.flare.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.roxy.flare.FlarePosition

/**
 * Host container for displaying Flare alerts as overlays on top of screen content.
 */
@Composable
fun FlareHost(
    state: FlareHostState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val currentMessage by state.currentMessage

    Box(modifier = modifier.fillMaxSize()) {
        // Render background screen content first
        content()

        // Overlay active alert
        if (currentMessage != null) {
            val message = currentMessage!!
            val alignment = when (message.position) {
                FlarePosition.TOP -> Alignment.TopCenter
                FlarePosition.BOTTOM -> Alignment.BottomCenter
                FlarePosition.CENTER -> Alignment.Center
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = alignment
            ) {
                AnimatedVisibility(
                    visible = true,
                    enter = FlareComposeAnimations.getEnterTransition(message.position, message.animationType),
                    exit = FlareComposeAnimations.getExitTransition(message.position, message.animationType)
                ) {
                    FlareAlert(
                        message = message,
                        onActionClicked = {
                            state.performAction(message)
                        },
                        onDismissRequested = {
                            state.dismiss(message)
                        }
                    )
                }
            }
        }
    }
}

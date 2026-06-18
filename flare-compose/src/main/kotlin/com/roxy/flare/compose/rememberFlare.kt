package com.roxy.flare.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

/**
 * Remember and configure a FlareHostState across recompositions.
 */
@Composable
fun rememberFlareHostState(): FlareHostState {
    val state = remember { FlareHostState() }
    DisposableEffect(state) {
        onDispose {
            state.release()
        }
    }
    return state
}

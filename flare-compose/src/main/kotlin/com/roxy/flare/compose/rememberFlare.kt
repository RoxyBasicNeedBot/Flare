package com.roxy.flare.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Remember and configure a FlareHostState across recompositions.
 */
@Composable
fun rememberFlareHostState(): FlareHostState {
    return remember { FlareHostState() }
}

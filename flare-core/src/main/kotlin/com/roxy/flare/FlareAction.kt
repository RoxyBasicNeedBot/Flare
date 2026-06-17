package com.roxy.flare

/**
 * Configuration for an action button inside the alert.
 */
data class FlareAction(
    val label: String,
    val dismissOnAction: Boolean = true,
    val onClick: () -> Unit
)

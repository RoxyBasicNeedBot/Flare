package com.roxy.flare

/**
 * Control how long the alert is displayed before auto-dismissing.
 */
sealed class FlareDuration {
    abstract val durationMillis: Long

    data object SHORT : FlareDuration() {
        override val durationMillis: Long = 2000L
    }

    data object LONG : FlareDuration() {
        override val durationMillis: Long = 3500L
    }

    data object INDEFINITE : FlareDuration() {
        override val durationMillis: Long = -1L // Stays until manually dismissed
    }

    data class CUSTOM(override val durationMillis: Long) : FlareDuration()
}

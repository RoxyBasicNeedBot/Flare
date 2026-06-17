package com.roxy.flare

/**
 * Representation of the icon to be displayed in the alert.
 */
sealed class FlareIconType {
    data object Default : FlareIconType()
    data class Custom(val icon: Any) : FlareIconType()
    data object None : FlareIconType()
}

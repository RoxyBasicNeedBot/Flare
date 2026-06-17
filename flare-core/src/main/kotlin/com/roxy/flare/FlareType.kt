package com.roxy.flare

/**
 * Defines the type of alert. Each type has its own default background color and icon theme.
 */
sealed class FlareType {
    abstract val defaultColorLong: Long
    abstract val defaultIconColorLong: Long
    abstract val name: String

    data object SUCCESS : FlareType() {
        override val defaultColorLong: Long = 0xFF2E7D32 // Deep Green
        override val defaultIconColorLong: Long = 0xFFFFFFFF
        override val name: String = "SUCCESS"
    }

    data object ERROR : FlareType() {
        override val defaultColorLong: Long = 0xFFD32F2F // Deep Red
        override val defaultIconColorLong: Long = 0xFFFFFFFF
        override val name: String = "ERROR"
    }

    data object WARNING : FlareType() {
        override val defaultColorLong: Long = 0xFFED6C02 // Dark Amber
        override val defaultIconColorLong: Long = 0xFFFFFFFF
        override val name: String = "WARNING"
    }

    data object INFO : FlareType() {
        override val defaultColorLong: Long = 0xFF0288D1 // Light Blue
        override val defaultIconColorLong: Long = 0xFFFFFFFF
        override val name: String = "INFO"
    }

    data object LOADING : FlareType() {
        override val defaultColorLong: Long = 0xFF1976D2 // Blue
        override val defaultIconColorLong: Long = 0xFFFFFFFF
        override val name: String = "LOADING"
    }

    data class CUSTOM(
        override val defaultColorLong: Long,
        override val defaultIconColorLong: Long = 0xFFFFFFFF
    ) : FlareType() {
        override val name: String = "CUSTOM"
    }
}

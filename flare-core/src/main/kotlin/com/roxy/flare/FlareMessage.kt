package com.roxy.flare

import java.util.UUID

/**
 * Representation of a single Flare alert message config.
 */
data class FlareMessage(
    val id: String = UUID.randomUUID().toString(),
    val type: FlareType,
    val message: String,
    val position: FlarePosition,
    val duration: FlareDuration,
    val action: FlareAction? = null,
    val showProgressBar: Boolean = false,
    val haptic: Boolean = true,
    val icon: FlareIconType = FlareIconType.Default,
    val animationType: FlareAnimationType = FlareAnimationType.SLIDE,
    val cornerRadiusDp: Float? = null,
    val customColor: Long? = null,
    val fontResId: Int? = null
)

/**
 * DSL Builder for creating FlareMessage instances.
 */
class FlareMessageBuilder {
    var type: FlareType = FlareType.INFO
    var message: String = ""
    var position: FlarePosition? = null
    var duration: FlareDuration? = null
    var action: FlareAction? = null
    var showProgressBar: Boolean = false
    var haptic: Boolean = true
    var icon: FlareIconType = FlareIconType.Default
    var animationType: FlareAnimationType? = null
    var cornerRadiusDp: Float? = null
    var customColor: Long? = null
    var fontResId: Int? = null

    fun action(label: String, dismissOnAction: Boolean = true, onClick: () -> Unit) {
        action = FlareAction(label, dismissOnAction, onClick)
    }

    fun build(defaults: FlareConfig): FlareMessage {
        return FlareMessage(
            type = type,
            message = message,
            position = position ?: defaults.defaultPosition,
            duration = duration ?: defaults.defaultDuration,
            action = action,
            showProgressBar = showProgressBar,
            haptic = haptic && defaults.hapticEnabled,
            icon = icon,
            animationType = animationType ?: defaults.defaultAnimationType,
            cornerRadiusDp = cornerRadiusDp ?: defaults.cornerRadiusDp,
            customColor = customColor,
            fontResId = fontResId ?: defaults.fontResId
        )
    }
}

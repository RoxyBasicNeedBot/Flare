package com.roxy.flare.android

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.roxy.flare.FlareAction
import com.roxy.flare.FlareAnimationType
import com.roxy.flare.FlareConfig
import com.roxy.flare.FlareDuration
import com.roxy.flare.FlareIconType
import com.roxy.flare.FlareMessage
import com.roxy.flare.FlareMessageBuilder
import com.roxy.flare.FlarePosition
import com.roxy.flare.FlareQueue
import com.roxy.flare.FlareTheme
import com.roxy.flare.FlareType
import java.lang.ref.WeakReference

/**
 * Main entry point for the View system (XML) API of the Flare library.
 */
object Flare {
    private var isLifecycleCallbacksRegistered = false

    /**
     * Start building an alert for the specified Activity.
     */
    fun with(activity: Activity): FlareBuilder {
        registerLifecycleCallbacksIfNeeded(activity.application)
        return FlareBuilder(activity)
    }

    /**
     * Configure global default settings.
     */
    fun configure(block: FlareConfig.() -> Unit) {
        FlareConfig.configure(block)
    }

    /**
     * Override theme globally.
     */
    fun setTheme(theme: FlareTheme) {
        FlareConfig.configure {
            this.theme = theme
        }
    }

    /**
     * Clear all pending and active alerts.
     */
    fun clearQueue() {
        FlareQueue.clear()
        FlareWindowManager.dismissAll()
    }

    private fun registerLifecycleCallbacksIfNeeded(application: Application) {
        if (isLifecycleCallbacksRegistered) return
        isLifecycleCallbacksRegistered = true
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                // Auto-cleanup views associated with this activity to prevent leaks
                FlareWindowManager.dismissAll()
            }
        })
    }

    init {
        // Wire up the queue listener to window manager
        FlareQueue.addListener(object : FlareQueue.FlareQueueListener {
            override fun onShowMessage(message: FlareMessage) {
                // Display the message in the foreground activity
                // Finding the current activity context can be tricky, but since we build via builder,
                // the builder has a weak reference to the activity!
            }

            override fun onDismissMessage(message: FlareMessage) {
                FlareWindowManager.dismiss(message.id)
            }
        })
    }
}

/**
 * Fluent builder class for designing and showing alerts.
 */
class FlareBuilder(activity: Activity) {
    private val activityRef = WeakReference(activity)
    private val builder = FlareMessageBuilder()

    fun type(type: FlareType) = apply { builder.type = type }
    fun message(message: String) = apply { builder.message = message }
    fun position(position: FlarePosition) = apply { builder.position = position }
    fun duration(duration: FlareDuration) = apply { builder.duration = duration }
    
    fun action(label: String, dismissOnAction: Boolean = true, onClick: () -> Unit) = apply {
        builder.action(label, dismissOnAction, onClick)
    }

    fun showProgressBar(show: Boolean) = apply { builder.showProgressBar = show }
    fun haptic(enable: Boolean) = apply { builder.haptic = enable }
    
    fun icon(icon: Any) = apply { builder.icon = FlareIconType.Custom(icon) }
    fun noIcon() = apply { builder.icon = FlareIconType.None }
    
    fun animation(animationType: FlareAnimationType) = apply { builder.animationType = animationType }
    fun cornerRadius(dp: Float) = apply { builder.cornerRadiusDp = dp }
    fun customColor(colorLong: Long) = apply { builder.customColor = colorLong }
    fun font(fontResId: Int) = apply { builder.fontResId = fontResId }

    /**
     * Show the configured alert.
     */
    fun show() {
        val activity = activityRef.get() ?: return
        if (activity.isFinishing || activity.isDestroyed) return

        val defaults = FlareConfig.get()
        val flareMessage = builder.build(defaults)

        // Queue-system listener wrapper for android window manager
        val listener = object : FlareQueue.FlareQueueListener {
            override fun onShowMessage(message: FlareMessage) {
                if (message.id == flareMessage.id) {
                    val act = activityRef.get()
                    if (act != null && !act.isFinishing && !act.isDestroyed) {
                        FlareWindowManager.show(
                            activity = act,
                            message = message,
                            onActionClicked = {
                                message.action?.onClick?.invoke()
                                if (message.action?.dismissOnAction == true) {
                                    FlareWindowManager.dismiss(message.id)
                                }
                            },
                            onDismissed = {
                                FlareWindowManager.dismiss(message.id)
                            }
                        )
                    }
                    FlareQueue.removeListener(this)
                }
            }

            override fun onDismissMessage(message: FlareMessage) {
                if (message.id == flareMessage.id) {
                    FlareWindowManager.dismiss(message.id)
                    FlareQueue.removeListener(this)
                }
            }
        }

        // Add listener and enqueue
        FlareQueue.addListener(listener)
        FlareQueue.enqueue(flareMessage, defaults.queueMode)
    }
}

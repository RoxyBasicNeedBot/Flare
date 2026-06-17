package com.roxy.flare.android

import android.app.Activity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.roxy.flare.FlareInternalApi
import com.roxy.flare.FlareMessage
import com.roxy.flare.FlarePosition

@FlareInternalApi
object FlareWindowManager {

    private val activeViews = mutableMapOf<String, FlareView>()
    private val activeProgressAnimators = mutableMapOf<String, FlareProgressAnimator>()

    fun show(
        activity: Activity,
        message: FlareMessage,
        onActionClicked: () -> Unit,
        onDismissed: () -> Unit
    ) {
        val decorView = activity.window?.decorView as? ViewGroup ?: return

        // 1. Create FlareView
        val flareView = FlareView(
            activity,
            message,
            onActionClicked = onActionClicked,
            onDismissRequested = {
                dismiss(message.id)
            }
        )

        // 2. Setup layout parameters based on position
        val gravity = when (message.position) {
            FlarePosition.TOP -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            FlarePosition.BOTTOM -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            FlarePosition.CENTER -> Gravity.CENTER
        }

        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            this.gravity = gravity
        }

        flareView.layoutParams = lp

        // 3. Apply Window Insets for proper spacing
        ViewCompat.setOnApplyWindowInsetsListener(flareView) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())

            val topPadding = if (message.position == FlarePosition.TOP) {
                Math.max(systemBars.top, displayCutout.top)
            } else 0

            val bottomPadding = if (message.position == FlarePosition.BOTTOM) {
                Math.max(systemBars.bottom, displayCutout.bottom)
            } else 0

            flareView.setPadding(0, topPadding, 0, bottomPadding)
            insets
        }

        // Request insets to apply
        decorView.addView(flareView)
        activeViews[message.id] = flareView

        // 4. Animate entry
        FlareAnimator.animateIn(flareView, message.position, message.animationType) {
            // After anim finishes, check if we should auto-dismiss
            val durationMillis = message.duration.durationMillis
            if (durationMillis > 0) {
                val progressView = flareView.findViewById<android.view.View>(
                    flareView.context.resources.getIdentifier("progress_bar", "id", flareView.context.packageName)
                ) ?: flareView.getChildAt(0).let { rootCard ->
                    // Fallback to finding the progress view from card container
                    if (rootCard is ViewGroup && rootCard.childCount > 1) rootCard.getChildAt(1) else null
                }

                if (progressView != null && message.showProgressBar) {
                    // Create progress animator
                    val progressAnimator = FlareProgressAnimator(progressView, durationMillis) {
                        dismiss(message.id)
                    }
                    activeProgressAnimators[message.id] = progressAnimator
                    progressAnimator.start()
                } else {
                    // Standard post delay auto-dismiss
                    flareView.postDelayed({
                        dismiss(message.id)
                    }, durationMillis)
                }
            }
        }

        // Haptic feedback
        if (message.haptic) {
            FlareHapticHelper.performHapticFeedback(activity)
        }
    }

    fun dismiss(id: String) {
        val flareView = activeViews[id] ?: return
        
        // Cancel progress animators
        activeProgressAnimators[id]?.cancel()
        activeProgressAnimators.remove(id)

        // Remove from list so it can't be dismissed twice
        activeViews.remove(id)

        // Animate exit
        FlareAnimator.animateOut(flareView, flareView.message.position, flareView.message.animationType) {
            val parent = flareView.parent as? ViewGroup
            parent?.removeView(flareView)
            
            // Notify queue manager
            com.roxy.flare.FlareQueue.onMessageDismissed(flareView.message)
        }
    }

    fun dismissAll() {
        val keys = activeViews.keys.toList()
        keys.forEach { dismiss(it) }
    }
}

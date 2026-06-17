package com.roxy.flare.android

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.roxy.flare.FlareInternalApi

@FlareInternalApi
class FlareSwipeDismissHandler(
    private val view: View,
    private val onDismissed: () -> Unit
) : View.OnTouchListener {

    private var downX = 0f
    private var velocityTracker: VelocityTracker? = null
    private var isSwiping = false

    private val springAnimX = SpringAnimation(view, DynamicAnimation.TRANSLATION_X, 0f).apply {
        spring.stiffness = SpringForce.STIFFNESS_MEDIUM
        spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
    }

    private val springAnimAlpha = SpringAnimation(view, DynamicAnimation.ALPHA, 1f).apply {
        spring.stiffness = SpringForce.STIFFNESS_MEDIUM
        spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX
                velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
                isSwiping = false
                // Cancel ongoing animations if user touches again
                springAnimX.cancel()
                springAnimAlpha.cancel()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
                val dx = event.rawX - downX
                if (Math.abs(dx) > 10 || isSwiping) {
                    isSwiping = true
                    view.translationX = dx
                    // Fade out relative to distance (full fade out at width)
                    val alpha = 1f - (Math.abs(dx) / (view.width * 1.2f))
                    view.alpha = Math.max(0.1f, Math.min(1f, alpha))
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isSwiping) {
                    velocityTracker?.addMovement(event)
                    velocityTracker?.computeCurrentVelocity(1000)
                    val xVelocity = velocityTracker?.xVelocity ?: 0f
                    val currentTranslationX = view.translationX
                    val dismissThreshold = view.width * 0.35f

                    val shouldDismiss = Math.abs(currentTranslationX) > dismissThreshold || 
                                       Math.abs(xVelocity) > 1200f

                    if (shouldDismiss) {
                        // Determine exit direction
                        val targetX = if (currentTranslationX > 0 || xVelocity > 0) {
                            view.width.toFloat() + 100f
                        } else {
                            -view.width.toFloat() - 100f
                        }
                        
                        // Slide off screen
                        val exitXAnim = SpringAnimation(view, DynamicAnimation.TRANSLATION_X, targetX).apply {
                            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                            addEndListener { _, _, _, _ ->
                                onDismissed()
                            }
                        }
                        val exitAlphaAnim = SpringAnimation(view, DynamicAnimation.ALPHA, 0f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                        }
                        exitXAnim.start()
                        exitAlphaAnim.start()
                    } else {
                        // Snap back
                        springAnimX.animateToFinalPosition(0f)
                        springAnimAlpha.animateToFinalPosition(1f)
                    }
                } else {
                    // Let regular clicks pass if not swiped
                    v.performClick()
                }
                
                velocityTracker?.recycle()
                velocityTracker = null
                isSwiping = false
                return true
            }
        }
        return false
    }
}

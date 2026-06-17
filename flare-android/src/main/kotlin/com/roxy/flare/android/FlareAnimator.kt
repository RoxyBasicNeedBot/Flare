package com.roxy.flare.android

import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.roxy.flare.FlareAnimationType
import com.roxy.flare.FlareInternalApi
import com.roxy.flare.FlarePosition

@FlareInternalApi
object FlareAnimator {

    fun animateIn(
        view: View,
        position: FlarePosition,
        animationType: FlareAnimationType,
        onAnimationEnd: () -> Unit = {}
    ) {
        view.visibility = View.VISIBLE
        
        when (animationType) {
            FlareAnimationType.SLIDE -> {
                when (position) {
                    FlarePosition.TOP -> {
                        // Slide in from top
                        view.translationY = -600f // fallback start position
                        view.post {
                            val startY = -view.height.toFloat()
                            view.translationY = startY
                            val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0f).apply {
                                spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                                addEndListener { _, _, _, _ -> onAnimationEnd() }
                            }
                            anim.start()
                        }
                    }
                    FlarePosition.BOTTOM -> {
                        // Slide in from bottom
                        view.translationY = 600f // fallback
                        view.post {
                            val startY = view.height.toFloat()
                            view.translationY = startY
                            val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0f).apply {
                                spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                                addEndListener { _, _, _, _ -> onAnimationEnd() }
                            }
                            anim.start()
                        }
                    }
                    FlarePosition.CENTER -> {
                        // Fade in at center
                        view.alpha = 0f
                        val anim = SpringAnimation(view, DynamicAnimation.ALPHA, 1f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                            addEndListener { _, _, _, _ -> onAnimationEnd() }
                        }
                        anim.start()
                    }
                }
            }
            FlareAnimationType.FADE -> {
                view.alpha = 0f
                val anim = SpringAnimation(view, DynamicAnimation.ALPHA, 1f).apply {
                    spring.stiffness = SpringForce.STIFFNESS_LOW
                    spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                    addEndListener { _, _, _, _ -> onAnimationEnd() }
                }
                anim.start()
            }
            FlareAnimationType.BOUNCE -> {
                when (position) {
                    FlarePosition.TOP -> {
                        view.post {
                            val startY = -view.height.toFloat()
                            view.translationY = startY
                            val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0f).apply {
                                spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                                spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY // 0.5f damping creates bounce
                                addEndListener { _, _, _, _ -> onAnimationEnd() }
                            }
                            anim.start()
                        }
                    }
                    FlarePosition.BOTTOM -> {
                        view.post {
                            val startY = view.height.toFloat()
                            view.translationY = startY
                            val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0f).apply {
                                spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                                spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                                addEndListener { _, _, _, _ -> onAnimationEnd() }
                            }
                            anim.start()
                        }
                    }
                    FlarePosition.CENTER -> {
                        // Pop bounce at center (Scale + Alpha)
                        view.alpha = 0f
                        view.scaleX = 0.6f
                        view.scaleY = 0.6f
                        
                        val alphaAnim = SpringAnimation(view, DynamicAnimation.ALPHA, 1f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                        }
                        val scaleXAnim = SpringAnimation(view, DynamicAnimation.SCALE_X, 1f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                        }
                        val scaleYAnim = SpringAnimation(view, DynamicAnimation.SCALE_Y, 1f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                            addEndListener { _, _, _, _ -> onAnimationEnd() }
                        }
                        
                        alphaAnim.start()
                        scaleXAnim.start()
                        scaleYAnim.start()
                    }
                }
            }
        }
    }

    fun animateOut(
        view: View,
        position: FlarePosition,
        animationType: FlareAnimationType,
        onAnimationEnd: () -> Unit = {}
    ) {
        val width = view.width.toFloat()
        val height = view.height.toFloat()

        when (animationType) {
            FlareAnimationType.SLIDE -> {
                when (position) {
                    FlarePosition.TOP -> {
                        val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, -height - 100f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                            addEndListener { _, _, _, _ -> onAnimationEnd() }
                        }
                        anim.start()
                    }
                    FlarePosition.BOTTOM -> {
                        val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, height + 100f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                            addEndListener { _, _, _, _ -> onAnimationEnd() }
                        }
                        anim.start()
                    }
                    FlarePosition.CENTER -> {
                        val anim = SpringAnimation(view, DynamicAnimation.ALPHA, 0f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                            addEndListener { _, _, _, _ -> onAnimationEnd() }
                        }
                        anim.start()
                    }
                }
            }
            FlareAnimationType.FADE -> {
                val anim = SpringAnimation(view, DynamicAnimation.ALPHA, 0f).apply {
                    spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                    spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                    addEndListener { _, _, _, _ -> onAnimationEnd() }
                }
                anim.start()
            }
            FlareAnimationType.BOUNCE -> {
                // Bounce out feels best as a swift slide out or scale down
                when (position) {
                    FlarePosition.TOP -> {
                        val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, -height - 100f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_HIGH
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                            addEndListener { _, _, _, _ -> onAnimationEnd() }
                        }
                        anim.start()
                    }
                    FlarePosition.BOTTOM -> {
                        val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, height + 100f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_HIGH
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                            addEndListener { _, _, _, _ -> onAnimationEnd() }
                        }
                        anim.start()
                    }
                    FlarePosition.CENTER -> {
                        val alphaAnim = SpringAnimation(view, DynamicAnimation.ALPHA, 0f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_HIGH
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                        }
                        val scaleXAnim = SpringAnimation(view, DynamicAnimation.SCALE_X, 0.5f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_HIGH
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                        }
                        val scaleYAnim = SpringAnimation(view, DynamicAnimation.SCALE_Y, 0.5f).apply {
                            spring.stiffness = SpringForce.STIFFNESS_HIGH
                            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
                            addEndListener { _, _, _, _ -> onAnimationEnd() }
                        }
                        alphaAnim.start()
                        scaleXAnim.start()
                        scaleYAnim.start()
                    }
                }
            }
        }
    }
}

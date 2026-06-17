package com.roxy.flare.android

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import com.roxy.flare.FlareInternalApi

@FlareInternalApi
class FlareProgressAnimator(
    private val progressBar: View,
    private val durationMillis: Long,
    private val onCountdownFinished: () -> Unit
) {
    private var animator: ValueAnimator? = null

    fun start() {
        if (durationMillis <= 0) return

        progressBar.visibility = View.VISIBLE
        animator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = durationMillis
            interpolator = LinearInterpolator()
            
            addUpdateListener { anim ->
                val progress = anim.animatedValue as Float
                progressBar.scaleX = progress
            }

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    // Only trigger if progress reached 0 (was not cancelled)
                    if (progressBar.scaleX <= 0.05f) {
                        onCountdownFinished()
                    }
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        
        // Pivot point set to left edge so scaleX scales towards the left
        progressBar.pivotX = 0f
        animator?.start()
    }

    fun pause() {
        animator?.pause()
    }

    fun resume() {
        animator?.resume()
    }

    fun cancel() {
        animator?.cancel()
        animator = null
    }
}

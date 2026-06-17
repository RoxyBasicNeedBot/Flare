package com.roxy.flare.android

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.SystemClock

/**
 * Common base class for programmatic Flare drawables.
 */
abstract class FlareBaseDrawable(var tintColor: Int = Color.WHITE) : Drawable() {
    protected val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT"))
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

/**
 * Draws a checkmark (✓) icon.
 */
class CheckmarkDrawable(tintColor: Int = Color.WHITE) : FlareBaseDrawable(tintColor) {
    private val path = Path()

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        path.reset()
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()
        
        // Checkmark vector path: start middle-left, go down-middle, go up-right
        path.moveTo(w * 0.25f, h * 0.5f)
        path.lineTo(w * 0.45f, h * 0.7f)
        path.lineTo(w * 0.75f, h * 0.3f)
    }

    override fun draw(canvas: Canvas) {
        paint.color = tintColor
        paint.strokeWidth = bounds.width() * 0.1f
        canvas.drawPath(path, paint)
    }
}

/**
 * Draws a cross/X (✗) icon.
 */
class CrossDrawable(tintColor: Int = Color.WHITE) : FlareBaseDrawable(tintColor) {
    override fun draw(canvas: Canvas) {
        paint.color = tintColor
        paint.strokeWidth = bounds.width() * 0.1f
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()
        val padding = w * 0.28f

        // Draw diagonal line 1
        canvas.drawLine(padding, padding, w - padding, h - padding, paint)
        // Draw diagonal line 2
        canvas.drawLine(w - padding, padding, padding, h - padding, paint)
    }
}

/**
 * Draws a warning (⚠) triangle icon.
 */
class WarningDrawable(tintColor: Int = Color.WHITE) : FlareBaseDrawable(tintColor) {
    private val path = Path()

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        path.reset()
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()

        // Draw triangle
        path.moveTo(w * 0.5f, h * 0.15f)
        path.lineTo(w * 0.85f, h * 0.82f)
        path.lineTo(w * 0.15f, h * 0.82f)
        path.close()
    }

    override fun draw(canvas: Canvas) {
        paint.color = tintColor
        paint.strokeWidth = bounds.width() * 0.08f
        canvas.drawPath(path, paint)

        // Draw exclamation point: bar and dot
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()
        
        val dotPaint = Paint(paint).apply {
            style = Paint.Style.FILL
        }

        // Draw the vertical line of exclamation point
        canvas.drawLine(w * 0.5f, h * 0.42f, w * 0.5f, h * 0.62f, paint)
        // Draw the dot
        canvas.drawCircle(w * 0.5f, h * 0.72f, bounds.width() * 0.04f, dotPaint)
    }
}

/**
 * Draws an info (ℹ) circle icon.
 */
class InfoDrawable(tintColor: Int = Color.WHITE) : FlareBaseDrawable(tintColor) {
    override fun draw(canvas: Canvas) {
        paint.color = tintColor
        paint.strokeWidth = bounds.width() * 0.08f
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()
        val radius = w * 0.4f

        // Draw outer circle
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(w * 0.5f, h * 0.5f, radius, paint)

        // Draw info "i": dot and vertical stem
        val dotPaint = Paint(paint).apply {
            style = Paint.Style.FILL
        }
        canvas.drawCircle(w * 0.5f, h * 0.3f, bounds.width() * 0.04f, dotPaint)
        canvas.drawLine(w * 0.5f, h * 0.45f, w * 0.5f, h * 0.7f, paint)
    }
}

/**
 * Draws an animated circular spinner for loading states.
 */
class LoadingSpinnerDrawable(tintColor: Int = Color.WHITE) : FlareBaseDrawable(tintColor), Animatable, Runnable {
    private var isRunning = false
    private var angle = 0f
    private val rectF = RectF()

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        val padding = bounds.width() * 0.15f
        rectF.set(
            bounds.left + padding,
            bounds.top + padding,
            bounds.right - padding,
            bounds.bottom - padding
        )
    }

    override fun draw(canvas: Canvas) {
        paint.color = tintColor
        paint.strokeWidth = bounds.width() * 0.08f
        paint.style = Paint.Style.STROKE

        canvas.save()
        canvas.rotate(angle, bounds.centerX().toFloat(), bounds.centerY().toFloat())
        // Draw a 270 degree arc to create a spinning circle gap
        canvas.drawArc(rectF, 0f, 270f, false, paint)
        canvas.restore()
    }

    override fun start() {
        if (!isRunning) {
            isRunning = true
            run()
        }
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            unscheduleSelf(this)
        }
    }

    override fun isRunning(): Boolean = isRunning

    override fun run() {
        angle = (angle + 10) % 360
        invalidateSelf()
        if (isRunning) {
            scheduleSelf(this, SystemClock.uptimeMillis() + 16) // ~60fps
        }
    }
}

package com.roxy.flare.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.roxy.flare.FlareIconType
import com.roxy.flare.FlareInternalApi
import com.roxy.flare.FlareMessage
import com.roxy.flare.FlareTheme
import com.roxy.flare.FlareType

@SuppressLint("ViewConstructor")
@FlareInternalApi
class FlareView(
    context: Context,
    val message: FlareMessage,
    private val onActionClicked: () -> Unit,
    private val onDismissRequested: () -> Unit
) : FrameLayout(context) {

    private val cardContainer = LinearLayout(context)
    private val contentLayout = LinearLayout(context)
    private val iconView = ImageView(context)
    private val textView = TextView(context)
    private val actionButton = Button(context)
    private val progressBar = View(context)

    init {
        setupLayout()
        applyTheme()
    }

    private fun setupLayout() {
        val dp = context.resources.displayMetrics.density

        // 1. Root Card Container (Vertical: content + progress bar)
        cardContainer.orientation = LinearLayout.VERTICAL
        val cardLp = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            val margin = (16 * dp).toInt()
            setMargins(margin, margin, margin, margin)
        }
        cardContainer.layoutParams = cardLp

        // 2. Content Layout (Horizontal: Icon + Text + Action)
        contentLayout.orientation = LinearLayout.HORIZONTAL
        contentLayout.gravity = Gravity.CENTER_VERTICAL
        val padHorizontal = (16 * dp).toInt()
        val padVertical = (12 * dp).toInt()
        contentLayout.setPadding(padHorizontal, padVertical, padHorizontal, padVertical)

        // 3. Icon Setup
        val iconSize = (24 * dp).toInt()
        val iconLp = LinearLayout.LayoutParams(iconSize, iconSize).apply {
            rightMargin = (12 * dp).toInt()
        }
        iconView.layoutParams = iconLp
        setupIcon()

        // 4. Text Setup
        textView.text = message.message
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        textView.maxLines = 4
        textView.ellipsize = TextUtils.TruncateAt.END
        val textLp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        textView.layoutParams = textLp
        
        // Font customization
        message.fontResId?.let { fontId ->
            try {
                textView.typeface = ResourcesCompat.getFont(context, fontId)
            } catch (e: Exception) {
                textView.typeface = Typeface.DEFAULT
            }
        } ?: run {
            textView.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        }

        // 5. Action Button Setup
        val action = message.action
        if (action != null) {
            actionButton.text = action.label
            actionButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            actionButton.typeface = textView.typeface
            actionButton.isAllCaps = false
            
            // Clean transparent background for text button style
            actionButton.background = GradientDrawable().apply {
                setColor(Color.TRANSPARENT)
            }
            val actLp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (36 * dp).toInt()
            ).apply {
                leftMargin = (12 * dp).toInt()
                gravity = Gravity.CENTER_VERTICAL
            }
            actionButton.layoutParams = actLp
            actionButton.setPadding((12 * dp).toInt(), 0, (12 * dp).toInt(), 0)
            actionButton.setOnClickListener {
                onActionClicked()
            }
        } else {
            actionButton.visibility = GONE
        }

        // Add to Content Layout
        contentLayout.addView(iconView)
        contentLayout.addView(textView)
        if (action != null) {
            contentLayout.addView(actionButton)
        }

        // 6. Progress Bar Setup
        val progressHeight = (3 * dp).toInt()
        val progressLp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            progressHeight
        )
        progressBar.layoutParams = progressLp
        progressBar.visibility = if (message.showProgressBar) VISIBLE else GONE

        // Add to Root Card Container
        cardContainer.addView(contentLayout)
        cardContainer.addView(progressBar)

        addView(cardContainer)

        // Setup Swipe to Dismiss
        setOnTouchListener(FlareSwipeDismissHandler(cardContainer) {
            onDismissRequested()
        })
    }

    private fun setupIcon() {
        val type = message.type
        when (val iconType = message.icon) {
            is FlareIconType.None -> {
                iconView.visibility = GONE
            }
            is FlareIconType.Default -> {
                iconView.visibility = VISIBLE
                val drawable = when (type) {
                    is FlareType.SUCCESS -> CheckmarkDrawable(Color.WHITE)
                    is FlareType.ERROR -> CrossDrawable(Color.WHITE)
                    is FlareType.WARNING -> WarningDrawable(Color.WHITE)
                    is FlareType.INFO -> InfoDrawable(Color.WHITE)
                    is FlareType.LOADING -> LoadingSpinnerDrawable(Color.WHITE).apply { start() }
                    is FlareType.CUSTOM -> InfoDrawable(Color.WHITE)
                }
                iconView.setImageDrawable(drawable)
            }
            is FlareIconType.Custom -> {
                iconView.visibility = VISIBLE
                when (val customIcon = iconType.icon) {
                    is Int -> iconView.setImageResource(customIcon)
                    is android.graphics.Bitmap -> iconView.setImageBitmap(customIcon)
                    is android.graphics.drawable.Drawable -> iconView.setImageDrawable(customIcon)
                    else -> iconView.visibility = GONE // fallback
                }
            }
        }
    }

    private fun applyTheme() {
        val dp = context.resources.displayMetrics.density
        val configTheme = com.roxy.flare.FlareConfig.get().theme

        val isDark = when (configTheme) {
            FlareTheme.DARK -> true
            FlareTheme.LIGHT -> false
            FlareTheme.AUTO -> {
                val currentMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                currentMode == Configuration.UI_MODE_NIGHT_YES
            }
        }

        // Determine base color based on type or override
        val baseColor = message.customColor?.toInt() ?: message.type.defaultColorLong.toInt()
        
        // Draw card background
        val cornerRadius = (message.cornerRadiusDp ?: 12f) * dp
        val cardBg = GradientDrawable().apply {
            setColor(baseColor)
            setCornerRadius(cornerRadius)
        }
        cardContainer.background = cardBg

        // Text & Action button styling
        val textColor = if (isDark) Color.WHITE else Color.WHITE // High contrast defaults
        textView.setTextColor(textColor)
        
        if (message.action != null) {
            actionButton.setTextColor(textColor)
            // Draw a subtle border outline on the action button for premium look
            val actionBg = GradientDrawable().apply {
                setColor(Color.TRANSPARENT)
                setCornerRadius(4f * dp)
                setStroke((1f * dp).toInt(), Color.argb(120, 255, 255, 255))
            }
            actionButton.background = actionBg
        }

        // Progress bar background styling
        val progressColor = Color.argb(180, 255, 255, 255)
        progressBar.setBackgroundColor(progressColor)

        // Tint icon if default or custom is tintable
        if (message.icon is FlareIconType.Default) {
            iconView.imageTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Stop animation if it's a loading spinner
        val drawable = iconView.drawable
        if (drawable is android.graphics.drawable.Animatable) {
            drawable.stop()
        }
    }
}

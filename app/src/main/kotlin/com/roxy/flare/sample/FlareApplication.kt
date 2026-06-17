package com.roxy.flare.sample

import android.app.Application
import com.roxy.flare.FlareDuration
import com.roxy.flare.FlarePosition
import com.roxy.flare.FlareTheme
import com.roxy.flare.android.Flare

class FlareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Configure global defaults for Flare alerts on application start
        Flare.configure {
            defaultPosition = FlarePosition.BOTTOM
            defaultDuration = FlareDuration.SHORT
            hapticEnabled = true
            theme = FlareTheme.AUTO // Follows light/dark system theme
            cornerRadiusDp = 12f
        }
    }
}

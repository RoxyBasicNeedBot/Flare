package com.roxy.flare

/**
 * Global configuration options for Flare.
 */
class FlareConfig private constructor() {
    var defaultPosition: FlarePosition = FlarePosition.BOTTOM
    var defaultDuration: FlareDuration = FlareDuration.SHORT
    var hapticEnabled: Boolean = true
    var theme: FlareTheme = FlareTheme.AUTO
    var fontResId: Int? = null
    var cornerRadiusDp: Float = 12f
    var defaultAnimationType: FlareAnimationType = FlareAnimationType.SLIDE
    var queueMode: FlareQueueMode = FlareQueueMode.ENQUEUE

    companion object {
        @Volatile
        private var instance: FlareConfig = FlareConfig()

        fun get(): FlareConfig = instance

        /**
         * Configure global default options.
         */
        fun configure(block: FlareConfig.() -> Unit) {
            synchronized(this) {
                instance.block()
            }
        }
    }
}

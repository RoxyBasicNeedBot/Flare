package com.roxy.flare

/**
 * Strategy for handling multiple overlapping alert requests.
 */
enum class FlareQueueMode {
    ENQUEUE, // Add to queue behind current
    REPLACE  // Cancel current immediately and show new one
}

package com.roxy.flare.compose

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.roxy.flare.FlareConfig
import com.roxy.flare.FlareMessage
import com.roxy.flare.FlareMessageBuilder
import com.roxy.flare.FlareQueue
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume

/**
 * Result of showing a Flare alert.
 */
enum class FlareResult {
    Dismissed,
    ActionPerformed
}

/**
 * Host state for displaying Flare alerts in Jetpack Compose.
 */
class FlareHostState {
    private val _currentMessage = mutableStateOf<FlareMessage?>(null)
    val currentMessage: State<FlareMessage?> = _currentMessage

    private val continuations = ConcurrentHashMap<String, (FlareResult) -> Unit>()

    private val queueListener = object : FlareQueue.FlareQueueListener {
        override fun onShowMessage(message: FlareMessage) {
            _currentMessage.value = message
        }

        override fun onDismissMessage(message: FlareMessage) {
            if (_currentMessage.value?.id == message.id) {
                _currentMessage.value = null
            }
            continuations[message.id]?.invoke(FlareResult.Dismissed)
            continuations.remove(message.id)
        }
    }

    init {
        FlareQueue.addListener(queueListener)
    }

    /**
     * Show a new Flare alert and suspend until it is dismissed or action clicked.
     */
    suspend fun show(builder: FlareMessageBuilder.() -> Unit): FlareResult {
        val defaults = FlareConfig.get()
        val messageBuilder = FlareMessageBuilder().apply(builder)
        val message = messageBuilder.build(defaults)

        return suspendCancellableCoroutine { continuation ->
            continuations[message.id] = { result ->
                continuation.resume(result)
            }

            continuation.invokeOnCancellation {
                continuations.remove(message.id)
                FlareQueue.onMessageDismissed(message)
                if (_currentMessage.value?.id == message.id) {
                    _currentMessage.value = null
                }
            }

            // Enqueue using core manager
            FlareQueue.enqueue(message, defaults.queueMode)
        }
    }

    /**
     * Notify that the action was clicked.
     */
    fun performAction(message: FlareMessage) {
        continuations[message.id]?.invoke(FlareResult.ActionPerformed)
        continuations.remove(message.id)
        if (message.action?.dismissOnAction == true) {
            dismiss(message)
        }
    }

    /**
     * Dismiss the specified message.
     */
    fun dismiss(message: FlareMessage) {
        FlareQueue.onMessageDismissed(message)
        if (_currentMessage.value?.id == message.id) {
            _currentMessage.value = null
        }
        continuations[message.id]?.invoke(FlareResult.Dismissed)
        continuations.remove(message.id)
    }

    /**
     * Clear all pending and active alerts.
     */
    fun clearQueue() {
        FlareQueue.clear()
        _currentMessage.value = null
        continuations.values.forEach { it(FlareResult.Dismissed) }
        continuations.clear()
    }
}

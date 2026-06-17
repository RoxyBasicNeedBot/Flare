package com.roxy.flare

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Thread-safe alert queue manager.
 */
object FlareQueue {
    private val queue = ConcurrentLinkedQueue<FlareMessage>()
    private val listeners = mutableListOf<FlareQueueListener>()

    @Volatile
    private var currentMessage: FlareMessage? = null

    interface FlareQueueListener {
        fun onShowMessage(message: FlareMessage)
        fun onDismissMessage(message: FlareMessage)
    }

    fun addListener(listener: FlareQueueListener) {
        synchronized(listeners) {
            listeners.add(listener)
            // If there's already an active message, notify immediately
            currentMessage?.let { listener.onShowMessage(it) }
        }
    }

    fun removeListener(listener: FlareQueueListener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

    fun getCurrent(): FlareMessage? = currentMessage

    /**
     * Add a message to the queue, or replace the current one depending on settings.
     */
    fun enqueue(message: FlareMessage, mode: FlareQueueMode) {
        if (mode == FlareQueueMode.REPLACE) {
            val current = currentMessage
            if (current != null) {
                currentMessage = null
                notifyDismiss(current)
            }
            queue.clear()
            currentMessage = message
            notifyShow(message)
        } else {
            queue.add(message)
            processNext()
        }
    }

    /**
     * Mark the current message as finished and show the next one in the queue.
     */
    fun onMessageDismissed(message: FlareMessage) {
        if (currentMessage?.id == message.id) {
            currentMessage = null
            processNext()
        }
    }

    /**
     * Clear all pending messages in the queue and dismiss the current one.
     */
    fun clear() {
        queue.clear()
        val current = currentMessage
        if (current != null) {
            currentMessage = null
            notifyDismiss(current)
        }
    }

    private fun processNext() {
        if (currentMessage != null) return

        val next = queue.poll()
        if (next != null) {
            currentMessage = next
            notifyShow(next)
        }
    }

    private fun notifyShow(message: FlareMessage) {
        val targets = synchronized(listeners) { listeners.toList() }
        targets.forEach { it.onShowMessage(message) }
    }

    private fun notifyDismiss(message: FlareMessage) {
        val targets = synchronized(listeners) { listeners.toList() }
        targets.forEach { it.onDismissMessage(message) }
    }
}

package com.roxy.flare

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Thread-safe alert queue manager.
 */
object FlareQueue {
    private val stateLock = Any()
    private val queue = ConcurrentLinkedQueue<FlareMessage>()
    private val listeners = mutableListOf<FlareQueueListener>()

    @Volatile
    private var currentMessage: FlareMessage? = null

    interface FlareQueueListener {
        fun onShowMessage(message: FlareMessage) {}
        fun onDismissMessage(message: FlareMessage) {}
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
        val toDismissList = mutableListOf<FlareMessage>()
        var toShow: FlareMessage? = null

        synchronized(stateLock) {
            if (mode == FlareQueueMode.REPLACE) {
                currentMessage?.let {
                    currentMessage = null
                    toDismissList.add(it)
                }
                var next = queue.poll()
                while (next != null) {
                    toDismissList.add(next)
                    next = queue.poll()
                }
                currentMessage = message
                toShow = message
            } else {
                queue.add(message)
                toShow = processNextState()
            }
        }

        toDismissList.forEach { notifyDismiss(it) }
        toShow?.let { notifyShow(it) }
    }

    /**
     * Mark the current message as finished and show the next one in the queue.
     */
    fun onMessageDismissed(message: FlareMessage) {
        var toDismiss: FlareMessage? = null
        var toShow: FlareMessage? = null

        synchronized(stateLock) {
            if (currentMessage?.id == message.id) {
                currentMessage = null
                toDismiss = message
                toShow = processNextState()
            }
        }

        toDismiss?.let { notifyDismiss(it) }
        toShow?.let { notifyShow(it) }
    }

    /**
     * Clear all pending messages in the queue and dismiss the current one.
     */
    fun clear() {
        val toDismissList = mutableListOf<FlareMessage>()

        synchronized(stateLock) {
            var next = queue.poll()
            while (next != null) {
                toDismissList.add(next)
                next = queue.poll()
            }
            currentMessage?.let {
                currentMessage = null
                toDismissList.add(it)
            }
        }

        toDismissList.forEach { notifyDismiss(it) }
    }

    private fun processNextState(): FlareMessage? {
        synchronized(stateLock) {
            if (currentMessage != null) return null
            val next = queue.poll()
            if (next != null) {
                currentMessage = next
                return next
            }
            return null
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

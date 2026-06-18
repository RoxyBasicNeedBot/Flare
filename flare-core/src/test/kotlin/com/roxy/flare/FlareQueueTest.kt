package com.roxy.flare

import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class FlareQueueTest {

    @Test
    fun testConcurrentEnqueue() {
        val queue = FlareQueue
        queue.clear()

        val threadCount = 10
        val latch = CountDownLatch(threadCount)
        val showCount = AtomicInteger(0)

        val listener = object : FlareQueue.FlareQueueListener {
            override fun onShowMessage(message: FlareMessage) {
                showCount.incrementAndGet()
            }
        }
        queue.addListener(listener)

        val threads = List(threadCount) { i ->
            Thread {
                try {
                    val msg = FlareMessage(
                        id = "msg_$i",
                        message = "Message $i",
                        type = FlareType.INFO,
                        position = FlarePosition.BOTTOM,
                        duration = FlareDuration.SHORT,
                        action = null,
                        showProgressBar = false,
                        haptic = false,
                        icon = FlareIconType.Default,
                        animationType = FlareAnimationType.SLIDE,
                        customColor = null,
                        cornerRadiusDp = null,
                        fontResId = null
                    )
                    queue.enqueue(msg, FlareQueueMode.QUEUE)
                } finally {
                    latch.countDown()
                }
            }
        }

        threads.forEach { it.start() }
        val completed = latch.await(5, TimeUnit.SECONDS)
        assertTrue("Threads did not complete in time", completed)

        // Assert only ONE message is active
        val current = queue.getCurrent()
        assertNotNull("Current message should not be null", current)

        // Verify listener was only notified of the first message being shown
        assertEquals("Show count should be 1 since subsequent messages are queued", 1, showCount.get())

        queue.removeListener(listener)
        queue.clear()
    }

    @Test
    fun testClearQueueNotifiesAndRemovesListeners() {
        val queue = FlareQueue
        queue.clear()

        val dismissCounts = ConcurrentHashMapMock()
        val registeredListeners = mutableListOf<FlareQueue.FlareQueueListener>()

        // Helper mock listener that tracks all dismissals received
        class SpyListener(val targetId: String) : FlareQueue.FlareQueueListener {
            val dismissalsReceived = mutableListOf<String>()

            override fun onDismissMessage(message: FlareMessage) {
                dismissalsReceived.add(message.id)
                if (message.id == targetId) {
                    dismissCounts.increment(targetId)
                    queue.removeListener(this)
                }
            }
        }

        // Enqueue 3 messages with 3 separate spy listeners
        val messages = List(3) { i ->
            val msgId = "clear_msg_${i + 1}"
            val msg = FlareMessage(
                id = msgId,
                message = "Message ${i + 1}",
                type = FlareType.INFO,
                position = FlarePosition.BOTTOM,
                duration = FlareDuration.SHORT,
                action = null,
                showProgressBar = false,
                haptic = false,
                icon = FlareIconType.Default,
                animationType = FlareAnimationType.SLIDE,
                customColor = null,
                cornerRadiusDp = null,
                fontResId = null
            )

            val listener = SpyListener(msgId)
            registeredListeners.add(listener)
            queue.addListener(listener)
            queue.enqueue(msg, FlareQueueMode.QUEUE)
            msg
        }

        // Clear the queue. It should dismiss the active message (msg1) and the 2 pending messages (msg2, msg3).
        queue.clear()

        // Verify all 3 listeners received their own dismissals and unregistered themselves
        assertEquals("msg_1 should be dismissed", 1, dismissCounts.get("clear_msg_1"))
        assertEquals("msg_2 should be dismissed", 1, dismissCounts.get("clear_msg_2"))
        assertEquals("msg_3 should be dismissed", 1, dismissCounts.get("clear_msg_3"))

        // Create a new message to verify the listeners were actually removed
        val testMsg = FlareMessage(
            id = "test_msg_leak",
            message = "Test Leak message",
            type = FlareType.INFO,
            position = FlarePosition.BOTTOM,
            duration = FlareDuration.SHORT,
            action = null,
            showProgressBar = false,
            haptic = false,
            icon = FlareIconType.Default,
            animationType = FlareAnimationType.SLIDE,
            customColor = null,
            cornerRadiusDp = null,
            fontResId = null
        )

        // Register a clean, unrelated listener for this test message
        val cleanListener = object : FlareQueue.FlareQueueListener {
            override fun onDismissMessage(message: FlareMessage) {
                if (message.id == "test_msg_leak") {
                    queue.removeListener(this)
                }
            }
        }
        queue.addListener(cleanListener)
        queue.enqueue(testMsg, FlareQueueMode.QUEUE)
        
        // Dismiss the test message
        queue.onMessageDismissed(testMsg)

        // None of the cleared spy listeners should have received "test_msg_leak",
        // since they should have successfully removed themselves on clear dismissal
        registeredListeners.forEach { listener ->
            val spy = listener as SpyListener
            assertFalse(
                "Cleared listener for ${spy.targetId} should not have received test_msg_leak",
                spy.dismissalsReceived.contains("test_msg_leak")
            )
        }
    }

    private class ConcurrentHashMapMock {
        private val map = java.util.concurrent.ConcurrentHashMap<String, Int>()

        fun increment(key: String) {
            map.merge(key, 1) { old, new -> old + new }
        }

        fun get(key: String): Int = map[key] ?: 0
    }
}

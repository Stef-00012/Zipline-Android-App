package com.stefdp.zipline.transferservice

class ProgressTracker {
    private var startTime: Long = System.currentTimeMillis()
    private var lastUpdateTime: Long = startTime
    private var lastBytesTransferred: Long = 0L
    private var smoothedSpeed: Double = 0.0

    fun reset() {
        startTime = System.currentTimeMillis()
        lastUpdateTime = startTime
        lastBytesTransferred = 0L
        smoothedSpeed = 0.0
    }

    fun update(bytesTransferred: Long): Double {
        val now = System.currentTimeMillis()
        val timeDelta = (now - lastUpdateTime).coerceAtLeast(1)
        val bytesDelta = bytesTransferred - lastBytesTransferred

        val instantSpeed = (bytesDelta.toDouble() / timeDelta) * 1000.0

        // Exponential moving average for smooth speed
        smoothedSpeed = if (smoothedSpeed == 0.0) {
            instantSpeed
        } else {
            smoothedSpeed * 0.7 + instantSpeed * 0.3
        }

        lastUpdateTime = now
        lastBytesTransferred = bytesTransferred

        return smoothedSpeed
    }
}
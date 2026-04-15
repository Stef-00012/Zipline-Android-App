package com.stefdp.zipline.utils

import kotlin.math.round

fun getMetricsDifference(
    firstMetric: Double,
    lastMetric: Double
): Double {
    return round(((firstMetric - lastMetric) / lastMetric) * 100).takeUnless { it.isNaN() } ?: 0.0
}
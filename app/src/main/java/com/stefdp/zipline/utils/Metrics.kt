package com.stefdp.zipline.utils

import kotlin.math.round

fun getMetricsDifference(
    firstMetric: Double,
    lastMetric: Double
): Double {
    return round(((firstMetric - lastMetric) / lastMetric) * 100)
}
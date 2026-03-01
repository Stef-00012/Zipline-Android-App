package com.stefdp.zipline.network.models.requests

data class RunRequerySizeJobBody(
    val forceDelete: Boolean = false,
    val forceUpdate: Boolean = false,
)

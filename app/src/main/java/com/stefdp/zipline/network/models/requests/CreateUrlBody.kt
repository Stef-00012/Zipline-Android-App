package com.stefdp.zipline.network.models.requests

data class CreateUrlBody(
    val destination: String,
    val vanity: String? = null,
    val enabled: Boolean? = null,
)
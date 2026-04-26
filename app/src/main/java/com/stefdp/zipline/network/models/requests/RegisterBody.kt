package com.stefdp.zipline.network.models.requests

data class RegisterBody(
    val username: String,
    val password: String,
    val code: String? = null,
)
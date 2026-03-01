package com.stefdp.zipline.network.models.requests

data class LoginBody(
    val username: String,
    val password: String,
    val code: String? = null,
)
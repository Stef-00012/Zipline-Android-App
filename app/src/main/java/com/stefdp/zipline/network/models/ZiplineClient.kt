package com.stefdp.zipline.network.models

data class ZiplineClient(
    val client: String,
    val device: String,
    val ua: String
) {
    override fun toString(): String  = "{\"client\":\"$client\",\"device\":\"$device\",\"ua\":\"$ua\"}"
}
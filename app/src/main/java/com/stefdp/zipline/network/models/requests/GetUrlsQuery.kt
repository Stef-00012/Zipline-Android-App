package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName

enum class GetUrlsQuerySearchField(val value: String) {
    @SerializedName("destination")
    DESTINATION("destination"),

    @SerializedName("vanity")
    VANITY("vanity"),

    @SerializedName("code")
    CODE("code");

    override fun toString(): String = value
}
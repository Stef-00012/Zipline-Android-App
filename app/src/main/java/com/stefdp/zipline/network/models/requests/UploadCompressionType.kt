package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName

enum class UploadCompressionType(val value: String) {
    @SerializedName("jpg")
    JPG("jpg"),

    @SerializedName("png")
    PNG("png"),

    @SerializedName("webp")
    WEBP("webp"),

    @SerializedName("jxl")
    JXL("jxl");

    override fun toString(): String = value
}
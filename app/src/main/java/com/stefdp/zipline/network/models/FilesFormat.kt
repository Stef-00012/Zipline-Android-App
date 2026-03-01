package com.stefdp.zipline.network.models

import com.google.gson.annotations.SerializedName

enum class FilesFormat(val value: String) {
    @SerializedName("random")
    RANDOM("random"),

    @SerializedName("date")
    DATE("date"),

    @SerializedName("uuid")
    UUID("uuid"),

    @SerializedName("gfycat")
    GFYCAT("gfycat"),

    @SerializedName("random-words")
    RANDOM_WORDS("random-words");

    override fun toString(): String = value
}
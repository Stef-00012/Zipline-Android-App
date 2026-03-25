package com.stefdp.zipline.utils

import com.google.gson.annotations.SerializedName

enum class SortOrder(val value: String?) {
    @SerializedName("asc")
    ASC("asc"),

    @SerializedName("desc")
    DESC("desc"),

    @SerializedName("")
    UNSPECIFIED(null);

    override fun toString(): String = value.toString()
}
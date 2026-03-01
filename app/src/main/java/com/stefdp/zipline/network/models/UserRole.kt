package com.stefdp.zipline.network.models

import com.google.gson.annotations.SerializedName

enum class UserRole(val value: String) {
    @SerializedName("USER")
    USER("USER"),

    @SerializedName("ADMIN")
    ADMIN("ADMIN"),

    @SerializedName("SUPERADMIN")
    SUPERADMIN("SUPERADMIN");

    override fun toString(): String = value
}
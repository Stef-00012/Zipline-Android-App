package com.stefdp.zipline.network.models

import com.google.gson.annotations.SerializedName

enum class UserRole(
    val value: String,
    val level: Int,
    val roleName: String,
) {
    @SerializedName("USER")
    USER(
        value = "USER",
        level = 2,
        roleName = "User"
    ),

    @SerializedName("ADMIN")
    ADMIN(
        value = "ADMIN",
        level = 1,
        roleName = "Administrator"
    ),

    @SerializedName("SUPERADMIN")
    SUPERADMIN(
        value = "SUPERADMIN",
        level = 0,
        roleName = "Super Administrator"
    );

    override fun toString(): String = value
}
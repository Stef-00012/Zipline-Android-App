package com.stefdp.zipline.network.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val username: String,
    val createdAt: String,
    val updatedAt: String,
    val role: UserRole,
    val view: UserViewSettings,
    val sessions: List<UserSession>,
    val oauthProviders: List<OAuthProvider>,
    val totpSecret: String? = null,
    val passkeys: List<UserPasskey>?,
    val quota: UserQuota? = null,
    val avatar: String? = null,
    val password: String? = null,
    val token: String? = null,
)

data class UserViewSettings(
    val enabled: Boolean? = null,
    val align: UserViewSettingsAlign? = null,
    val showMimetype: Boolean? = null,
    val showTags: Boolean? = null,
    val showFolder: Boolean? = null,
    val content: String? = null,
    val embed: Boolean? = null,
    val embedTitle: String? = null,
    val embedDescription: String? = null,
    val embedColor: String? = null,
    val embedSiteName: String? = null,
)

enum class UserViewSettingsAlign(val value: String) {
    @SerializedName("left")
    LEFT("left"),

    @SerializedName("center")
    CENTER("center"),

    @SerializedName("right")
    RIGHT("right");

    override fun toString(): String = value
}

data class UserSession(
    val id: String,
    val createdAt: String,
    @SerializedName("ua") val userAgent: String,
    val client: String,
    val device: String,
    val userId: String,
)

data class OAuthProvider(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val userId: String,
    val provider: OAuthProviderProvider,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val oauthId: String,
)

enum class OAuthProviderProvider(val value: String) {
    @SerializedName("DISCORD")
    DISCORD("DISCORD"),

    @SerializedName("GOOGLE")
    GOOGLE("GOOGLE"),

    @SerializedName("GITHUB")
    GITHUB("GITHUB"),

    @SerializedName("OIDC")
    OIDC("OIDC");

    override fun toString(): String = value
}

data class UserPasskey(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val lastUsed: String? = null,
    val name: String,
    val reg: UserPasskeyReg,
    val userId: String,
)

data class UserPasskeyReg(
    val id: String,
    val type: String,
    val rawId: String,
    val response: UserPasskeyRegResponse,
    val clientExtensionResults: UserPasskeyRegClientExtensionResults,
)

data class UserPasskeyRegResponse(
    val transports: List<String>,
    val attestationObject: String,
    val clientDataJSON: String,
)

data class UserPasskeyRegClientExtensionResults(
    val credProps: Any? = null,
)

data class UserQuota(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val filesQuota: UserQuotaFilesQuota,
    val maxBytes: String?,
    val maxFiles: Long?,
    val maxUrls: Long?,
    val userId: String,
)

enum class UserQuotaFilesQuota(val value: String) {
    @SerializedName("NONE")
    NONE("NONE"),

    @SerializedName("BY_FILES")
    BY_FILES("BY_FILES"),

    @SerializedName("BY_BYTES")
    BY_BYTES("BY_BYTES");

    override fun toString(): String = value
}
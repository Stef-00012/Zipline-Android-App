package com.stefdp.zipline.network.models.requests

import com.stefdp.zipline.network.models.UserQuotaFilesQuota
import com.stefdp.zipline.network.models.UserRole

data class UpdateUserBody(
    val username: String? = null,
    val password: String? = null,
    val avatar: String? = null,
    val role: UserRole? = null,
    val quota: UpdateUserBodyQuota? = null,
)

data class UpdateUserBodyQuota(
    val filesType: UserQuotaFilesQuota? = null,
    val maxFiles: Long? = null,
    val maxBytes: String? = null,
    val maxUrls: Long? = null,
)

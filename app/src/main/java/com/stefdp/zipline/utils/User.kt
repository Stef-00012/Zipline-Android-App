package com.stefdp.zipline.utils

import com.stefdp.zipline.network.models.UserRole

val targetAllowedRoles = listOf(
    UserRole.USER,
    UserRole.ADMIN,
)

fun canInteract(currentUserRole: UserRole?, targetUseRole: UserRole?): Boolean = (
    (currentUserRole == UserRole.SUPERADMIN && targetUseRole in targetAllowedRoles) ||
    (currentUserRole == UserRole.ADMIN && targetUseRole == UserRole.USER)
)
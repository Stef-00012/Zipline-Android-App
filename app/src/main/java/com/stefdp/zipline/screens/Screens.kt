package com.stefdp.zipline.screens

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppScreen

@Serializable
object LoadingScreen : AppScreen

@Serializable
object LoginScreen : AppScreen

@Serializable
object BiometricAuthScreen : AppScreen

@Serializable
object HomeScreen : AppScreen

@Serializable
object MetricsScreen : AppScreen

@Serializable
data class FilesScreen(val userId: String? = null) : AppScreen


@Serializable
object FoldersScreen : AppScreen

@Serializable
data class UploadFileScreen(val files: List<String>? = null) : AppScreen

@Serializable
data class UploadTextScreen(val text: String? = null) : AppScreen

@Serializable
data class UrlsScreen(val url: String? = null) : AppScreen

@Serializable
object AdminSettingsScreen : AppScreen

@Serializable
object AdminUsersScreen : AppScreen

@Serializable
object AdminActionsScreen : AppScreen

@Serializable
object AdminInvitesScreen : AppScreen

@Serializable
object SettingsScreen : AppScreen
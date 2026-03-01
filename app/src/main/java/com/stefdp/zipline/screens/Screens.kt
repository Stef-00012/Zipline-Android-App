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
object FilesScreen : AppScreen

@Serializable
object FoldersScreen : AppScreen

@Serializable
object UploadFileScreen : AppScreen

@Serializable
object UploadTextScreen : AppScreen

@Serializable
object ShortenURLScreen : AppScreen

@Serializable
object AdminSettingsScreen : AppScreen

@Serializable
object AdminUsersScreen : AppScreen

@Serializable
object AdminInvitesScreen : AppScreen

@Serializable
object SettingsScreen : AppScreen
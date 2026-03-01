package com.stefdp.zipline.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.stefdp.zipline.R

fun getBiometricStatus(
    context: Context,
    authenticators: Int = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
): Int {
    val biometricManager = BiometricManager.from(context)

    return biometricManager.canAuthenticate(authenticators)
}

fun promptBiometricAuthentication(
    activity: FragmentActivity,
    promptInfo: BiometricPrompt.PromptInfo,
    prompt: BiometricPrompt,
    onNoHardwareError: () -> Unit = {},
    onHardwareUnavailableError: () -> Unit = {},
    onBiometricNotEnrolledError: () -> Unit = {},
) {
    when (getBiometricStatus(activity)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            prompt.authenticate(promptInfo)
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            onNoHardwareError()
        }

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            onHardwareUnavailableError()
        }

        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            onBiometricNotEnrolledError()
        }
    }
}

fun createBiometricPrompt(
    activity: FragmentActivity,
    onError: (errorCode: Int, errString: CharSequence) -> Unit = { _, _ -> },
    onFailed: () -> Unit = {},
    onSuccess: (result: BiometricPrompt.AuthenticationResult) -> Unit = {}
): BiometricPrompt {
    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)

            onError(errorCode, errString)
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()

            onFailed()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)

            onSuccess(result)
        }
    }

    val executor = ContextCompat.getMainExecutor(activity)
    return BiometricPrompt(activity, executor, callback)
}

fun createPromptInfo(
    context: Context,
    title: String = context.getString(R.string.biometric_prompt_default_title),
    subTitle: String? = null,
    description: String? = null,
    allowedAuthenticators: Int = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL,
    confirmationRequired: Boolean = false,
    negativeButtonText: String? = context.getString(R.string.biometric_prompt_default_negative_button)
): BiometricPrompt.PromptInfo {
    val builder = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setDescription(description)
        .setSubtitle(subTitle)
        .setAllowedAuthenticators(allowedAuthenticators)
        .setConfirmationRequired(confirmationRequired)

    if (
        negativeButtonText != null &&
        (allowedAuthenticators and BiometricManager.Authenticators.DEVICE_CREDENTIAL) == 0
    ) {
        builder.setNegativeButtonText(negativeButtonText)
    }

    return builder.build()
}

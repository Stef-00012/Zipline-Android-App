package com.stefdp.zipline.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import android.util.Base64

val Context.dataStore by preferencesDataStore(name = "hackatime_settings")

class SecureStorage private constructor(context: Context) {
     private val dataStore = context.applicationContext.dataStore

    companion object {
        @Volatile
        private var INSTANCE: SecureStorage? = null

        fun getInstance(context: Context): SecureStorage {
            return INSTANCE ?: synchronized(this) {
                val instance = SecureStorage(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun set(key: String, value: String) {
        val dataKey = stringPreferencesKey(key)
        val encryptedBytes = CryptoManager.encrypt(value.toByteArray())
        val base64Value = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

        dataStore.edit { it[dataKey] = base64Value }
    }

    suspend fun get(key: String): String? {
        val dataKey = stringPreferencesKey(key)
        val base64Value = dataStore.data.first()[dataKey] ?: return null

        return try {
            val encryptedBytes = Base64.decode(base64Value, Base64.NO_WRAP)
            val decryptedBytes = CryptoManager.decrypt(encryptedBytes)

            String(decryptedBytes)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun del(key: String): Boolean {
        return try {
            val dataKey = stringPreferencesKey(key)

            dataStore.edit { preferences ->
                preferences.remove(dataKey)
            }

            true
        } catch (e: Exception) {
            false
        }
    }
}
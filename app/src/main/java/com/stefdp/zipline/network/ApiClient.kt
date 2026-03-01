package com.stefdp.zipline.network

import com.stefdp.zipline.DEBUG_NETWORK
import com.stefdp.zipline.network.models.PasswordState
import com.stefdp.zipline.network.models.PasswordStateDeserializer
import com.stefdp.zipline.network.models.passwordStateConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ZiplineApiClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    fun getZiplineApiService(baseUrl: String): ZiplineApiService {
        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        val fullUrl = formattedUrl + "api/"

        val retrofit = Retrofit.Builder()
            .baseUrl(fullUrl)

        if (DEBUG_NETWORK) {
            retrofit.client(okHttpClient)
        }

        return retrofit
            .addConverterFactory(GsonConverterFactory.create(passwordStateConverterFactory))
            .build()
            .create(ZiplineApiService::class.java)
    }
}
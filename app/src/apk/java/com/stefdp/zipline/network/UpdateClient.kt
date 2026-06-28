package com.stefdp.zipline.network

import com.google.gson.GsonBuilder
import com.stefdp.zipline.DEBUG_NETWORK
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object UpdateClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    fun getUpdateService(includeNull: Boolean = false): UpdateService {
        val url = "https://git.stefdp.com/"

        val retrofit = Retrofit.Builder()
            .baseUrl(url)

        if (DEBUG_NETWORK) {
            retrofit.client(okHttpClient)
        }

        retrofit.addConverterFactory(ScalarsConverterFactory.create())

        if (includeNull) {
            val gson = GsonBuilder()
                .serializeNulls()
                .create()

            retrofit.addConverterFactory(GsonConverterFactory.create(gson))
        } else {
            retrofit.addConverterFactory(GsonConverterFactory.create())
        }

        return retrofit
            .build()
            .create(UpdateService::class.java)
    }
}
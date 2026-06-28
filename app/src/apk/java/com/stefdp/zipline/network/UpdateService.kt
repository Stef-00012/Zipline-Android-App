package com.stefdp.zipline.network

import com.stefdp.zipline.network.models.ForgejoRelease
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UpdateService {
    @GET("api/v1/repos/{username}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("username") username: String,
        @Path("repo") repo: String
    ): Response<ForgejoRelease>
}
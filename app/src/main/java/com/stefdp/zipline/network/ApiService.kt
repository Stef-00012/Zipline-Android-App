package com.stefdp.zipline.network

import com.stefdp.zipline.network.models.BaseFolder
import com.stefdp.zipline.network.models.Export
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.IncompleteFile
import com.stefdp.zipline.network.models.Invite
import com.stefdp.zipline.network.models.Metric
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.PublicFolder
import com.stefdp.zipline.network.models.PublicServerConfig
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.Tag
import com.stefdp.zipline.network.models.Url
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.WebSettings
import com.stefdp.zipline.network.models.requests.BulkDeleteFilesBody
import com.stefdp.zipline.network.models.requests.BulkUpdateFilesBody
import com.stefdp.zipline.network.models.requests.CreateFolderBody
import com.stefdp.zipline.network.models.requests.CreateTagBody
import com.stefdp.zipline.network.models.requests.CreateInviteBody
import com.stefdp.zipline.network.models.requests.CreateUrlBody
import com.stefdp.zipline.network.models.requests.CreateUserBody
import com.stefdp.zipline.network.models.requests.AddFileToFolderBody
import com.stefdp.zipline.network.models.requests.RemoveFileFromFolderBody
import com.stefdp.zipline.network.models.requests.DeleteFolderBody
import com.stefdp.zipline.network.models.requests.DeleteSessionBody
import com.stefdp.zipline.network.models.requests.DeleteUserBody
import com.stefdp.zipline.network.models.requests.GetFilesQueryFilter
import com.stefdp.zipline.network.models.requests.GetFilesQueryOrder
import com.stefdp.zipline.network.models.requests.GetFilesQuerySearchField
import com.stefdp.zipline.network.models.requests.GetFilesQuerySortBy
import com.stefdp.zipline.network.models.requests.GetUrlsQuerySearchField
import com.stefdp.zipline.network.models.requests.LoginBody
import com.stefdp.zipline.network.models.requests.RunRequerySizeJobBody
import com.stefdp.zipline.network.models.requests.RunThumbnailGenerationJobBody
import com.stefdp.zipline.network.models.requests.UpdateCurrenUserBody
import com.stefdp.zipline.network.models.requests.UpdateFileBody
import com.stefdp.zipline.network.models.requests.UpdateTagBody
import com.stefdp.zipline.network.models.requests.UpdateUrlBody
import com.stefdp.zipline.network.models.requests.UpdateUserBody
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.network.models.requests.VerifyFilePasswordBody
import com.stefdp.zipline.network.models.requests.VerifyUrlPasswordBody
import com.stefdp.zipline.network.models.responses.BulkDeleteFilesResponse
import com.stefdp.zipline.network.models.responses.BulkUpdateFilesResponse
import com.stefdp.zipline.network.models.responses.CreateUrlResponse
import com.stefdp.zipline.network.models.responses.DeleteExportResponse
import com.stefdp.zipline.network.models.responses.DeleteIncompleteFilesResponse
import com.stefdp.zipline.network.models.responses.DeleteTagResponse
import com.stefdp.zipline.network.models.responses.GetFilesResponse
import com.stefdp.zipline.network.models.responses.GetCurrentUserResponse
import com.stefdp.zipline.network.models.responses.GetServerDataCountsResponse
import com.stefdp.zipline.network.models.responses.GetSessionsResponse
import com.stefdp.zipline.network.models.responses.GetStatsResponse
import com.stefdp.zipline.network.models.responses.GetTokenResponse
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.network.models.responses.HealthCheckResponse
import com.stefdp.zipline.network.models.responses.LoginResponse
import com.stefdp.zipline.network.models.responses.RunJobResponse
import com.stefdp.zipline.network.models.responses.UploadFileResponse
import com.stefdp.zipline.network.models.responses.UploadPartialFileResponse
import com.stefdp.zipline.network.models.responses.VerifyFilePasswordResponse
import com.stefdp.zipline.network.models.responses.VerifyUrlPasswordResponse
import com.stefdp.zipline.network.models.responses.ZeroByteFilesResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ZiplineApiService {
    @GET("version")
    suspend fun getServerVersion(
        @Header("Authorization") token: String,
    ): Response<GetServerVersionResponse>

    @GET("stats")
    suspend fun getServerStats(
        @Header("Authorization") token: String,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("all") all: Boolean? = null,
    ): Response<List<Metric>>

    @GET("healthcheck")
    suspend fun healthCheck(): Response<HealthCheckResponse>

    @POST("auth/login")
    suspend fun login(
        @Body data: LoginBody,
    ): Response<LoginResponse>

    @POST("auth/invites")
    suspend fun createInvite(
        @Header("Authorization") token: String,
        @Body data: CreateInviteBody,
    ): Response<Invite>

    @GET("auth/invites")
    suspend fun getInvites(
        @Header("Authorization") token: String,
    ): Response<List<Invite>>

    @POST("server/thumbnails")
    suspend fun runThumbnailGenerationJob(
        @Header("Authorization") token: String,
        @Body data: RunThumbnailGenerationJobBody
    ): Response<RunJobResponse>

    @POST("server/requery_size")
    suspend fun runRequerySizeJob(
        @Header("Authorization") token: String,
        @Body data: RunRequerySizeJobBody
    ): Response<RunJobResponse>

    @GET("server/public")
    suspend fun getPublicConfig(): Response<PublicServerConfig>

    @GET("server/folder/{folderId}")
    suspend fun getPublicFolderData(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
        @Query("uploads") filterAllowedUploadsOnly: Boolean? = null,
    ): Response<PublicFolder>

    @Streaming
    @GET("server/export")
    suspend fun exportData(
        @Header("Authorization") token: String,
        @Query("nometrics") noMetrics: Boolean? = null,
    ): Response<ResponseBody>

    @GET("server/export")
    suspend fun getServerDataCounts(
        @Header("Authorization") token: String,
        @Query("counts") noMetrics: Boolean = true,
    ): Response<GetServerDataCountsResponse>

    @GET("server/clear_zeros")
    suspend fun scanForZeroByteFiles(
        @Header("Authorization") token: String,
    ): Response<ZeroByteFilesResponse>

    @DELETE("server/clear_zeros")
    suspend fun deleteZeroByteFiles(
        @Header("Authorization") token: String,
    ) : Response<ZeroByteFilesResponse>

    @DELETE("server/clear_temp")
    suspend fun runDeleteTemporaryFilesJob(
        @Header("Authorization") token: String,
    ): Response<RunJobResponse>

    @GET("server/settings")
    suspend fun getServerSettings(
        @Header("Authorization") token: String,
    ): Response<ServerSettings>

    @PATCH("server/settings")
    suspend fun updateServerSettings(
        @Header("Authorization") token: String,
        @Body data: PartialServerSettingsSettings,
    ): Response<ServerSettings>

    @GET("server/settings/web")
    suspend fun getWebServerSettings(
        @Header("Authorization") token: String,
    ): Response<WebSettings>

    @Multipart
    @POST("upload")
    suspend fun uploadFile(
        @Header("Authorization") token: String,
        @Header("x-zipline-deletes-at") deletesAt: String? = null,
        @Header("x-zipline-format") format: FilesFormat? = null,
        @Header("x-zipline-image-compression-percent") imageCompressionPercent: Int? = null,
        @Header("x-zipline-image-compression-type") imageCompressionType: UploadCompressionType? = null,
        @Header("x-zipline-password") password: String? = null,
        @Header("x-zipline-max-views") maxViews: Long? = null,
        @Header("x-zipline-original-name") originalName: String? = null,
        @Header("x-zipline-folder") folder: String? = null,
        @Header("x-zipline-filename") filename: String? = null,
        @Header("x-zipline-domain") domain: String? = null,
        @Header("x-zipline-file-extension") fileExtension: String? = null,
        @Part file: MultipartBody.Part
    ): Response<UploadFileResponse>

    // example for later
//    suspend fun uploadFileRetrofit(filePath: String, uploadUrl: String) {
//        val file = File(filePath)
//        val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
//        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
//
//        val service = ZiplineApiClient.getZiplineApiService("url")
//        val response = service.uploadFile("", file = multipartBody)
//
//        if (response.isSuccessful) {
//            println(response.body()?.files)
//        } else {
//            println("Upload failed: ${response.code()} - ${response.errorBody()?.string()}")
//        }
//    }

//    @Multipart
//    @POST("upload")
//    suspend fun uploadFileNoJson(
//        @Header("Authorization") token: String,
//        @Header("x-zipline-deletes-at") deletesAt: String? = null,
//        @Header("x-zipline-format") format: FilesFormat? = null,
//        @Header("x-zipline-image-compression-percent") imageCompressionPercent: Int? = null,
//        @Header("x-zipline-image-compression-type") imageCompressionType: UploadCompressionType? = null,
//        @Header("x-zipline-password") password: String? = null,
//        @Header("x-zipline-max-views") maxViews: Long? = null,
//        @Header("x-zipline-original-name") originalName: String? = null,
//        @Header("x-zipline-folder") folder: String? = null,
//        @Header("x-zipline-filename") filename: String? = null,
//        @Header("x-zipline-domain") domain: String? = null,
//        @Header("x-zipline-file-extension") fileExtension: String? = null,
//        @Header("x-zipline-no-json") noJson: Boolean = true,
//        @Part file: MultipartBody.Part
//    ): Response<String>

    @Multipart
    @POST("upload/partial")
    suspend fun uploadPartialFile(
        @Header("Authorization") token: String,
        @Header("x-zipline-p-filename") partialFilename: String,
        @Header("x-zipline-p-content-type") partialContentType: String,
        @Header("x-zipline-p-lastchunk") isLastChunk: Boolean,
        @Header("x-zipline-p-content-length") partialContentLength: Long,
        @Header("Content-Range") contentRange: String,
        @Header("x-zipline-p-identifier") identifier: String? = null, // not required on first request, required on next ones
        @Header("x-zipline-deletes-at") deletesAt: String? = null,
        @Header("x-zipline-format") format: FilesFormat? = null,
        @Header("x-zipline-image-compression-percent") imageCompressionPercent: Int? = null,
        @Header("x-zipline-image-compression-type") imageCompressionType: UploadCompressionType? = null,
        @Header("x-zipline-password") password: String? = null,
        @Header("x-zipline-max-views") maxViews: Long? = null,
        @Header("x-zipline-original-name") originalName: String? = null,
        @Header("x-zipline-folder") folder: String? = null,
        @Header("x-zipline-filename") filename: String? = null,
        @Header("x-zipline-domain") domain: String? = null,
        @Header("x-zipline-file-extension") fileExtension: String? = null,
        @Part file: MultipartBody.Part
    ): Response<UploadPartialFileResponse>

//    @Multipart
//    @POST("upload/partial")
//    suspend fun uploadPartialFileNoJson(
//        @Header("Authorization") token: String,
//        @Header("x-zipline-p-filename") partialFilename: String,
//        @Header("x-zipline-p-content-type") partialContentType: String,
//        @Header("x-zipline-p-lastchunk") isLastChunk: Boolean,
//        @Header("x-zipline-p-content-length") partialContentLength: Long,
//        @Header("x-zipline-p-identifier") identifier: String? = null, // not required on first request, required on next ones
//        @Header("x-zipline-deletes-at") deletesAt: String? = null,
//        @Header("x-zipline-format") format: FilesFormat? = null,
//        @Header("x-zipline-image-compression-percent") imageCompressionPercent: Int? = null,
//        @Header("x-zipline-image-compression-type") imageCompressionType: UploadCompressionType? = null,
//        @Header("x-zipline-password") password: String? = null,
//        @Header("x-zipline-max-views") maxViews: Long? = null,
//        @Header("x-zipline-original-name") originalName: String? = null,
//        @Header("x-zipline-folder") folder: String? = null,
//        @Header("x-zipline-filename") filename: String? = null,
//        @Header("x-zipline-domain") domain: String? = null,
//        @Header("x-zipline-file-extension") fileExtension: String? = null,
//        @Header("x-zipline-no-json") noJson: Boolean = true,
//        @Part file: MultipartBody.Part
//    ): Response<String>

    @GET("user")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String,
    ): Response<GetCurrentUserResponse>

    @PATCH("user")
    suspend fun updateCurrentUser(
        @Header("Authorization") token: String,
        @Body data: UpdateCurrenUserBody,
    ): Response<GetCurrentUserResponse>

    @GET("user/token")
    suspend fun getToken(
        @Header("Cookie") cookie: String,
    ): Response<GetTokenResponse>

    @PATCH("user/token")
    suspend fun refreshToken(
        @Header("Authorization") token: String,
    ): Response<LoginResponse>

    @GET("user/stats")
    suspend fun getStats(
        @Header("Authorization") token: String,
    ): Response<GetStatsResponse>

    @GET("user/sessions")
    suspend fun getSessions(
        @Header("Authorization") token: String,
    ): Response<GetSessionsResponse>

    @DELETE("user/sessions")
    suspend fun deleteSession(
        @Header("Authorization") token: String,
        @Body data: DeleteSessionBody
    ): Response<GetSessionsResponse>

    @GET("user/recent")
    suspend fun getRecentFiles(
        @Header("Authorization") token: String,
        @Query("take") count: Int? = null,
    ): Response<List<File>>

    @GET("user/export")
    suspend fun getExports(
        @Header("Authorization") token: String,
    ): Response<List<Export>>

    @Streaming
    @GET("user/export")
    suspend fun downloadExport(
        @Header("Authorization") token: String,
        @Query("id") exportId: String,
    ): Response<ResponseBody>

    @DELETE("user/export")
    suspend fun deleteExport(
        @Header("Authorization") token: String,
        @Query("id") exportId: String,
    ): Response<DeleteExportResponse>

    @POST("user/export")
    suspend fun startExport(
        @Header("Authorization") token: String,
    ): Response<Export>

    @GET("user/avatar")
    suspend fun getAvatar(
        @Header("Authorization") token: String,
    ): Response<String>

    @GET("user/urls")
    suspend fun getUrls(
        @Header("Authorization") token: String,
        @Query("searchField") searchField: GetUrlsQuerySearchField? = null,
        @Query("searchQuery") searchQuery: String? = null,
    ): Response<List<Url>>

    @POST("user/urls")
    suspend fun createUrl(
        @Header("Authorization") token: String,
        @Header("x-zipline-max-views") maxViews: Long? = null,
        @Header("x-zipline-domain") domain: String? = null,
        @Header("x-zipline-password") password: String? = null,
        @Body data: CreateUrlBody,
    ): Response<CreateUrlResponse>

//    @POST("user/urls")
//    suspend fun createUrlNoJson(
//        @Header("Authorization") token: String,
//        @Header("x-zipline-max-views") maxViews: Long? = null,
//        @Header("x-zipline-domain") domain: String? = null,
//        @Header("x-zipline-password") password: String? = null,
//        @Header("x-zipline-no-json") noJson: Boolean = true,
//        @Body data: CreateUrlBody,
//    ): Response<String>

    @GET("user/urls/{urlId}")
    suspend fun getUrl(
        @Header("Authorization") token: String,
        @Path("urlId") urlId: String,
    ): Response<Url>

    @PATCH("user/urls/{urlId}")
    suspend fun updateUrl(
        @Header("Authorization") token: String,
        @Path("urlId") urlId: String,
        @Body data: UpdateUrlBody
    ): Response<Url>

    @DELETE("user/urls/{urlId}")
    suspend fun deleteUrl(
        @Header("Authorization") token: String,
        @Path("urlId") urlId: String,
    ): Response<Url>

    @POST("user/urls/{urlId}/password")
    suspend fun verifyUrlPassword(
        @Header("Authorization") token: String,
        @Path("urlId") urlId: String,
        @Body data: VerifyUrlPasswordBody
    ): Response<VerifyUrlPasswordResponse>

    @GET("user/tags")
    suspend fun getTags(
        @Header("Authorization") token: String,
    ): Response<List<Tag>>

    @POST("user/tags")
    suspend fun createTag(
        @Header("Authorization") token: String,
        @Body data: CreateTagBody
    ): Response<Tag>

    @GET("user/tags/{tagId}")
    suspend fun getTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: String,
    ): Response<Tag>

    @DELETE("user/tags/{tagId}")
    suspend fun deleteTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: String,
    ): Response<DeleteTagResponse>

    @PATCH("user/tags/{tagId}")
    suspend fun updateTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: String,
        @Body data: UpdateTagBody
    ): Response<Tag>

    @GET("user/folders")
    suspend fun getFolders(
        @Header("Authorization") token: String,
    ): Response<List<BaseFolder>>

    @POST("user/folders")
    suspend fun createFolder(
        @Header("Authorization") token: String,
        @Body data: CreateFolderBody
    ): Response<BaseFolder>

    @GET("user/folders/{folderId}")
    suspend fun getFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
    ) : Response<BaseFolder>

    @PUT("user/folders/{folderId}")
    suspend fun addFileToFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
        @Body data: AddFileToFolderBody,
    ): Response<BaseFolder>

    @HTTP(
        method = "DELETE",
        path = "user/folders/{folderId}",
        hasBody = true
    )
    suspend fun deleteFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
        @Body data: DeleteFolderBody,
    ): Response<BaseFolder>

    @HTTP(
        method = "DELETE",
        path = "user/folders/{folderId}",
        hasBody = true
    )
    suspend fun removeFileFromFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
        @Body data: RemoveFileFromFolderBody,
    ): Response<BaseFolder>

    @Streaming
    @GET("user/folders/{folderId}/export")
    suspend fun exportFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
    ): Response<ResponseBody>

    @GET("user/files")
    suspend fun getFiles(
        @Header("Authorization") token: String,
        @Query("page") page: Long = 1,
        @Query("perpage") perPage: Long? = null,
        @Query("filter") filter: GetFilesQueryFilter? = null,
        @Query("favorite") filterFavorite: Boolean? = null,
        @Query("sortBy") sortBy: GetFilesQuerySortBy? = null,
        @Query("order") sortOrder: GetFilesQueryOrder? = null,
        @Query("searchField") searchField: GetFilesQuerySearchField? = null,
        @Query("searchQuery") searchQuery: String? = null,
        @Query("id") userId: String? = null,
        @Query("folder") folderId: String? = null,
    ): Response<GetFilesResponse>

    @PATCH("user/files/transaction")
    suspend fun bulkUpdateFiles(
        @Header("Authorization") token: String,
        @Body data: BulkUpdateFilesBody,
    ): Response<BulkUpdateFilesResponse>

    @DELETE("user/files/transaction")
    suspend fun bulkDeleteFiles(
        @Header("Authorization") token: String,
        @Body data: BulkDeleteFilesBody,
    ): Response<BulkDeleteFilesResponse>

    @GET("user/files/incomplete")
    suspend fun getIncompleteFiles(
        @Header("Authorization") token: String,
    ): Response<List<IncompleteFile>>

    @DELETE("user/files/incomplete")
    suspend fun deleteIncompleteFiles(
        @Header("Authorization") token: String,
    ): Response<DeleteIncompleteFilesResponse>

    @GET("user/files/{fileId}")
    suspend fun getFile(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
    ): Response<File>

    @PATCH("user/files/{fileId}")
    suspend fun updateFile(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
        @Body data: UpdateFileBody,
    ): Response<File>

    @DELETE("user/files/{fileId}")
    suspend fun deleteFile(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
    ): Response<File>

    @POST("user/files/{fileId}/password")
    suspend fun verifyFilePassword(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
        @Body data: VerifyFilePasswordBody
    ): Response<VerifyFilePasswordResponse>

    @Streaming
    @GET("user/files/{fileId}/raw")
    suspend fun downloadFile(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
        @Query("pw") password: String? = null,
        @Query("download") download: Boolean = true,
    ): Response<ResponseBody>

    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") token: String,
        @Query("noIncl") exclude: Boolean? = null,
    ): Response<List<User>>

    @POST("users")
    suspend fun createUser(
        @Header("Authorization") token: String,
        @Body data: CreateUserBody,
    ): Response<User>

    @GET("users/{userId}")
    suspend fun getUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
    ): Response<User>

    @PATCH("users/{userId}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body data: UpdateUserBody,
    ): Response<User>

    @DELETE("users/{userId}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Body data: DeleteUserBody
    ): Response<User>

    @GET("users/{userId}/tags")
    suspend fun getUserTags(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
    ): Response<List<Tag>>
}
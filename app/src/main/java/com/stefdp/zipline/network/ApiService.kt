package com.stefdp.zipline.network

import com.stefdp.zipline.network.models.Export
import com.stefdp.zipline.network.models.File
import com.stefdp.zipline.network.models.FilesFormat
import com.stefdp.zipline.network.models.Folder
import com.stefdp.zipline.network.models.IncompleteFile
import com.stefdp.zipline.network.models.Invite
import com.stefdp.zipline.network.models.Metric
import com.stefdp.zipline.network.models.PartialServerSettingsSettings
import com.stefdp.zipline.network.models.PublicServerConfig
import com.stefdp.zipline.network.models.ServerSettings
import com.stefdp.zipline.network.models.Tag
import com.stefdp.zipline.network.models.Url
import com.stefdp.zipline.network.models.User
import com.stefdp.zipline.network.models.WebSettings
import com.stefdp.zipline.network.models.requests.BulkDeleteCurrentUserFilesBody
import com.stefdp.zipline.network.models.requests.BulkUpdateCurrentUserFilesBody
import com.stefdp.zipline.network.models.requests.CreateCurrentUserFolderBody
import com.stefdp.zipline.network.models.requests.CreateCurrentUserTagBody
import com.stefdp.zipline.network.models.requests.CreateInviteBody
import com.stefdp.zipline.network.models.requests.CreateUrlBody
import com.stefdp.zipline.network.models.requests.CreateUserBody
import com.stefdp.zipline.network.models.requests.CurrentUserAddFileToFolderBody
import com.stefdp.zipline.network.models.requests.CurrentUserRemoveFileFromFolderBody
import com.stefdp.zipline.network.models.requests.DeleteCurrentUserFolderBody
import com.stefdp.zipline.network.models.requests.DeleteCurrentUserSessionBody
import com.stefdp.zipline.network.models.requests.DeleteUserBody
import com.stefdp.zipline.network.models.requests.GetCurrentUserFilesQueryFilter
import com.stefdp.zipline.network.models.requests.GetCurrentUserFilesQueryOrder
import com.stefdp.zipline.network.models.requests.GetCurrentUserFilesQuerySearchField
import com.stefdp.zipline.network.models.requests.GetCurrentUserFilesQuerySortBy
import com.stefdp.zipline.network.models.requests.GetCurrentUserUrlsQuerySearchField
import com.stefdp.zipline.network.models.requests.LoginBody
import com.stefdp.zipline.network.models.requests.RunRequerySizeJobBody
import com.stefdp.zipline.network.models.requests.RunThumbnailGenerationJobBody
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserBody
import com.stefdp.zipline.network.models.requests.UpdateCurrentUserFileBody
import com.stefdp.zipline.network.models.requests.UpdateUrlBody
import com.stefdp.zipline.network.models.requests.UpdateUserBody
import com.stefdp.zipline.network.models.requests.UploadCompressionType
import com.stefdp.zipline.network.models.requests.VerifyCurrentUserFilePasswordBody
import com.stefdp.zipline.network.models.requests.VerifyCurrentUserUrlPasswordBody
import com.stefdp.zipline.network.models.responses.BulkDeleteCurrentUserFilesResponse
import com.stefdp.zipline.network.models.responses.BulkUpdateCurrentUserFilesResponse
import com.stefdp.zipline.network.models.responses.CreateUrlResponse
import com.stefdp.zipline.network.models.responses.DeleteCurrentUserExportResponse
import com.stefdp.zipline.network.models.responses.DeleteCurrentUserIncompleteFilesResponse
import com.stefdp.zipline.network.models.responses.DeleteCurrentUserTagResponse
import com.stefdp.zipline.network.models.responses.GetCurrentUserFilesResponse
import com.stefdp.zipline.network.models.responses.GetCurrentUserResponse
import com.stefdp.zipline.network.models.responses.GetCurrentUserSessions
import com.stefdp.zipline.network.models.responses.GetCurrentUserStatsResponse
import com.stefdp.zipline.network.models.responses.GetCurrentUserTokenResponse
import com.stefdp.zipline.network.models.responses.GetServerVersionResponse
import com.stefdp.zipline.network.models.responses.HealthCheckResponse
import com.stefdp.zipline.network.models.responses.LoginResponse
import com.stefdp.zipline.network.models.responses.RunJobResponse
import com.stefdp.zipline.network.models.responses.UploadFileResponse
import com.stefdp.zipline.network.models.responses.UploadPartialFileResponse
import com.stefdp.zipline.network.models.responses.VerifyCurrentUserFilePasswordResponse
import com.stefdp.zipline.network.models.responses.VerifyCurrentUserUrlPasswordResponse
import com.stefdp.zipline.network.models.responses.ZeroByteFilesResponse
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
//import java.io.File

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
        @Header("x-zipline-client") client: String = "Zipline Android App",
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
        @Query("uploads") uploads: Boolean? = null,
    ): Response<Folder>

    @Streaming
    @GET("server/export")
    suspend fun exportData(
        @Header("Authorization") token: String,
    ): Response<ResponseBody>

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

    @Multipart
    @POST("upload")
    suspend fun uploadFileNoJson(
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
        @Header("x-zipline-no-json") noJson: Boolean = true,
        @Part file: MultipartBody.Part
    ): Response<String>

    @Multipart
    @POST("upload/partial")
    suspend fun uploadPartialFile(
        @Header("Authorization") token: String,
        @Header("x-zipline-p-filename") partialFilename: String,
        @Header("x-zipline-p-content-type") partialContentType: String,
        @Header("x-zipline-p-lastchunk") isLastChunk: Boolean,
        @Header("x-zipline-p-content-length") partialContentLength: Long,
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

    @Multipart
    @POST("upload/partial")
    suspend fun uploadPartialFileNoJson(
        @Header("Authorization") token: String,
        @Header("x-zipline-p-filename") partialFilename: String,
        @Header("x-zipline-p-content-type") partialContentType: String,
        @Header("x-zipline-p-lastchunk") isLastChunk: Boolean,
        @Header("x-zipline-p-content-length") partialContentLength: Long,
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
        @Header("x-zipline-no-json") noJson: Boolean = true,
        @Part file: MultipartBody.Part
    ): Response<String>

    @GET("user")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String,
    ): Response<GetCurrentUserResponse>

    @PATCH("user")
    suspend fun updateCurrentUser(
        @Header("Authorization") token: String,
        @Body data: UpdateCurrentUserBody,
    ): Response<GetCurrentUserResponse>

    @GET("user/token")
    suspend fun getCurrentUserToken(
        @Header("Cookie") cookie: String,
    ): Response<GetCurrentUserTokenResponse>

    @PATCH("user/token")
    suspend fun refreshCurrentUserToken(
        @Header("Authorization") token: String,
    ): Response<LoginResponse>

    @GET("user/stats")
    suspend fun getCurrentUserStats(
        @Header("Authorization") token: String,
    ): Response<GetCurrentUserStatsResponse>

    @GET("user/sessions")
    suspend fun getCurrentUserSessions(
        @Header("Authorization") token: String,
    ): Response<GetCurrentUserSessions>

    @DELETE("user/sessions")
    suspend fun deleteCurrentUserSession(
        @Header("Authorization") token: String,
        @Body data: DeleteCurrentUserSessionBody
    ): Response<GetCurrentUserSessions>

    @GET("user/recent")
    suspend fun getCurrentUserRecentFiles(
        @Header("Authorization") token: String,
        @Query("take") limit: Int? = null,
    ): Response<List<File>>

    @GET("user/export")
    suspend fun getCurrentUserExports(
        @Header("Authorization") token: String,
    ): Response<List<Export>>

    @GET("user/export")
    suspend fun downloadCurrentUserExport(
        @Header("Authorization") token: String,
        @Query("id") exportId: String,
    ): Response<ResponseBody>

    @DELETE("user/export")
    suspend fun deleteCurrentUserExport(
        @Header("Authorization") token: String,
        @Query("id") exportId: String,
    ): Response<DeleteCurrentUserExportResponse>

    @POST("user/export")
    suspend fun startCurrentUserExport(
        @Header("Authorization") token: String,
    ): Response<Export>

    @GET("user/avatar")
    suspend fun getCurrentUserAvatar(
        @Header("Authorization") token: String,
    ): Response<String>

    @GET("user/urls")
    suspend fun getCurrentUserUrls(
        @Header("Authorization") token: String,
        @Query("searchField") searchField: GetCurrentUserUrlsQuerySearchField? = null,
        @Query("searchQuery") searchQuery: String? = null,
    ): Response<List<Url>>

    @POST("user/urls")
    suspend fun createCurrentUserUrl(
        @Header("Authorization") token: String,
        @Header("x-zipline-max-views") maxViews: Long? = null,
        @Header("x-zipline-domain") domain: String? = null,
        @Header("x-zipline-password") password: String? = null,
        @Body data: CreateUrlBody,
    ): Response<CreateUrlResponse>

    @POST("user/urls")
    suspend fun createCurrentUserUrlNoJson(
        @Header("Authorization") token: String,
        @Header("x-zipline-max-views") maxViews: Long? = null,
        @Header("x-zipline-domain") domain: String? = null,
        @Header("x-zipline-password") password: String? = null,
        @Header("x-zipline-no-json") noJson: Boolean = true,
        @Body data: CreateUrlBody,
    ): Response<String>

    @GET("user/urls/{urlId}")
    suspend fun getCurrentUserUrl(
        @Header("Authorization") token: String,
        @Path("urlId") urlId: String,
    ): Response<Url>

    @PATCH("user/urls/{urlId}")
    suspend fun updateCurrentUserUrl(
        @Header("Authorization") token: String,
        @Path("urlId") urlId: String,
        @Body data: UpdateUrlBody
    ): Response<Url>

    @DELETE("user/urls/{urlId}")
    suspend fun deleteCurrentUserUrl(
        @Header("Authorization") token: String,
        @Path("urlId") urlId: String,
    ): Response<Url>

    @POST("user/urls/{urlId}/password")
    suspend fun verifyCurrentUserUrlPassword(
        @Header("Authorization") token: String,
        @Path("urlId") urlId: String,
        @Body data: VerifyCurrentUserUrlPasswordBody
    ): Response<VerifyCurrentUserUrlPasswordResponse>

    @GET("user/tags")
    suspend fun getCurrentUserTags(
        @Header("Authorization") token: String,
    ): Response<List<Tag>>

    @POST("user/tags")
    suspend fun createCurrentUserTag(
        @Header("Authorization") token: String,
        @Body data: CreateCurrentUserTagBody
    ): Response<Tag>

    @GET("user/tags/{tagId}")
    suspend fun getCurrentUserTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: String,
    ): Response<Tag>

    @DELETE("user/tags/{tagId}")
    suspend fun deleteCurrentUserTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: String,
    ): Response<DeleteCurrentUserTagResponse>

    @PATCH("user/tags/{tagId}")
    suspend fun updateCurrentUserTag(
        @Header("Authorization") token: String,
        @Path("tagId") tagId: String,
        @Body data: CreateCurrentUserTagBody
    ): Response<Tag>

    @GET("user/folders")
    suspend fun getCurrentUserFolders(
        @Header("Authorization") token: String,
    ): Response<List<Folder>>

    @POST("user/folders")
    suspend fun createCurrentUserFolder(
        @Header("Authorization") token: String,
        @Body data: CreateCurrentUserFolderBody
    ): Response<Folder>

    @GET("user/folders/{folderId}")
    suspend fun getCurrentUserFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
    ) : Response<Folder>

    @PUT("user/folders/{folderId}")
    suspend fun currentUserAddFileToFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
        @Body data: CurrentUserAddFileToFolderBody,
    ): Response<Folder>

    @DELETE("user/folders/{folderId}/")
    suspend fun deleteCurrentUserFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
        @Body data: DeleteCurrentUserFolderBody,
    ): Response<Folder>

    @DELETE("user/folders/{folderId}")
    suspend fun currentUserRemoveFileFromFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
        @Body data: CurrentUserRemoveFileFromFolderBody,
    ): Response<Folder>

    @GET("user/folders/{folderId}/export")
    suspend fun exportCurrentUserFolder(
        @Header("Authorization") token: String,
        @Path("folderId") folderId: String,
    ): Response<ResponseBody>

    @GET("user/files")
    suspend fun getCurrentUserFiles(
        @Header("Authorization") token: String,
        @Query("page") page: Long = 1,
        @Query("perpage") perPage: Long? = null,
        @Query("filter") filter: GetCurrentUserFilesQueryFilter? = null,
        @Query("favorite") favorite: Boolean? = null,
        @Query("sortBy") sortBy: GetCurrentUserFilesQuerySortBy? = null,
        @Query("order") order: GetCurrentUserFilesQueryOrder? = null,
        @Query("searchField") searchField: GetCurrentUserFilesQuerySearchField? = null,
        @Query("searchQuery") searchQuery: String? = null,
        @Query("id") userId: String? = null,
        @Query("folder") folderId: String? = null,
    ): Response<GetCurrentUserFilesResponse>

    @PATCH("user/files/transaction")
    suspend fun bulkUpdateCurrentUserFiles(
        @Header("Authorization") token: String,
        @Body data: BulkUpdateCurrentUserFilesBody,
    ): Response<BulkUpdateCurrentUserFilesResponse>

    @DELETE("user/files/transaction")
    suspend fun bulkDeleteCurrentUserFiles(
        @Header("Authorization") token: String,
        @Body data: BulkDeleteCurrentUserFilesBody,
    ): Response<BulkDeleteCurrentUserFilesResponse>

    @GET("user/files/incomplete")
    suspend fun getCurrentUserIncompleteFiles(
        @Header("Authorization") token: String,
    ): Response<List<IncompleteFile>>

    @DELETE("user/files/incomplete")
    suspend fun deleteCurrentUserIncompleteFiles(
        @Header("Authorization") token: String,
    ): Response<DeleteCurrentUserIncompleteFilesResponse>

    @GET("user/files/{fileId}")
    suspend fun getCurrentUserFile(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
    ): Response<File>

    @PATCH("user/files/{fileId}")
    suspend fun updateCurrentUserFile(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
        @Body data: UpdateCurrentUserFileBody,
    ): Response<File>

    @DELETE("user/files/{fileId}")
    suspend fun deleteCurrentUserFile(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
    ): Response<File>

    @POST("user/files/{fileId}/password")
    suspend fun verifyCurrentUserFilePassword(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
        @Body data: VerifyCurrentUserFilePasswordBody
    ): Response<VerifyCurrentUserFilePasswordResponse>

    @GET("user/files/{fileId}/raw")
    suspend fun downloadCurrentUserFile(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: String,
        @Query("pw") password: String? = null,
        @Query("download") download: Boolean = true,
    ): Response<ResponseBody>

    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") token: String,
        @Query("noIncl") excludeCurrentUser: Boolean? = null,
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
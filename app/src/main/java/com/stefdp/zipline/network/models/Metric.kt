package com.stefdp.zipline.network.models

data class Metric(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val data: MetricData
)

data class MetricData(
    val users: Long,
    val files: Long,
    val fileViews: Long,
    val urls: Long,
    val urlViews: Long,
    val storage: Long,
    val filesUsers: List<MetricDataFilesUsers>,
    val urlsUsers: List<MetricDataUrlsUsers>,
    val types: List<MetricDataTypes>
)

data class MetricDataFilesUsers(
    val username: String?,
    val sum: Long,
    val storage: Long,
    val views: Long
)

data class MetricDataUrlsUsers(
    val username: String?,
    val sum: Long,
    val views: Long
)

data class MetricDataTypes(
    val type: String,
    val sum: Long,
)
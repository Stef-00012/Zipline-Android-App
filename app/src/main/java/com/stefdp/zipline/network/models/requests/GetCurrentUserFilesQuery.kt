package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName

enum class GetCurrentUserFilesQueryFilter(val value: String) {
    @SerializedName("dashboard")
    DASHBOARD("dashboard"),

    @SerializedName("none")
    NONE("none"),

    @SerializedName("all")
    ALL("all");

    override fun toString(): String = value
}

enum class GetCurrentUserFilesQuerySortBy(val value: String) {
    @SerializedName("id")
    ID("id"),

    @SerializedName("createdAt")
    CREATED_AT("createdAt"),

    @SerializedName("updatedAt")
    UPDATED_AT("updatedAt"),

    @SerializedName("deletesAt")
    DELETES_AT("deletesAt"),

    @SerializedName("name")
    NAME("name"),

    @SerializedName("originalName")
    ORIGINAL_NAME("originalName"),

    @SerializedName("size")
    SIZE("size"),

    @SerializedName("type")
    TYPE("type"),

    @SerializedName("views")
    VIEWS("views"),

    @SerializedName("favorite")
    FAVORITE("favorite");

    override fun toString(): String = value
}

enum class GetCurrentUserFilesQueryOrder(val value: String) {
    @SerializedName("asc")
    ASC("asc"),

    @SerializedName("desc")
    DESC("desc");

    override fun toString(): String = value
}

enum class GetCurrentUserFilesQuerySearchField(val value: String) {
    @SerializedName("name")
    NAME("name"),

    @SerializedName("originalName")
    ORIGINAL_NAME("originalName"),

    @SerializedName("type")
    TYPE("type"),

    @SerializedName("tags")
    TAGS("tags"),

    @SerializedName("id")
    ID("id");

    override fun toString(): String = value
}
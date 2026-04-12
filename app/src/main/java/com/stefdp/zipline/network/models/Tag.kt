package com.stefdp.zipline.network.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val name: String,
    val color: String,
    val files: List<TagFile>? = null,
) : Parcelable

@Parcelize
data class TagFile(
    val id: String,
) : Parcelable

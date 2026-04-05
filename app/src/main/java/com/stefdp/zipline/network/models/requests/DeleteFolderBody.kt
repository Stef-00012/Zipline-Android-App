package com.stefdp.zipline.network.models.requests

import com.google.gson.annotations.SerializedName
import com.stefdp.zipline.network.models.DeleteFolderType

data class DeleteFolderBody(
    val delete: String = DeleteFolderType.FOLDER.toString(),
    val childrenAction: DeleteFolderChildrenAction,
    val targetFolderId: String? = null
)

enum class DeleteFolderChildrenAction(
    val value: String,
    val displayName: String,
    val apiName: String
) {
    @SerializedName("root")
    MOVE_TO_ROOT(
        value = "MOVE_TO_ROOT",
        displayName = "Move contents to root folder",
        apiName = "root"
    ),

    @SerializedName("folder")
    MOVE_TO_FOLDER(
        value = "MOVE_TO_FOLDER",
        displayName = "Move contents to another folder",
        apiName = "folder"
    ),

    @SerializedName("cascade")
    CASCADE_DELETE(
        value = "CASCADE_DELETE",
        displayName = "Delete everything (cascade delete)",
        apiName = "cascade"
    );

    override fun toString(): String = value
}
package com.stefdp.zipline.network.models.requests

import com.stefdp.zipline.network.models.DeleteFolderType

data class DeleteFolderBody(
    val delete: String = DeleteFolderType.FOLDER.toString(),
)

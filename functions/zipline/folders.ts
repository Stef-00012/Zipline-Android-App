import * as db from "@/functions/database";
import type {
	APIFoldersNoIncl,
	APIFolders,
    APIFolder,
} from "@/types/zipline";
import axios from "axios";

// GET /api/user/folders
export async function getFolders<T extends boolean | undefined = undefined>(
	noIncl?: T,
): Promise<T extends true ? APIFoldersNoIncl | null : APIFolders | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	const params = new URLSearchParams();

	if (noIncl) params.append("noincl", "true");

	try {
		const res = await axios.get(`${url}/api/user/folders?${params}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

// POST /api/user/folders
export async function createFolder(name: string, isPublic = false): Promise<APIFolder | null> {
    const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.post(`${url}/api/user/folders`, {
            name,
            isPublic
        }, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

// PATCH /api/user/folders/[id]
export async function editFolder(id: string, isPublic: boolean): Promise<APIFolder | null> {
    const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.patch(`${url}/api/user/folders/${id}`, {
            isPublic
        }, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

// DELETE /api/user/folders/[id]
export async function deleteFolder(id: string): Promise<APIFolder | null> {
    const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.delete(`${url}/api/user/folders/${id}`, {
			headers: {
				Authorization: token,
			},
            data: {
                delete: "folder"
            }
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

// DELETE /api/user/folders/[folderId]
export async function removeFileFromFolder(folderId: string, fileId: string): Promise<APIFolder | null> {
    const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.delete(`${url}/api/user/folders/${folderId}`, {
			headers: {
				Authorization: token,
			},
            data: {
                delete: "file",
                id: fileId
            }
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

// POST /api/user/folders/[folderId]
export async function addFileToFolder(folderId: string, fileId: string): Promise<APIFolder | null> {
    const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.post(`${url}/api/user/folders/${folderId}`, {
            id: fileId
        }, {
			headers: {
				Authorization: token,
			}
		});

		return res.data;
	} catch (e) {
		return null;
	}
}
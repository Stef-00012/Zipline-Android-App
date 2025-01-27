import type { APIFoldersNoIncl, APIFolders, APIFolder } from "@/types/zipline";
import * as db from "@/functions/database";
import axios, { type AxiosError } from "axios";

// GET /api/user/folders
export async function getFolders<T extends boolean | undefined = undefined>(
	noIncl?: T,
): Promise<T extends true ? APIFoldersNoIncl | null : APIFolders | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

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
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

// GET /api/user/folders/[id]
export async function getFolder(id: string): Promise<APIFolder | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user/folders/${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

// POST /api/user/folders
export async function createFolder(
	name: string,
	isPublic = false,
): Promise<APIFolder | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.post(
			`${url}/api/user/folders`,
			{
				name,
				isPublic,
			},
			{
				headers: {
					Authorization: token,
				},
			},
		);

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

// PATCH /api/user/folders/[id]
export async function editFolder(
	id: string,
	isPublic: boolean,
): Promise<APIFolder | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.patch(
			`${url}/api/user/folders/${id}`,
			{
				isPublic,
			},
			{
				headers: {
					Authorization: token,
				},
			},
		);

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

// DELETE /api/user/folders/[id]
export async function deleteFolder(id: string): Promise<APIFolder | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.delete(`${url}/api/user/folders/${id}`, {
			headers: {
				Authorization: token,
			},
			data: {
				delete: "folder",
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

// DELETE /api/user/folders/[folderId]
export async function removeFileFromFolder(
	folderId: string,
	fileId: string,
): Promise<APIFolder | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.delete(`${url}/api/user/folders/${folderId}`, {
			headers: {
				Authorization: token,
			},
			data: {
				delete: "file",
				id: fileId,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

// POST /api/user/folders/[folderId]
export async function addFileToFolder(
	folderId: string,
	fileId: string,
): Promise<APIFolder | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.post(
			`${url}/api/user/folders/${folderId}`,
			{
				id: fileId,
			},
			{
				headers: {
					Authorization: token,
				},
			},
		);

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

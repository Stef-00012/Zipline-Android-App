import type {
	APIFile,
	APIFiles,
	APISettings,
	APITag,
	APIUploadFile,
} from "@/types/zipline";
import * as FileSystem from "expo-file-system";
import * as db from "@/functions/database";
import axios, { type AxiosError } from "axios";

export interface GetFilesOptions {
	id?: string;
	favorite?: boolean;
}
// GET /api/user/files
export async function getFiles(
	page: string,
	options: GetFilesOptions = {},
): Promise<APIFiles | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	const params = new URLSearchParams({
		page: page,
	});

	if (options.id) params.append("id", options.id);
	if (options.favorite) params.append("favorite", "true");

	try {
		const res = await axios.get(`${url}/api/user/files?${params}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
	}
}

// GET /api/user/files/[id]
export async function getFile(id: string): Promise<APIFile | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user/files/${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
	}
}

// DELETE /api/user/files/[id]
export async function deleteFile(id: string): Promise<APIFile | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.delete(`${url}/api/user/files/${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
	}
}

interface UpdateFileTagsOptions {
	add?: Array<string>;
	remove?: Array<string>;
}
// PATCH /api/user/files/[id]
export async function updateFileTags(
	id: string,
	options: UpdateFileTagsOptions = {},
): Promise<APIFile | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const file = await getFile(id);

		if (typeof file === "string") return file;

		let newTags = (file?.tags || []).map((tag) => tag.id);

		if (options.remove)
			newTags = newTags.filter((tag) => !options.remove?.includes(tag));
		if (options.add) newTags.push(...options.add);

		const res = await axios.patch(
			`${url}/api/user/files/${id}`,
			{
				tags: newTags,
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

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
	}
}

export interface EditFileOptions {
	originalName?: string;
	maxViews?: number | null;
	password?: string | null;
	type?: string;
	favorite?: boolean;
	tags?: Array<APITag["id"]>;
}
// PATCH /api/user/files/[id]
export async function editFile(
	id: string,
	options: EditFileOptions = {},
): Promise<APIFile | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.patch(`${url}/api/user/files/${id}`, options, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
	}
}

export interface UploadFileOptions {
	text?: boolean;
	maxViews?: number;
	compression?: number;
	format?: APISettings["filesDefaultFormat"];
	password?: string;
	filename?: string;
	folder?: string;
	overrideDomain?: string;
	originalName?: boolean;
	expiresAt?: Date;
}
// POST /api/upload
export async function uploadFiles(
	file: {
		uri: string;
		mimetype: string;
	},
	options: UploadFileOptions = {},
): Promise<Array<APIUploadFile> | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	const headers: {
		[key: string]: string;
	} = {};

	headers.Authorization = token;
	headers["X-Zipline-Format"] = options.format?.toLowerCase() || "random";

	if (options.compression)
		headers["X-Zipline-Image-Compression-Percent"] = String(
			options.compression,
		);
	if (options.maxViews)
		headers["X-Zipline-Max-Views"] = String(options.maxViews);
	if (options.password) headers["X-Zipline-Password"] = options.password;
	if (options.folder) headers["X-Zipline-Folder"] = options.folder;
	if (options.overrideDomain)
		headers["X-Zipline-Domain"] = options.overrideDomain;
	if (options.expiresAt)
		headers["X-Zipline-Deletes-At"] = `date=${options.expiresAt.toISOString()}`;
	if (options.originalName) headers["X-Zipline-Original-Name"] = "true";
	if (options.filename) headers["X-Zipline-Filename"] = options.filename;

	try {
		const res = await FileSystem.uploadAsync(`${url}/api/upload`, file.uri, {
			uploadType: FileSystem.FileSystemUploadType.MULTIPART,
			headers,
			httpMethod: "POST",
			fieldName: "file",
			mimeType: file.mimetype,
		});

		return JSON.parse(res.body)?.files || [];
	} catch (e) {
		const error = e as AxiosError;

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
	}
}

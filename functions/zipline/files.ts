import * as FileSystem from "expo-file-system";
import axios, { type AxiosError } from "axios";
import * as db from "@/functions/database";
import type {
	APIFile,
	APIFiles,
	APISettings,
	APITag,
	APITransactionResult,
	APIUploadFile,
	APIUploadResponse,
} from "@/types/zipline";
import bytes from "bytes";
import { generateRandomString } from "../util";
import { Directory, Paths } from "expo-file-system/next";

export interface GetFilesOptions {
	id?: string;
	favorite?: boolean;
	sortBy?: "name" | "type" | "size" | "createdAt" | "favorite";
	order?: "asc" | "desc";
	perPage?: number;
	searchField?: "name" | "tags" | "type" | "id";
	searchQuery?: string;
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
		sortBy: options.sortBy || "createdAt",
		order: options.order || "desc",
	});

	if (options.id) params.append("id", options.id);
	if (options.favorite) params.append("favorite", "true");
	if (options.perPage) params.append("perpage", String(options.perPage));
	if (options.searchField) params.append("searchField", options.searchField);
	if (options.searchQuery)
		params.append("searchQuery", encodeURIComponent(options.searchQuery));

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
	add?: string[];
	remove?: string[];
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
	tags?: APITag["id"][];
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

interface UploadFile {
	uri: string;
	blob: Blob,
	mimetype: string;
}

interface UploadZiplineOptions {
	maxChunkSize: string;
	chunkSize: string;
	chunksEnabled: boolean;
}

export interface UploadFileOptions {
	text?: boolean;
	maxViews?: number;
	compression?: number;
	format?: APISettings["settings"]["filesDefaultFormat"];
	password?: string;
	filename?: string;
	folder?: string;
	overrideDomain?: string;
	originalName?: boolean;
	expiresAt?: Date;
}

interface UploadProgressData {
	totalBytesSent: number;
	totalBytesExpectedToSend: number;
}
// POST /api/upload
export async function uploadFiles(
	file: UploadFile,
	ziplineOptions: UploadZiplineOptions,
	options: UploadFileOptions = {},
	onProgress?: (uploadProgressData: UploadProgressData) => void,
): Promise<APIUploadFile[] | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url) return "Invalid URL";

	if (!options.folder && !token) return "Invalid token";

	const blob = file.blob;

	const headers: {
		[key: string]: string;
	} = {};

	if (token) headers.Authorization = token;
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

	const filename = options.filename || file.uri.split("/").pop() || "android-app-upload"
	const extension = filename.split(".").pop() || "bin"

	try {
		if (blob.size < (bytes(ziplineOptions.maxChunkSize) || 0)) {
			const formData = new FormData();
			
			console.log("bs", blob.size)
			formData.append("file", blob, filename);
			console.debug(formData)

			const uploadTask = FileSystem.createUploadTask(
				`${url}/api/upload`,
				file.uri,
				{
					uploadType: FileSystem.FileSystemUploadType.MULTIPART,
					headers,
					httpMethod: "POST",
					fieldName: "file",
					mimeType: file.mimetype,
				},
				onProgress,
			);

			const res = await uploadTask.uploadAsync();

			if (res?.status && (res.status < 200 || res.status > 299)) {
				const data = JSON.parse(res?.body) as {
					error: string;
					statusCode: number;
				} | null;

				if (data) return data.error;

				return "Something went wrong...";
			}

			if (!res) return "Something went wrong...";

			return JSON.parse(res.body)?.files || [];
		}

		const chunkSize = bytes(ziplineOptions.chunkSize) || 0
		
		const numberOfChunks = Math.ceil(blob.size / chunkSize);

		const cacheDirectory = new Directory(Paths.cache)

		const identifier = generateRandomString();

		for (let i = numberOfChunks - 1; i >= 0; i--) {
			const start = i * chunkSize
			const end = Math.min(start + chunkSize, blob.size)

			const chunk = blob.slice(start, end)
			const formData = new FormData()

			formData.append("file", chunk, filename)

			headers["Content-Range"] = `bytes ${start}-${end - 1}/${blob.size}`
			headers["X-Zipline-P-Filename"] = filename;
			headers["X-Zipline-P-Lastchunk"] = i === 0 ? "true" : "false";
			headers["X-Zipline-P-Identifier"] = identifier;
			headers["X-Zipline-P-Content-Type"] = file.mimetype || blob.type;
			headers["X-Zipline-P-Content-Length"] = String(blob.size);

			const arrayBuffer = await blob.arrayBuffer()

			const base64 = Buffer.from(arrayBuffer).toString("base64")

			const chunkURI = `${cacheDirectory.uri}/chunk-${i}.${extension}`

			await FileSystem.writeAsStringAsync(
				chunkURI,
				base64,
				{
					encoding: FileSystem.EncodingType.Base64,
				}
			)

			const uploadTask = FileSystem.createUploadTask(
				`${url}/api/upload`,
				chunkURI,
				{
					uploadType: FileSystem.FileSystemUploadType.MULTIPART,
					headers,
					httpMethod: "POST",
					fieldName: "file",
					mimeType: file.mimetype,
				},
				onProgress,
			);

			const res = await uploadTask.uploadAsync();

			if (res?.status && (res.status < 200 || res.status > 299)) {
				const data = JSON.parse(res?.body) as {
					error: string;
					statusCode: number;
				} | null;

				if (data) return data.error;

				return "Something went wrong...";
			}

			if (!res) return "Something went wrong...";

			const fetchedData = JSON.parse(res.body) as APIUploadResponse

			if (fetchedData.files.length > 0) {
				return fetchedData.files
			}
		}

		return "Something went wrong..."
		// const uploadTask = FileSystem.createUploadTask(
		// 	`${url}/api/upload`,
		// 	file.uri,
		// 	{
		// 		uploadType: FileSystem.FileSystemUploadType.MULTIPART,
		// 		headers,
		// 		httpMethod: "POST",
		// 		fieldName: "file",
		// 		mimeType: file.mimetype,
		// 	},
		// 	onProgress,
		// );

		// const res = await uploadTask.uploadAsync();

		// if (res?.status && (res.status < 200 || res.status > 299)) {
		// 	const data = JSON.parse(res?.body) as {
		// 		error: string;
		// 		statusCode: number;
		// 	} | null;

		// 	if (data) return data.error;

		// 	return "Something went wrong...";
		// }

		// if (!res) return "Something went wrong...";

		// return JSON.parse(res.body)?.files || [];
	} catch (e) {
		const error = e as AxiosError;

		console.error(e, file.uri, file)

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

interface BulkEditFilesOptions {
	files: APIFile["id"][];
	favorite?: boolean;
	folder?: string;
	remove?: boolean;
}

// PATCH/DELETE /api/user/files/transaction
export async function bulkEditFiles({
	files,
	favorite = false,
	folder,
	remove,
}: BulkEditFilesOptions): Promise<APITransactionResult | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		if (remove) {
			const res = await axios.delete(`${url}/api/user/files/transaction`, {
				headers: {
					Authorization: token,
				},
				data: {
					files,
				},
			});

			return res.data;
		}

		const res = await axios.patch(
			`${url}/api/user/files/transaction`,
			{
				files,
				favorite,
				folder,
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

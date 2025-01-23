import { convertToBlob, generateRandomString, guessMimetype } from "../util";
import type { APIFile, APIFiles, APISettings } from "@/types/zipline";
import { getSettings } from "@/functions/zipline/settings";
import type { Mimetypes } from "@/types/mimetypes";
import * as db from "@/functions/database";
import bytes from "bytes";
import axios from "axios";

// GET /api/user/files
export async function getFiles(page: number): Promise<APIFiles | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	const params = new URLSearchParams({
		page: String(page),
	});

	try {
		const res = await axios.get(`${url}/api/user/files?${params}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

// GET /api/user/files/[id]
export async function getFile(id: string): Promise<APIFile | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/user/files/${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

// DELETE /api/user/files/[id]
export async function deleteFile(id: string): Promise<APIFile | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.delete(`${url}/api/user/files/${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
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
): Promise<APIFile | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const file = await getFile(id);

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
		return null;
	}
}

interface EditFileOptions {
	originalName?: string;
	maxViews?: number;
	password?: string;
	type?: string;
	favorite?: boolean;
}
// PATCH /api/user/files/[id]
export async function editFile(
	id: string,
	options: EditFileOptions = {},
): Promise<APIFile | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.patch(`${url}/api/user/files/${id}`, options, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

interface UploadFileOptions {
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
export async function uploadFile(
	file: string,
	options: UploadFileOptions = {},
) {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	const blob = convertToBlob(file);

	const settings = await getSettings();

	const chunksMax = bytes(settings?.chunksMax || "95mb") || 95 * 1024 * 1024;
	const chunksSize =
		bytes(settings?.chunksSize || "25mb") || 25 * 1024 * 1024;

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

	if (blob.size < chunksMax) {
		const filename = `${new Date().toISOString()}.${guessMimetype(blob.type as keyof Mimetypes) || "png"}`;

		const formData = new FormData();

		if (options.text)
			formData.append("file", blob, `${new Date().toISOString()}.txt`);
		else formData.append("file", blob, filename);

		try {
			const res = await axios.post(`${url}/api/upload`, formData, {
				headers,
			});

			const data = await res.data;

			const fileUrl = data?.files?.[0]?.url;

			if (fileUrl) return url;

			return null;
		} catch (e) {
			return null;
		}
	} else {
		const numberOfChunks = Math.ceil(blob.size / chunksSize);

		const identifier = generateRandomString();
		const filename = `${new Date().toISOString()}.${(await guessMimetype(blob.type as keyof Mimetypes)) || "png"}`;

		for (let i = numberOfChunks - 1; i >= 0; i--) {
			const chunkId = numberOfChunks - i;

			const start = i * chunksSize;
			const end = Math.min(start + chunksSize, blob.size);

			const chunk = blob.slice(start, end);
			const formData = new FormData();

			formData.append("file", chunk, filename);

			headers["Content-Range"] = `bytes ${start}-${end - 1}/${blob.size}`;

			headers["X-Zipline-P-Filename"] = filename;
			headers["X-Zipline-P-Lastchunk"] = i === 0 ? "true" : "false";
			headers["X-Zipline-P-Identifier"] = identifier;
			headers["X-Zipline-P-Mimetype"] = blob.type;

			try {
				const response = await axios.post(`${url}/api/upload`, formData, {
					headers,
				});

				const data = await response.data;

				if (data.files?.length > 0) return data.files?.[0]?.url;
			} catch (e) {
				return null;
			}
		}
	}
}

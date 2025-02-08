import type { APITag, APITags } from "@/types/zipline";
import * as db from "@/functions/database";
import axios, { type AxiosError } from "axios";

// GET /api/user/tags
export async function getTags(): Promise<APITags | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user/tags`, {
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

// POST /api/user/tags
export async function createTag(
	name: string,
	color: string,
): Promise<APITag | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.post(
			`${url}/api/user/tags`,
			{
				name,
				color,
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

// DELETE /api/user/tags/[id]
export async function deleteTag(id: string): Promise<APITag | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.delete(`${url}/api/user/tags/${id}`, {
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

interface EditTagOptions {
	color?: string;
	name?: string;
}
// PATCH /api/user/tags/[id]
export async function editTag(id: string, options: EditTagOptions = {}) {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.patch(
			`${url}/api/user/tags/${id}`,
			{
				...options,
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

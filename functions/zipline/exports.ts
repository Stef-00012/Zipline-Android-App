import * as db from "@/functions/database";
import type { APIExports } from "@/types/zipline";
import axios, { type AxiosError } from "axios";

// GET /api/user/export
export async function getUserExports(): Promise<APIExports | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user/export`, {
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

// POST /api/user/export
export async function createUserExport(): Promise<
	{ running: boolean } | string
> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.post(`${url}/api/user/export`, null, {
			headers: {
				Authorization: token,
				"Content-Type": "application/json",
			},
		});

		return res.data.user;
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

// DELETE /api/user/export?id=[id]
export async function deleteUserExport(
	id: string,
): Promise<{ deleted: boolean } | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.delete(`${url}/api/user/export?id=${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data.user;
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

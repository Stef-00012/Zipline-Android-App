import type { APIExports } from "@/types/zipline";
import * as db from "@/functions/database";
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

		return res.data.user;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

// POST /api/user/export
export async function createUserExport(): Promise<{ running: boolean } | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.post(`${url}/api/user/export`, null, {
			headers: {
				Authorization: token,
			},
		});

		return res.data.user;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
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
		
		return  error.response.error;
	}
}

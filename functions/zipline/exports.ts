import * as db from "@/functions/database";
import type { APIExports } from "@/types/zipline";
import axios from "axios";

// GET /api/user/export
export async function getUserExports(): Promise<APIExports | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/user/export`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data.user;
	} catch (e) {
		return null;
	}
}

// POST /api/user/export
export async function createUserExport(): Promise<{ running: boolean } | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.post(`${url}/api/user/export`, null, {
			headers: {
				Authorization: token,
			},
		});

		return res.data.user;
	} catch (e) {
		return null;
	}
}

// DELETE /api/user/export?id=[id]
export async function deleteUserExport(
	id: string,
): Promise<{ deleted: boolean } | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.delete(`${url}/api/user/export?id=${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data.user;
	} catch (e) {
		return null;
	}
}

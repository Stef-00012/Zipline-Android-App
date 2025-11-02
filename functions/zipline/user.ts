import * as db from "@/functions/database";
import type { APIRecentFiles, APISelfUser, APIUser } from "@/types/zipline";
import axios, { type AxiosError } from "axios";

// GET /api/user
export async function getCurrentUser(): Promise<APISelfUser | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user`, {
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

// GET /api/user/recent
export async function getRecentFiles(): Promise<APIRecentFiles | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user/recent`, {
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

// GET /api/user/avatar
export async function getCurrentUserAvatar(): Promise<string | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/user/avatar`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (_e) {
		return null;
	}
}

export type EditUserOptions = Partial<
	Omit<
		APIUser,
		| "id"
		| "createdAt"
		| "updatedAt"
		| "role"
		| "oauthProviders"
		| "totpSecret"
		| "passkeys"
		| "sessions"
		| "quota"
	> & {
		password: string;
	}
>;
// PATCH /api/users/[id]
export async function editCurrentUser(
	options: EditUserOptions = {},
): Promise<APIUser | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.patch(`${url}/api/user`, options, {
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

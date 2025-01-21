import * as db from "@/functions/database";
import type { APIRecentFiles, APISelfUser } from "@/types/zipline";
import axios from "axios";

// GET /api/user
export async function getCurrentUser(): Promise<APISelfUser | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/user`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data.user;
	} catch (e) {
		return null;
	}
}

// GET /api/user/recent
export async function getRecentFiles(): Promise<APIRecentFiles | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/user/recent`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
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
	} catch (e) {
		return null;
	}
}

type EditCurrentUserOptions = Partial<
	Omit<
		APISelfUser,
		| "role"
		| "id"
		| "createdAt"
		| "updatedAt"
		| "oauthProviders"
		| "totpSecret"
		| "passkeys"
		| "quota"
		| "sessions"
		| "view"
	> & {
		view?: Partial<APISelfUser["view"]>;
		avatar?: string;
		password?: string;
	}
>;
export async function editCurrentUser(options: EditCurrentUserOptions = {}) {}

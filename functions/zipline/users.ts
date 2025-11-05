import * as db from "@/functions/database";
import type {
	APIUser,
	APIUserQuota,
	APIUsers,
	APIUsersNoIncl,
} from "@/types/zipline";
import axios, { type AxiosError } from "axios";

// GET /api/users(?noincl=true)
export async function getUsers<T extends boolean | undefined = undefined>(
	noIncl?: T,
): Promise<T extends true ? APIUsersNoIncl | string : APIUsers | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	const params = new URLSearchParams();

	if (noIncl) params.append("noincl", "true");

	try {
		const res = await axios.get(`${url}/api/users?${params}`, {
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

		console.error(e);

		return "Something went wrong...";
	}
}

export async function getUser(id: string): Promise<APIUser | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/users/${id}`, {
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

		console.error(e);

		return "Something went wrong...";
	}
}

// POST /api/users
export async function createUser(
	username: string,
	password: string,
	role: Omit<APIUser["role"], "SUPERADMIN">,
	avatar?: string,
): Promise<APIUser | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.post(
			`${url}/api/users`,
			{
				username,
				password,
				role,
				avatar,
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

		console.error(e);

		return "Something went wrong...";
	}
}

// DELETE /api/users/[id]
export async function deleteUser(
	id: string,
	deleteData = false,
): Promise<APIUser | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.delete(`${url}/api/users/${id}`, {
			headers: {
				Authorization: token,
			},
			data: {
				delete: deleteData,
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

		console.error(e);

		return "Something went wrong...";
	}
}

export type EditUserOptions = Partial<
	Omit<
		APIUser,
		| "id"
		| "createdAt"
		| "updatedAt"
		| "role"
		| "view"
		| "oauthProviders"
		| "totpSecret"
		| "passkeys"
		| "sessions"
		| "quota"
	> & {
		role?: Exclude<APIUser["role"], "SUPERADMIN">;
		quota?: Partial<
			Omit<
				APIUserQuota,
				"id" | "createdAt" | "updatedAt" | "filesQuota" | "userId"
			> & {
				filesType?: APIUserQuota["filesQuota"] | "NONE";
			}
		>;
		password: string;
	}
>;
// PATCH /api/users/[id]
export async function editUser(
	id: string,
	options: EditUserOptions = {},
): Promise<APIUser | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.patch(`${url}/api/users/${id}`, options, {
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

		console.error(e);

		return "Something went wrong...";
	}
}

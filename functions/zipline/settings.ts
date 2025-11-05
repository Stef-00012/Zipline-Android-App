import { settingNames } from "@/constants/adminSettings";
import * as db from "@/functions/database";
import type {
	APIPublicSettings,
	APISettings,
	APIWebSettings,
} from "@/types/zipline";
import axios, { type AxiosError } from "axios";

// GET /api/server/settings
export async function getSettings(): Promise<APISettings | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/server/settings`, {
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

interface APIIssue {
	code: string;
	message: string;
	path: string[];
}
// PATCH /api/server/settings
export async function updateSettings(
	settings: Partial<APISettings["settings"]> = {},
): Promise<APISettings | string[]> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return ["Invalid token or URL"];

	try {
		const res = await axios.patch(`${url}/api/server/settings`, settings, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;

		const data = error.response?.data as
			| {
					issues: APIIssue[];
					statusCode: number;
			  }
			| undefined;

		if (data)
			return data.issues.map(
				(issue) =>
					`${settingNames[issue.path.join(".") as keyof typeof settingNames] || issue.path.join(".")} - ${issue.message}`,
			);

		console.error(e);

		return ["Something went wrong..."];
	}
}

export async function getPublicSettings(): Promise<APIPublicSettings | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const response = await axios.get(`${url}/api/server/public`, {
			headers: {
				Authorization: token,
			},
		});

		return response.data;
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

export async function getWebSettings(): Promise<APIWebSettings | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const response = await axios.get(`${url}/api/server/settings/web`, {
			headers: {
				Authorization: token,
			},
		});

		return response.data;
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

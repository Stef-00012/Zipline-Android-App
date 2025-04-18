import { settingNames } from "@/constants/adminSettings";
import type { APISettings } from "@/types/zipline";
import axios, { type AxiosError } from "axios";
import * as db from "@/functions/database";

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

		return "Something went wrong...";
	}
}

interface APIIssue {
	code: string;
	message: string;
	path: Array<string>;
}
// PATCH /api/server/settings
export async function updateSettings(
	settings: Partial<APISettings> = {},
): Promise<APISettings | Array<string>> {
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
					issues: Array<APIIssue>;
					statusCode: number;
			  }
			| undefined;

		if (data)
			return data.issues.map(
				(issue) =>
					`${settingNames[issue.path.join(".") as keyof typeof settingNames] || issue.path.join(".")} - ${issue.message}`,
			);

		return ["Something went wrong..."];
	}
}

// GET /reload & /api/reload

export async function reloadSettings(): Promise<true | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		await axios.get(`${url}/reload`, {
			headers: {
				Authorization: token,
			},
		});

		await axios.get(`${url}/api/reload`, {
			headers: {
				Authorization: token,
			},
		});

		return true;
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

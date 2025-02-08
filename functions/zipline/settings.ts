import type { APISettings } from "@/types/zipline";
import * as db from "@/functions/database";
import axios, { type AxiosError } from "axios";
import { settingNames } from "@/constants/adminSettings";

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

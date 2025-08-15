import type { APIVersion } from "@/types/zipline";
import axios, { type AxiosError } from "axios";
import * as db from "@/functions/database";
import { minimumVersion } from "@/constants/auth";

// GET /api/version
export async function getVersion(): Promise<APIVersion | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/version`, {
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

		if (data && data.statusCode !== 403) return data.error;

		// start: temp fix for non-admins
		try {
			const res = await axios.head(`${url}/api/server/settings`, {
				headers: {
					Authorization: token,
				},
			});

			if (res.status !== 404)
				return {
					version: minimumVersion,
				};
		} catch (e) {
			const error = e as AxiosError;

			if (error.status !== 404)
				return {
					version: minimumVersion,
				};
		}

		// end: temp fix for non-admins

		return "Something went wrong...";
	}
}

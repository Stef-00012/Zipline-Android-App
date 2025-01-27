import type { APIUserStats, APIStats } from "@/types/zipline";
import * as db from "@/functions/database";
import axios, { type AxiosError } from "axios";

// GET /api/stats
export async function getStats(
	from?: string,
	to?: string,
): Promise<APIStats | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	const params = new URLSearchParams();

	if (from) params.append("from", from);
	if (to) params.append("to", to);

	try {
		const res = await axios.get(`${url}/api/stats?${params}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		const data = error.response?.data as {
			error: string;
			statusCode: number;
		} | undefined;

		if (data) return data.error

		return "Something went wrong..."
	}
}

// GET /api/user/stats
export async function getUserStats(): Promise<APIUserStats | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user/stats`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		const data = error.response?.data as {
			error: string;
			statusCode: number;
		} | undefined;

		if (data) return data.error

		return "Something went wrong..."
	}
}

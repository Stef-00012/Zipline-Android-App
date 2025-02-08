import type { APIUserStats, APIStats } from "@/types/zipline";
import * as db from "@/functions/database";
import axios, { type AxiosError } from "axios";

export interface StatsProps {
	from?: string,
	to?: string,
	all?: boolean
}

// GET /api/stats
export async function getStats({
	from,
	to,
	all = false
}: StatsProps): Promise<APIStats | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	const params = new URLSearchParams();

	if (from) params.append("from", from);
	if (to) params.append("to", to);
	if (all) params.append("all", "true");

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

export function getChartFiles(stats: APIStats) {

}

export function filterStats(data: APIStats, amount = 100) {
	data.sort(
		(a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
	);

	if (data.length <= amount) {
		return data;
	}

	const result = [data[0], data[data.length - 1]];
	const step = (data.length - 2) / (amount - 2);

	for (let i = 1; i < amount - 1; i++) {
		const index = Math.floor(i * step);
		result.push(data[index]);
	}

	return result.sort(
		(a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
	);
}

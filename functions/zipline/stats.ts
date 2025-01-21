import * as db from "@/functions/database";
import type {
	APIUserStats,
	APIStats,
} from "@/types/zipline";
import axios from "axios";

// GET /api/stats
export async function getStats(
	from?: string,
	to?: string,
): Promise<APIStats | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

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
		return null;
	}
}

// GET /api/user/stats
export async function getUserStats(): Promise<APIUserStats | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/user/stats`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}
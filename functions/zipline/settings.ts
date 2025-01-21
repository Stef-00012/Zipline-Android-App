import * as db from "@/functions/database";
import type {
	APISettings,
} from "@/types/zipline";
import axios from "axios";

// GET /api/server/settings
export async function getSettings(): Promise<APISettings | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/server/settings`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

// PATCH /api/server/settings
export async function updateSettings(
	settings: Partial<APISettings> = {},
): Promise<APISettings | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.patch(`${url}/api/server/settings`, settings, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}
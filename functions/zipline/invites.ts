import type { APIInvites } from "@/types/zipline";
import * as db from "@/functions/database";
import axios from "axios";

// GET /api/auth/invites
export async function getInvites(): Promise<APIInvites | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/auth/invites`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

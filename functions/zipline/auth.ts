import * as db from "@/functions/database";
import type { APISelfUser } from "@/types/zipline";
import axios from "axios";

export async function isAuthenticated(): Promise<APISelfUser["role"] | false> {
	const url = db.get("url");
	const token = db.get("token");

	if (!token || !url) return false;

	try {
		const res = await axios.get(`${url}/api/user`, {
			headers: {
				Authorization: token,
			},
		});

		const data: APISelfUser = res.data.user;

		if (!data) return false;

		if (res.status === 200) return data.role;

		return false;
	} catch (e) {
		return false;
	}
}

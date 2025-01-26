import type { APIInvite, APIInvites } from "@/types/zipline";
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

export async function createInvite(expiresAt?: string, maxUses?: number): Promise<APIInvite | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.post(
			`${url}/api/auth/invites`,
			{
				expiresAt,
				maxUses,
			},
			{
				headers: {
					Authorization: token,
				},
			}
		);

		return res.data;
	} catch (e) {
		return null;
	}
}

export async function deleteInvite(code: string): Promise<APIInvite | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.delete(
			`${url}/api/auth/invites/${code}`,
			{
				headers: {
					Authorization: token,
				},
			}
		);

		return res.data;
	} catch (e) {
		return null;
	}
} 
import type { APIURLs, APIURL } from "@/types/zipline";
import * as db from "@/functions/database";
import axios from "axios";

// GET /user/urls
export async function getURLs(): Promise<APIURLs | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/user/urls`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

// GET /user/urls/[id]
export async function getURL(id: string): Promise<APIURL | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.get(`${url}/api/user/urls/${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

export interface CreateURLParams {
	destination: string;
	vanity?: string;
	maxViews?: number;
	password?: string;
}
// POST /api/user/urls
export async function createURL({
	destination,
	vanity,
	maxViews,
	password
}: CreateURLParams): Promise<{
	url: string;
} | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	const headers: { [key: string]: string | number } = {};

	if (maxViews) headers["X-Zipline-Max-Views"] = maxViews;
	if (password) headers["X-Zipline-Password"] = password;

	try {
		const res = await axios.post(
			`${url}/api/user/urls`,
			{
				destination,
				vanity,
			},
			{
				headers: {
					Authorization: token,
					...headers,
				},
			},
		);

		return res.data;
	} catch (e) {
		return null;
	}
}

// DELETE /api/user/urls/[id]
export async function deleteURL(id: string): Promise<APIURL | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.delete(`${url}/api/user/urls/${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

interface editURLOptions {
	destination?: string;
	maxViews?: number;
	password?: string;
}
// PATCH /api/user/urls/[id]
export async function editURL(
	id: string,
	options: editURLOptions = {},
): Promise<APIURL | null> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return null;

	try {
		const res = await axios.patch(`${url}/api/user/urls/${id}`, options, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		return null;
	}
}

import type { APIURLs, APIURL } from "@/types/zipline";
import * as db from "@/functions/database";
import axios, { type AxiosError } from "axios";

// GET /user/urls
export async function getURLs(): Promise<APIURLs | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user/urls`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

// GET /user/urls/[id]
export async function getURL(id: string): Promise<APIURL | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user/urls/${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
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
} | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

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
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

// DELETE /api/user/urls/[id]
export async function deleteURL(id: string): Promise<APIURL | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.delete(`${url}/api/user/urls/${id}`, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

export interface EditURLOptions {
	destination?: string;
	maxViews?: number;
	password?: string;
	vanity?: string;
}
// PATCH /api/user/urls/[id]
export async function editURL(
	id: string,
	options: EditURLOptions = {},
): Promise<APIURL | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.patch(`${url}/api/user/urls/${id}`, options, {
			headers: {
				Authorization: token,
			},
		});

		return res.data;
	} catch (e) {
		const error = e as AxiosError;
		
		return  error.response.error;
	}
}

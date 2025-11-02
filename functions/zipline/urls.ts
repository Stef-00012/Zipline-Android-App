import * as db from "@/functions/database";
import type { APIShortenResponse, APIURL, APIURLs } from "@/types/zipline";
import axios, { type AxiosError } from "axios";

interface GetURLsOptions {
	searchField?: "code" | "vanity" | "destination";
	searchQuery?: string;
}
// GET /user/urls
export async function getURLs(
	options?: GetURLsOptions,
): Promise<APIURLs | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	const searchParams = new URLSearchParams();

	if (options?.searchField && options?.searchQuery) {
		searchParams.append("searchField", options.searchField);
		searchParams.append("searchQuery", options.searchQuery);
	}

	try {
		const res = await axios.get(`${url}/api/user/urls?${searchParams}`, {
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

		if (data) return data.error;

		return "Something went wrong...";
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

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
	}
}

export interface CreateURLParams {
	destination: string;
	vanity?: string;
	maxViews?: number;
	password?: string;
	enabled?: boolean;
}
// POST /api/user/urls
export async function createURL({
	destination,
	vanity,
	maxViews,
	password,
	enabled = true,
}: CreateURLParams): Promise<APIShortenResponse | string> {
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
				enabled,
			},
			{
				headers: {
					Authorization: token,
					...headers,
				},
			},
		);

		return res.data as APIShortenResponse;
	} catch (e) {
		const error = e as AxiosError;

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
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

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
	}
}

export interface EditURLOptions {
	destination?: string;
	maxViews?: number;
	password?: string;
	vanity?: string;
	enabled?: boolean;
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

		const data = error.response?.data as
			| {
					error: string;
					statusCode: number;
			  }
			| undefined;

		if (data) return data.error;

		return "Something went wrong...";
	}
}

import axios, { type AxiosError } from "axios";
import * as db from "@/functions/database";
import type {
	APILoginResponse,
	APISelfUser,
	APITokenResponse,
} from "@/types/zipline";

export async function isAuthenticated(): Promise<APISelfUser["role"] | false> {
	const url = db.get("url");
	const token = db.get("token");

	if (!token || !url) return false;

	try {
		const res = await axios.get(`${url}/api/user`, {
			headers: {
				Authorization: token,
			},
			timeout: 10000,
		});

		const data: APISelfUser = res.data.user;

		if (!data) return false;

		if (res.status === 200) return data.role;

		return false;
	} catch (e) {
		return false;
	}
}

type GetAuthCookieResponse =
	| {
			totp: boolean;
			data?: undefined;
	  }
	| {
			data: string;
			totp?: undefined;
	  }
	| string;
export async function getAuthCookie(
	username: string,
	password: string,
	totpCode?: string,
): Promise<GetAuthCookieResponse> {
	const url = db.get("url");

	if (!url) return "Missing URL";

	try {
		const res = await axios.post(`${url}/api/auth/login`, {
			username,
			password,
			code: totpCode,
		});

		const data = res.data as APILoginResponse;

		if (data.totp)
			return {
				totp: true,
			};

		const setCookieHeader = res.headers["set-cookie"];

		if (!setCookieHeader) return "Something went wrong...";

		const authCookie = setCookieHeader[0].split(";")[0];

		if (!authCookie) return "Something went wrong...";

		return {
			data: authCookie,
		};
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

export async function getToken(getCookieResponse?: GetAuthCookieResponse) {
	const url = db.get("url");

	if (!url) return "Missing URL";

	if (!getCookieResponse) return "Something went wrong...";

	if (typeof getCookieResponse === "string") return getCookieResponse;
	if (getCookieResponse.totp)
		return {
			totp: true,
		};

	const cookie = getCookieResponse.data;

	if (!cookie) return "Something went wrong...";

	try {
		const res = await axios.get(`${url}/api/user/token`, {
			headers: {
				Cookie: cookie,
			},
		});

		const data = res.data as APITokenResponse;

		if (data.token) {
			db.set("token", data.token);
			return {
				token: data.token,
			};
		}

		return "Something went wrong...";
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

export async function login(
	username: string,
	password: string,
	totpCode?: string,
) {
	const cookieRes = await getAuthCookie(username, password, totpCode);

	const token = await getToken(cookieRes);

	return token;
}

export async function getTokenWithToken(): Promise<APITokenResponse | string> {
	const token = db.get("token");
	const url = db.get("url");

	if (!url || !token) return "Invalid token or URL";

	try {
		const res = await axios.get(`${url}/api/user/token`, {
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

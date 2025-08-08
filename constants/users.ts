import type { SelectProps } from "@/components/Select";
import type { APIUser } from "@/types/zipline";

export const userRoles: SelectProps["data"] = [
	{
		label: "User",
		value: "USER",
	},
	{
		label: "Administrator",
		value: "ADMIN",
	},
];

export const fileQuotaTypes: SelectProps["data"] = [
	{
		label: "By Bytes",
		value: "BY_BYTES",
	},
	{
		label: "By File Count",
		value: "BY_FILES",
	},
	{
		label: "No File Quota",
		value: "NONE",
	},
];

export const templateUser: APIUser = {
	createdAt: new Date().toISOString(),
	updatedAt: new Date().toISOString(),
	username: "Placeholder",
	oauthProviders: [],
	totpSecret: null,
	id: "1234567890",
	passkeys: [],
	sessions: [],
	avatar: null,
	role: "USER",
	quota: null,
	view: {},
};

export const searchKeyNames = {
	username: "Username",
	id: "ID",
};

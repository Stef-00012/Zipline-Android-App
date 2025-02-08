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
	avatar: null,
	createdAt: new Date().toISOString(),
	id: "1234567890",
	oauthProviders: [],
	passkeys: [],
	quota: null,
	role: "USER",
	sessions: [],
	totpSecret: null,
	updatedAt: new Date().toISOString(),
	username: "Placeholder",
	view: {},
};

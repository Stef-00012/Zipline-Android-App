import type { SelectProps } from "@/components/Select";
import type { APISettings, ShortenEmbed, UploadEmbed } from "@/types/zipline";
import type { KeyboardTypeOptions } from "react-native";

export const defaultUploadEmbed: UploadEmbed = {
	url: false,
	color: null,
	title: null,
	footer: null,
	thumbnail: false,
	timestamp: false,
	description: null,
	imageOrVideo: false,
};

export const defaultShortenEmbed: ShortenEmbed = {
	url: false,
	color: null,
	title: null,
	footer: null,
	thumbnail: false,
	timestamp: false,
	description: null,
	imageOrVideo: false,
};

export const settingNames = {
	chunksMax: "Max Chunks Size",
	chunksSize: "Chunks Size",
};

export const formats: SelectProps["data"] = [
	{ label: "Random", value: "random" },
	{ label: "Date", value: "date" },
	{ label: "UUID", value: "uuid" },
	{ label: "Use File Name", value: "name" },
	{ label: "Gfycat-style Name", value: "gfycat" },
];

interface CategorySetting {
	type: "category";
	name: string,
	children: Array<Setting>;
}

type SettingPath<T extends keyof APISettings = keyof APISettings> = T extends 'discordOnUploadEmbed'
	? T | `${T}.${keyof UploadEmbed}`
	: T extends "discordOnShortenEmbed"
		? T | `${T}.${keyof ShortenEmbed}`
		: T;



interface InputSetting {
	type: "input",
	name: string,
	keyboardType: KeyboardTypeOptions;
	setting: SettingPath
	multiline?: boolean;
	placeholder?: string
}

interface SelectSetting {
	type: "select",
	name: string,
	options: SelectProps["data"],
	defaultValue: string;
	setting: SettingPath
}

interface SwitchSetting {
	type: "switch"
	name: string
	setting: SettingPath
}

interface SaveSetting {
	type: "save",
	category: SaveCategory
}

type SaveCategory = "core" | "chunks" | "tasks" | "mfa" | "features" | "files" | "urlShortener" | "invites" | "ratelimit" | "website" | "oauth" | "pwa" | "httpWebhooks" | "discordWebhook" | "discordOnUploadWebhook" | "discordOnShortenWebhook";
type Setting = CategorySetting | InputSetting | SelectSetting | SwitchSetting | SaveSetting

export const settings: Array<Setting> =  [
	{
		type: "category",
		name: "Core",
		children: [
			{
				type: "switch",
				name: "Return HTTPS URls",
				setting: "coreReturnHttpsUrls",
			},
			{
				type: "input",
				name: "Default Domain",
				keyboardType: "url",
				setting: "coreDefaultDomain",
				placeholder: "example.com"
			},
			{
				type: "input",
				name: "Temp Directory",
				keyboardType: "default",
				setting: "coreTempDirectory",
				placeholder: "/tmp/zipline"
			},
			{
				type: "save",
				category: "core"
			}
		]
	},
	{
		type: "category",
		name: "Chunks",
		children: [
			{
				type: "input",
				name: "Max Chunks Size",
				keyboardType: "numeric",
				setting: "chunksMax"
			},
			{
				type: "input",
				name: "Chunks Size",
				keyboardType: "numeric",
				setting: "chunksSize"
			},
			{
				type: "save",
				category: "chunks"
			}
		]
	},
	{
		type: "category",
		name: "Tasks",
		children: [
			{
				type: "input",
				name: "Delete Files Interval",
				keyboardType: "default",
				setting: "tasksDeleteInterval",
				placeholder: "30m"
			},
			{
				type: "input",
				name: "Clear Invites Interval",
				keyboardType: "default",
				setting: "tasksClearInvitesInterval",
				placeholder: "30m"
			},
			{
				type: "input",
				name: "Max Views Interval",
				keyboardType: "default",
				setting: "tasksMaxViewsInterval",
				placeholder: "30m"
			},
			{
				type: "input",
				name: "Thumbnail Interval",
				keyboardType: "default",
				setting: "tasksThumbnailsInterval",
				placeholder: "5m"
			},
			// {
			// 	type: "input",
			// 	name: "Metrics Interval",
			// 	keyboardType: "default",
			// 	setting: "tasksMetricsInterval",
			// 	placeholder: "5m"
			// },
			{
				type: "save",
				category: "tasks"
			}
		]
	},
	{
		type: "category",
		name: "Multi-Factor Authentication",
		children: [
			{
				type: "switch",
				name: "Passkeys",
				setting: "mfaPasskeys",
			},
			{
				type: "switch",
				name: "TOTP Enabled",
				setting: "mfaTotpEnabled",
			},
			{
				type: "input",
				name: "Issuer",
				keyboardType: "default",
				setting: "mfaTotpIssuer",
				placeholder: "Zipline"
			},
			{
				type: "save",
				category: "mfa"
			}
		]
	},
	{
		type: "category",
		name: "Features",
		children: [
			
		]
	},
	{
		type: "category",
		name: "Files",
		children: []
	},
	{
		type: "category",
		name: "URL Shortener",
		children: []
	},
	{
		type: "category",
		name: "Invites",
		children: []
	},
	{
		type: "category",
		name: "Ratelimit",
		children: []
	},
	{
		type: "category",
		name: "Website",
		children: []
	},
	{
		type: "category",
		name: "OAuth",
		children: []
	},
	{
		type: "category",
		name: "PWA",
		children: []
	},
	{
		type: "category",
		name: "HTTP Webhooks",
		children: []
	},
	{
		type: "category",
		name: "Discord Webhook",
		children: []
	},
]
import type {
	APISettings,
	ExternalLink,
	ShortenEmbed,
	UploadEmbed,
} from "@/types/zipline";
import type { SelectProps } from "@/components/Select";
import type { KeyboardType } from "react-native";

export const defaultUploadEmbed: UploadEmbed = {
	imageOrVideo: false,
	description: null,
	timestamp: false,
	thumbnail: false,
	footer: null,
	title: null,
	color: null,
	url: false,
};

export const defaultShortenEmbed: ShortenEmbed = {
	description: null,
	timestamp: false,
	footer: null,
	title: null,
	color: null,
	url: false,
};

export const settingNames: Partial<Record<SettingPath, string>> = {
	coreDefaultDomain: "Default Domain",
	coreReturnHttpsUrls: "Return HTTPS URLs",
	coreTempDirectory: "Temporary Directory",

	chunksEnabled: "Enable Chunks",
	chunksMax: "Max Chunks Size",
	chunksSize: "Chunks Size",

	tasksDeleteInterval: "Delete Files Interval",
	tasksClearInvitesInterval: "Clear Invites Interval",
	tasksMaxViewsInterval: "Max Views Interval",
	tasksThumbnailsInterval: "Thumbnail Interval",
	tasksMetricsInterval: "Metrics Interval",

	mfaPasskeys: "Passkeys",
	mfaTotpEnabled: "Enable TOTP",
	mfaTotpIssuer: "Issuer",

	featuresImageCompression: "Image Compression",
	featuresRobotsTxt: "/robots.txt",
	featuresHealthcheck: "Healthcheck",
	featuresUserRegistration: "User Registration",
	featuresOauthRegistration: "OAuth Registration",
	featuresDeleteOnMaxViews: "Delete on Max Views",
	featuresMetricsEnabled: "Enable Metrics",
	featuresMetricsAdminOnly: "Admin Only Metrics",
	featuresMetricsShowUserSpecific: "Show User Specific Metrics",
	featuresThumbnailsEnabled: "Enable Thumbnails",
	featuresThumbnailsNumberThreads: "Thumbnails Number Threads",

	filesRoute: "Route",
	filesLength: "Length",
	filesAssumeMimetypes: "Assume Mimetypes",
	filesRemoveGpsMetadata: "Remove GPS Metadata",
	filesDefaultFormat: "Default Format",
	filesDisabledExtensions: "Disabled Extensions",
	filesMaxFileSize: "Max File Size",
	filesDefaultExpiration: "Default Expiration",
	filesDefaultDateFormat: "Default Date Format",

	urlsRoute: "Route",
	urlsLength: "Length",

	invitesEnabled: "Enable Invites",
	invitesLength: "Length",

	ratelimitEnabled: "Enable Ratelimit",
	ratelimitAdminBypass: "Admin Bypass",
	ratelimitMax: "Max Requests",
	ratelimitWindow: "Window",
	ratelimitAllowList: "Allow List",

	websiteTitle: "Title",
	websiteTitleLogo: "Title Logo",
	websiteExternalLinks: "External Links",
	websiteLoginBackground: "Login Background",
	websiteLoginBackgroundBlur: "Login Background Blur",
	websiteDefaultAvatar: "Default Avatar",
	websiteTos: "Terms of Service",
	websiteThemeDefault: "Default Theme",
	websiteThemeDark: "Dark Theme",
	websiteThemeLight: "Light Theme",

	oauthBypassLocalLogin: "Bypass Local Login",
	oauthLoginOnly: "Login Only",

	oauthDiscordClientId: "Discord Client ID",
	oauthDiscordClientSecret: "Discord Client Secret",
	oauthDiscordRedirectUri: "Discord Redirect URL",

	oauthGoogleClientId: "Google Client ID",
	oauthGoogleClientSecret: "Google Client Secret",
	oauthGoogleRedirectUri: "Google Redirect URL",

	oauthGithubClientId: "GitHub Client ID",
	oauthGithubClientSecret: "GitHub Client Secret",
	oauthGithubRedirectUri: "GitHub Redirect URL",

	oauthOidcClientId: "OIDC Client ID",
	oauthOidcClientSecret: "OIDC Client Secret",
	oauthOidcAuthorizeUrl: "OIDC Authorize URL",
	oauthOidcTokenUrl: "OIDC Token URL",
	oauthOidcUserinfoUrl: "OIDC Userinfo URL",
	oauthOidcRedirectUri: "OIDC Redirect URL",

	pwaEnabled: "PWA Enabled",
	pwaTitle: "Title",
	pwaShortName: "Short Name",
	pwaDescription: "Description",
	pwaThemeColor: "Theme Color",
	pwaBackgroundColor: "Background Color",

	httpWebhookOnUpload: "On Upload",
	httpWebhookOnShorten: "On Shorten",

	discordWebhookUrl: "Webhook URL",
	discordUsername: "Username",
	discordAvatarUrl: "Avatar URL",

	discordOnUploadWebhookUrl: "Webhook URL",
	discordOnUploadAvatarUrl: "Avatar URL",
	discordOnUploadUsername: "Username",
	discordOnUploadContent: "Content",
	discordOnUploadEmbed: "Embed",

	"discordOnUploadEmbed.color": "Color",
	"discordOnUploadEmbed.description": "Description",
	"discordOnUploadEmbed.footer": "Footer",
	"discordOnUploadEmbed.imageOrVideo": "Image/Video",
	"discordOnUploadEmbed.thumbnail": "Thumbnail",
	"discordOnUploadEmbed.timestamp": "Timestamp",
	"discordOnUploadEmbed.title": "Title",
	"discordOnUploadEmbed.url": "URL",

	discordOnShortenWebhookUrl: "Webhook URL",
	discordOnShortenUsername: "Username",
	discordOnShortenAvatarUrl: "Avatar URL",
	discordOnShortenContent: "Content",
	discordOnShortenEmbed: "Embed",

	"discordOnShortenEmbed.color": "Color",
	"discordOnShortenEmbed.description": "Description",
	"discordOnShortenEmbed.footer": "Footer",
	"discordOnShortenEmbed.timestamp": "Timestamp",
	"discordOnShortenEmbed.title": "Title",
	"discordOnShortenEmbed.url": "URL",
};

export interface SaveSettings {
	coreDefaultDomain: string | null;
	coreReturnHttpsUrls: boolean;
	coreTempDirectory: string;

	chunksEnabled: boolean;
	chunksMax: number | string;
	chunksSize: number | string;

	tasksDeleteInterval: number | string;
	tasksClearInvitesInterval: number | string;
	tasksMaxViewsInterval: number | string;
	tasksThumbnailsInterval: number | string;
	tasksMetricsInterval: number | string;

	mfaPasskeys: boolean;
	mfaTotpEnabled: boolean;
	mfaTotpIssuer: string;

	featuresImageCompression: boolean;
	featuresRobotsTxt: boolean;
	featuresHealthcheck: boolean;
	featuresUserRegistration: boolean;
	featuresOauthRegistration: boolean;
	featuresDeleteOnMaxViews: boolean;
	featuresMetricsEnabled: boolean;
	featuresMetricsAdminOnly: boolean;
	featuresMetricsShowUserSpecific: boolean;
	featuresThumbnailsEnabled: boolean;
	featuresThumbnailsNumberThreads: number;

	filesRoute: string;
	filesLength: number;
	filesAssumeMimetypes: boolean;
	filesRemoveGpsMetadata: boolean;
	filesDefaultFormat: "random" | "uuid" | "date" | "name" | "gfycat";
	filesDisabledExtensions: string;
	filesMaxFileSize: number | string;
	filesDefaultExpiration: string | null;
	filesDefaultDateFormat: string;
	filesRandomWordsSeparator: string;
	filesRandomWordsNumAdjectives: number;

	urlsRoute: string;
	urlsLength: number;

	invitesEnabled: boolean;
	invitesLength: number;

	ratelimitEnabled: boolean;
	ratelimitAdminBypass: boolean;
	ratelimitMax: number;
	ratelimitWindow: number | null;
	ratelimitAllowList: string;

	websiteTitle: string;
	websiteTitleLogo: string | null;
	websiteExternalLinks: string;
	websiteLoginBackground: string | null;
	websiteLoginBackgroundBlur: boolean;
	websiteDefaultAvatar: string | null;
	websiteTos: string | null;
	websiteThemeDefault: string;
	websiteThemeDark: string;
	websiteThemeLight: string;

	oauthBypassLocalLogin: boolean;
	oauthLoginOnly: boolean;

	oauthDiscordClientId: string | null;
	oauthDiscordClientSecret: string | null;
	oauthDiscordRedirectUri: string | null;

	oauthGoogleClientId: string | null;
	oauthGoogleClientSecret: string | null;
	oauthGoogleRedirectUri: string | null;

	oauthGithubClientId: string | null;
	oauthGithubClientSecret: string | null;
	oauthGithubRedirectUri: string | null;

	oauthOidcClientId: string | null;
	oauthOidcClientSecret: string | null;
	oauthOidcAuthorizeUrl: string | null;
	oauthOidcTokenUrl: string | null;
	oauthOidcUserinfoUrl: string | null;
	oauthOidcRedirectUri: string | null;

	pwaEnabled: boolean;
	pwaTitle: string;
	pwaShortName: string;
	pwaDescription: string;
	pwaThemeColor: string;
	pwaBackgroundColor: string;

	httpWebhookOnUpload: string | null;
	httpWebhookOnShorten: string | null;

	discordWebhookUrl: string | null;
	discordUsername: string | null;
	discordAvatarUrl: string | null;

	discordOnUploadWebhookUrl: string | null;
	discordOnUploadAvatarUrl: string | null;
	discordOnUploadUsername: string | null;
	discordOnUploadContent: string | null;
	discordOnUploadEmbed: boolean;

	"discordOnUploadEmbed.color": string | null;
	"discordOnUploadEmbed.description": string | null;
	"discordOnUploadEmbed.footer": string | null;
	"discordOnUploadEmbed.imageOrVideo": boolean;
	"discordOnUploadEmbed.thumbnail": boolean;
	"discordOnUploadEmbed.timestamp": boolean;
	"discordOnUploadEmbed.title": string | null;
	"discordOnUploadEmbed.url": boolean;

	discordOnShortenWebhookUrl: string | null;
	discordOnShortenUsername: string | null;
	discordOnShortenAvatarUrl: string | null;
	discordOnShortenContent: string | null;
	discordOnShortenEmbed: boolean;

	"discordOnShortenEmbed.color": string | null;
	"discordOnShortenEmbed.description": string | null;
	"discordOnShortenEmbed.footer": string | null;
	"discordOnShortenEmbed.timestamp": boolean;
	"discordOnShortenEmbed.title": string | null;
	"discordOnShortenEmbed.url": boolean;
}

export const formats: SelectProps["data"] = [
	{
		label: "Random",
		value: "random",
	},
	{
		label: "Date",
		value: "date",
	},
	{
		label: "UUID",
		value: "uuid",
	},
	{
		label: "Use File Name",
		value: "name",
	},
	{
		label: "Gfycat-style Name",
		value: "gfycat",
	},
];

export type SettingPath<T extends keyof APISettings = keyof APISettings> =
	T extends "discordOnUploadEmbed"
		? T | `${T}.${keyof UploadEmbed}`
		: T extends "discordOnShortenEmbed"
			? T | `${T}.${keyof ShortenEmbed}`
			: T;

export type SaveCategories =
	| "core"
	| "chunks"
	| "tasks"
	| "mfa"
	| "features"
	| "files"
	| "urlShortener"
	| "invites"
	| "ratelimit"
	| "website"
	| "oauth"
	| "pwa"
	| "httpWebhooks"
	| "discordWebhook"
	| "discordOnUploadWebhook"
	| "discordOnShortenWebhook";

interface Category {
	children: Array<Setting>;
	type: "category";
	if?: SettingPath;
	name: string;
	category: `${
		| SaveCategories
		| "discordOAuth"
		| "githubOAuth"
		| "googleOAuth"
		| "OIDCOAuth"
		| "discordOnUploadEmbed"
		| "discordOnShortenEmbed"}Category`;
}

interface Input {
	displayType?: "bytes" | "time";
	keyboardType?: KeyboardType;
	passwordInput?: boolean;
	placeholder?: string;
	setting: SettingPath;
	joinString?: string;
	multiline?: boolean;
	type: "input";
	name: string;
}

interface Switch {
	setType?: "upload" | "shorten";
	setting: SettingPath;
	type: "switch";
	name: string;
}

interface Select {
	defaultValue?: SelectProps["data"][0]["value"];
	options: SelectProps["data"];
	multiSelect?: boolean;
	setting: SettingPath;
	placeholder: string;
	type: "select";
	name: string;
}

interface ColorPicker {
	type: "colorPicker";
	setting: SettingPath;
	name: string;
}

interface Save {
	type: "save";
	category: SaveCategories;
}

interface ExternalUrl {
	type: "externalUrls";
}

export type Setting =
	| Category
	| Input
	| Switch
	| Select
	| ExternalUrl
	| ColorPicker
	| Save;

export const settings: Array<Setting> = [
	{
		type: "category",
		name: "Core",
		category: "coreCategory",
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
				placeholder: "example.com",
			},
			{
				type: "input",
				name: "Temp Directory",
				keyboardType: "default",
				setting: "coreTempDirectory",
				placeholder: "/tmp/zipline",
			},
			{
				type: "save",
				category: "core",
			},
		],
	},
	{
		type: "category",
		name: "Chunks",
		category: "chunksCategory",
		children: [
			{
				type: "input",
				name: "Max Chunks Size",
				keyboardType: "numeric",
				setting: "chunksMax",
				placeholder: "95mb",
				displayType: "bytes",
			},
			{
				type: "input",
				name: "Chunks Size",
				keyboardType: "numeric",
				setting: "chunksSize",
				placeholder: "25mb",
				displayType: "bytes",
			},
			{
				type: "save",
				category: "chunks",
			},
		],
	},
	{
		type: "category",
		name: "Tasks",
		category: "tasksCategory",
		children: [
			{
				type: "input",
				name: "Delete Files Interval",
				keyboardType: "default",
				setting: "tasksDeleteInterval",
				placeholder: "30m",
				displayType: "time",
			},
			{
				type: "input",
				name: "Clear Invites Interval",
				keyboardType: "default",
				setting: "tasksClearInvitesInterval",
				placeholder: "30m",
				displayType: "time",
			},
			{
				type: "input",
				name: "Max Views Interval",
				keyboardType: "default",
				setting: "tasksMaxViewsInterval",
				placeholder: "30m",
				displayType: "time",
			},
			{
				type: "input",
				name: "Thumbnail Interval",
				keyboardType: "default",
				setting: "tasksThumbnailsInterval",
				placeholder: "5m",
				displayType: "time",
			},
			// {
			// 	type: "input",
			// 	name: "Metrics Interval",
			// 	keyboardType: "default",
			// 	setting: "tasksMetricsInterval",
			// 	placeholder: "5m"
			//  displayType: "time"
			// },
			{
				type: "save",
				category: "tasks",
			},
		],
	},
	{
		type: "category",
		name: "Multi-Factor Authentication",
		category: "mfaCategory",
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
				placeholder: "Zipline",
			},
			{
				type: "save",
				category: "mfa",
			},
		],
	},
	{
		type: "category",
		name: "Features",
		category: "featuresCategory",
		children: [
			{
				type: "switch",
				name: "Image Compression",
				setting: "featuresImageCompression",
			},
			{
				type: "switch",
				name: "/robots.txt",
				setting: "featuresRobotsTxt",
			},
			{
				type: "switch",
				name: "Healthcheck",
				setting: "featuresHealthcheck",
			},
			{
				type: "switch",
				name: "User Registration",
				setting: "featuresUserRegistration",
			},
			{
				type: "switch",
				name: "OAuth Registration",
				setting: "featuresOauthRegistration",
			},
			{
				type: "switch",
				name: "Delete on Max Views",
				setting: "featuresDeleteOnMaxViews",
			},
			{
				type: "switch",
				name: "Enable Metrics",
				setting: "featuresMetricsEnabled",
			},
			{
				type: "switch",
				name: "Admin Only Metrics",
				setting: "featuresMetricsAdminOnly",
			},
			{
				type: "switch",
				name: "Show User Specific Metrics",
				setting: "featuresMetricsShowUserSpecific",
			},
			{
				type: "switch",
				name: "Enable Thumbnails",
				setting: "featuresThumbnailsEnabled",
			},
			{
				type: "input",
				name: "Thumbnails Number Threads",
				setting: "featuresThumbnailsNumberThreads",
				keyboardType: "numeric",
				placeholder: "Enter a number...",
			},
			{
				type: "save",
				category: "features",
			},
		],
	},
	{
		type: "category",
		name: "Files",
		category: "filesCategory",
		children: [
			{
				type: "input",
				name: "Route",
				setting: "filesRoute",
				keyboardType: "default",
			},
			{
				type: "input",
				name: "Length",
				setting: "filesLength",
				keyboardType: "numeric",
			},
			{
				type: "switch",
				name: "Assume Mimetypes",
				setting: "filesAssumeMimetypes",
			},
			{
				type: "switch",
				name: "Remove GPS Metadata",
				setting: "filesRemoveGpsMetadata",
			},
			{
				type: "select",
				options: formats,
				name: "Default Format",
				defaultValue: "random",
				setting: "filesDefaultFormat",
				placeholder: "Select format...",
			},
			{
				type: "input",
				name: "Disabled Extensions",
				setting: "filesDisabledExtensions",
				keyboardType: "default",
				joinString: ", ",
			},
			{
				type: "input",
				name: "Max File Size",
				setting: "filesMaxFileSize",
				keyboardType: "default",
				displayType: "bytes",
			},
			{
				type: "input",
				name: "Default Expiration",
				setting: "filesDefaultExpiration",
				keyboardType: "default",
			},
			{
				type: "input",
				name: "Default Date Format",
				setting: "filesDefaultDateFormat",
				keyboardType: "default",
			},
			{
				type: "input",
				name: "Random Words Num Adjectives",
				setting: "filesRandomWordsNumAdjectives",
				keyboardType: "numeric",
			},
			{
				type: "input",
				name: "Random Words Separator",
				setting: "filesRandomWordsSeparator",
				placeholder: "-",
			},
			{
				type: "save",
				category: "files",
			},
		],
	},
	{
		type: "category",
		name: "URL Shortener",
		category: "urlShortenerCategory",
		children: [
			{
				type: "input",
				name: "Route",
				setting: "urlsRoute",
				keyboardType: "default",
			},
			{
				type: "input",
				name: "Length",
				setting: "urlsLength",
				keyboardType: "numeric",
			},
			{
				type: "save",
				category: "urlShortener",
			},
		],
	},
	{
		type: "category",
		name: "Invites",
		category: "invitesCategory",
		children: [
			{
				type: "switch",
				name: "Enable Invites",
				setting: "invitesEnabled",
			},
			{
				type: "input",
				name: "Length",
				setting: "invitesLength",
				keyboardType: "numeric",
			},
			{
				type: "save",
				category: "invites",
			},
		],
	},
	{
		type: "category",
		name: "Ratelimit",
		category: "ratelimitCategory",
		children: [
			{
				type: "switch",
				name: "Enable Ratelimit",
				setting: "ratelimitEnabled",
			},
			{
				type: "switch",
				name: "Admin Bypass",
				setting: "ratelimitAdminBypass",
			},
			{
				type: "input",
				name: "Max Requests",
				setting: "ratelimitMax",
				keyboardType: "numeric",
			},
			{
				type: "input",
				name: "Window",
				setting: "ratelimitWindow",
				keyboardType: "numeric",
			},
			{
				type: "input",
				name: "Allow List",
				setting: "ratelimitAllowList",
				joinString: ", ",
				keyboardType: "default",
			},
			{
				type: "save",
				category: "ratelimit",
			},
		],
	},
	{
		type: "category",
		name: "Website",
		category: "websiteCategory",

		children: [
			{
				type: "input",
				name: "Title",
				setting: "websiteTitle",
				keyboardType: "default",
			},
			{
				type: "input",
				name: "Title Logo",
				setting: "websiteTitleLogo",
				keyboardType: "url",
			},
			{
				type: "externalUrls",
			},
			{
				type: "input",
				name: "Login Backgorund",
				setting: "websiteLoginBackground",
				keyboardType: "url",
			},
			{
				type: "switch",
				name: "Login Backgorund Blur",
				setting: "websiteLoginBackgroundBlur",
			},
			{
				type: "input",
				name: "Default Avatar",
				setting: "websiteDefaultAvatar",
				keyboardType: "default",
			},
			{
				type: "input",
				name: "Terms of Service",
				setting: "websiteTos",
				keyboardType: "default",
			},
			{
				type: "input",
				name: "Default Theme",
				setting: "websiteThemeDefault",
				keyboardType: "default",
			},
			{
				type: "input",
				name: "Dark Theme",
				setting: "websiteThemeDark",
				keyboardType: "default",
			},
			{
				type: "input",
				name: "Light Theme",
				setting: "websiteThemeLight",
				keyboardType: "default",
			},
			{
				type: "save",
				category: "website",
			},
		],
	},
	{
		type: "category",
		name: "OAuth",
		category: "oauthCategory",
		children: [
			{
				type: "switch",
				name: "Bypass Local Login",
				setting: "oauthBypassLocalLogin",
			},
			{
				type: "switch",
				name: "Login Only",
				setting: "oauthLoginOnly",
			},
			{
				type: "category",
				name: "Discord",
				category: "discordOAuthCategory",
				children: [
					{
						type: "input",
						name: "Discord Client ID",
						setting: "oauthDiscordClientId",
						keyboardType: "default",
					},
					{
						type: "input",
						name: "Discord Client Secret",
						setting: "oauthDiscordClientSecret",
						keyboardType: "default",
						passwordInput: true,
					},
					{
						type: "input",
						name: "Discord Redirect URI",
						setting: "oauthDiscordRedirectUri",
						keyboardType: "default",
					},
				],
			},
			{
				type: "category",
				name: "Google",
				category: "googleOAuthCategory",
				children: [
					{
						type: "input",
						name: "Google Client ID",
						setting: "oauthGoogleClientId",
						keyboardType: "default",
					},
					{
						type: "input",
						name: "Google Client Secret",
						setting: "oauthGoogleClientSecret",
						keyboardType: "default",
						passwordInput: true,
					},
					{
						type: "input",
						name: "Google Redirect URI",
						setting: "oauthGoogleRedirectUri",
						keyboardType: "default",
					},
				],
			},
			{
				type: "category",
				name: "GitHub",
				category: "githubOAuthCategory",
				children: [
					{
						type: "input",
						name: "GitHub Client ID",
						setting: "oauthGithubClientId",
						keyboardType: "default",
					},
					{
						type: "input",
						name: "GitHub Client Secret",
						setting: "oauthGithubClientSecret",
						keyboardType: "default",
						passwordInput: true,
					},
					{
						type: "input",
						name: "GitHub Redirect URI",
						setting: "oauthGithubRedirectUri",
						keyboardType: "default",
					},
				],
			},
			{
				type: "category",
				name: "OpenID Connect",
				category: "OIDCOAuthCategory",
				children: [
					{
						type: "input",
						name: "OIDC Client ID",
						setting: "oauthOidcClientId",
						keyboardType: "default",
					},
					{
						type: "input",
						name: "OIDC Client Secret",
						setting: "oauthOidcClientSecret",
						keyboardType: "default",
						passwordInput: true,
					},
					{
						type: "input",
						name: "OIDC Authorize URL",
						setting: "oauthOidcAuthorizeUrl",
						keyboardType: "default",
					},
					{
						type: "input",
						name: "OIDC Token URL",
						setting: "oauthOidcTokenUrl",
						keyboardType: "default",
					},
					{
						type: "input",
						name: "OIDC Userinfo URL",
						setting: "oauthOidcUserinfoUrl",
						keyboardType: "default",
					},
					{
						type: "input",
						name: "OIDC Redirect URL",
						setting: "oauthOidcRedirectUri",
						keyboardType: "default",
					},
				],
			},
			{
				type: "save",
				category: "oauth",
			},
		],
	},
	{
		type: "category",
		name: "PWA",
		category: "pwaCategory",
		children: [
			{
				type: "switch",
				name: "PWA Enabled",
				setting: "pwaEnabled",
			},
			{
				type: "input",
				name: "Title",
				setting: "pwaTitle",
				keyboardType: "default",
				placeholder: "Zipline",
			},
			{
				type: "input",
				name: "Short Name",
				setting: "pwaShortName",
				keyboardType: "default",
				placeholder: "Zipline",
			},
			{
				type: "input",
				name: "Description",
				setting: "pwaDescription",
				keyboardType: "default",
				placeholder: "Zipline",
			},
			{
				type: "input",
				name: "Theme Color",
				setting: "pwaThemeColor",
				keyboardType: "default",
				placeholder: "#000000",
			},
			{
				type: "colorPicker",
				name: "Background Color",
				setting: "pwaBackgroundColor",
			},
			{
				type: "save",
				category: "pwa",
			},
		],
	},
	{
		type: "category",
		name: "HTTP Webhooks",
		category: "httpWebhooksCategory",
		children: [
			{
				type: "input",
				name: "On Upload",
				setting: "httpWebhookOnUpload",
				keyboardType: "url",
				placeholder: "https://example.com/upload",
			},
			{
				type: "input",
				name: "On Shorten",
				setting: "httpWebhookOnShorten",
				keyboardType: "url",
				placeholder: "https://example.com/shorten",
			},
			{
				type: "save",
				category: "httpWebhooks",
			},
		],
	},
	{
		type: "category",
		name: "Discord Webhook",
		category: "discordWebhookCategory",
		children: [
			{
				type: "input",
				name: "Webhook URL",
				setting: "discordWebhookUrl",
				keyboardType: "url",
				placeholder: "https://discord.com/api/webhooks/...",
			},
			{
				type: "input",
				name: "Username",
				setting: "discordUsername",
				keyboardType: "default",
				placeholder: "Zipline",
			},
			{
				type: "input",
				name: "Avatar URL",
				setting: "discordAvatarUrl",
				keyboardType: "url",
				placeholder: "https://example.com/avatar.png",
			},
			{
				type: "save",
				category: "discordWebhook",
			},
			{
				type: "category",
				name: "On Upload",
				category: "discordOnUploadWebhookCategory",
				children: [
					{
						type: "input",
						name: "Webhook URL",
						setting: "discordOnUploadWebhookUrl",
						keyboardType: "url",
						placeholder: "https://discord.com/api/webhooks/...",
					},
					{
						type: "input",
						name: "Username",
						setting: "discordOnUploadUsername",
						keyboardType: "default",
						placeholder: "Zipline Uploads",
					},
					{
						type: "input",
						name: "Avatar URL",
						setting: "discordOnUploadAvatarUrl",
						keyboardType: "url",
						placeholder: "https://example.com/uploadAvatar.png",
					},
					{
						type: "input",
						name: "Content",
						setting: "discordOnUploadContent",
						keyboardType: "default",
						multiline: true,
					},
					{
						type: "switch",
						name: "Embed",
						setting: "discordOnUploadEmbed",
						setType: "upload",
					},
					{
						type: "category",
						name: "Embed Settings",
						category: "discordOnUploadEmbedCategory",
						if: "discordOnUploadEmbed",
						children: [
							{
								type: "input",
								name: "Title",
								setting: "discordOnUploadEmbed.title",
								keyboardType: "default",
							},
							{
								type: "input",
								name: "Description",
								setting: "discordOnUploadEmbed.description",
								keyboardType: "default",
							},
							{
								type: "input",
								name: "Footer",
								setting: "discordOnUploadEmbed.footer",
								keyboardType: "default",
							},
							{
								type: "colorPicker",
								name: "Color",
								setting: "discordOnUploadEmbed.color",
							},
							{
								type: "switch",
								name: "Thumbnail",
								setting: "discordOnUploadEmbed.thumbnail",
							},
							{
								type: "switch",
								name: "Image/Video",
								setting: "discordOnUploadEmbed.imageOrVideo",
							},
							{
								type: "switch",
								name: "Timestamp",
								setting: "discordOnUploadEmbed.timestamp",
							},
							{
								type: "switch",
								name: "URL",
								setting: "discordOnUploadEmbed.url",
							},
						],
					},
					{
						type: "save",
						category: "discordOnUploadWebhook",
					},
				],
			},
			{
				type: "category",
				name: "On Shorten",
				category: "discordOnShortenWebhookCategory",
				children: [
					{
						type: "input",
						name: "Webhook URL",
						setting: "discordOnShortenWebhookUrl",
						keyboardType: "url",
						placeholder: "https://discord.com/api/webhooks/...",
					},
					{
						type: "input",
						name: "Username",
						setting: "discordOnShortenUsername",
						keyboardType: "default",
						placeholder: "Zipline Shortens",
					},
					{
						type: "input",
						name: "Avatar URL",
						setting: "discordOnShortenAvatarUrl",
						keyboardType: "url",
						placeholder: "https://example.com/shortenAvatar.png",
					},
					{
						type: "input",
						name: "Content",
						setting: "discordOnShortenContent",
						keyboardType: "default",
						multiline: true,
					},
					{
						type: "switch",
						name: "Embed",
						setting: "discordOnShortenEmbed",
					},
					{
						type: "category",
						name: "Embed Settings",
						category: "discordOnShortenEmbedCategory",
						if: "discordOnShortenEmbed",
						children: [
							{
								type: "input",
								name: "Title",
								setting: "discordOnShortenEmbed.title",
								keyboardType: "default",
							},
							{
								type: "input",
								name: "Description",
								setting: "discordOnShortenEmbed.description",
								keyboardType: "default",
							},
							{
								type: "input",
								name: "Footer",
								setting: "discordOnShortenEmbed.footer",
								keyboardType: "default",
							},
							{
								type: "colorPicker",
								name: "Color",
								setting: "discordOnShortenEmbed.color",
							},
							{
								type: "switch",
								name: "Timestamp",
								setting: "discordOnShortenEmbed.timestamp",
							},
							{
								type: "switch",
								name: "URL",
								setting: "discordOnShortenEmbed.url",
							},
						],
					},
					{
						type: "save",
						category: "discordOnShortenWebhook",
					},
				],
			},
		],
	},
];

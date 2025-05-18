import type {
	APISettings,
	ExternalLink,
	ShortenEmbed,
	UploadEmbed,
} from "@/types/zipline";
import type { SelectProps } from "@/components/Select";
import type { DimensionValue, KeyboardType } from "react-native";

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
	featuresVersionChecking: "Version Checking",
	featuresVersionAPI: "Version API URL",

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
	featuresVersionChecking: boolean,
	featuresVersionAPI: string,

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
	description?: string;
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
	description?: string;
	skeletonWidth: DimensionValue;
}

interface Switch {
	setType?: "upload" | "shorten";
	setting: SettingPath;
	type: "switch";
	name: string;
	description: string;
}

interface Select {
	defaultValue?: SelectProps["data"][0]["value"];
	options: SelectProps["data"];
	multiSelect?: boolean;
	setting: SettingPath;
	placeholder: string;
	type: "select";
	name: string;
	description: string;
}

interface ColorPicker {
	type: "colorPicker";
	setting: SettingPath;
	name: string;
	description: string;
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
				description: "Return URLs with HTTPS protocol.",
			},
			{
				type: "input",
				name: "Default Domain",
				keyboardType: "url",
				setting: "coreDefaultDomain",
				placeholder: "example.com",
				description:
					"The domain to use when generating URLs. This value should not include the protocol.",
				skeletonWidth: "40%",
			},
			{
				type: "input",
				name: "Temp Directory",
				keyboardType: "default",
				setting: "coreTempDirectory",
				placeholder: "/tmp/zipline",
				description:
					"The directory to store temporary files. If the path is invalid, certain functions may break. Requires a server restart.",
				skeletonWidth: "60%",
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
				type: "switch",
				name: "Enable Chunks",
				setting: "chunksEnabled",
				description: "Enable chunked uploads.",
			},
			{
				type: "input",
				name: "Max Chunks Size",
				keyboardType: "numeric",
				setting: "chunksMax",
				placeholder: "95mb",
				displayType: "bytes",
				description:
					"Maximum size of an upload before it is split into chunks.",
				skeletonWidth: 50,
			},
			{
				type: "input",
				name: "Chunks Size",
				keyboardType: "numeric",
				setting: "chunksSize",
				placeholder: "25mb",
				displayType: "bytes",
				description: "Size of each chunk.",
				skeletonWidth: 40,
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
		description: "All options require a restart to take effect.",
		category: "tasksCategory",
		children: [
			{
				type: "input",
				name: "Delete Files Interval",
				keyboardType: "default",
				setting: "tasksDeleteInterval",
				placeholder: "30m",
				displayType: "time",
				description: "How often to check and delete expired files.",
				skeletonWidth: 40,
			},
			{
				type: "input",
				name: "Clear Invites Interval",
				keyboardType: "default",
				setting: "tasksClearInvitesInterval",
				placeholder: "30m",
				displayType: "time",
				description: "How often to check and clear expired/used invites.",
				skeletonWidth: 50,
			},
			{
				type: "input",
				name: "Max Views Interval",
				keyboardType: "default",
				setting: "tasksMaxViewsInterval",
				placeholder: "30m",
				displayType: "time",
				description:
					"How often to check and delete files that have reached max views.",
				skeletonWidth: 50,
			},
			{
				type: "input",
				name: "Thumbnail Interval",
				keyboardType: "default",
				setting: "tasksThumbnailsInterval",
				placeholder: "5m",
				displayType: "time",
				description:
					"How often to check and generate thumbnails for video files.",
				skeletonWidth: 40,
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
				description:
					"Enable the use of passwordless login with the use of WebAuthn passkeys like your phone, security keys, etc.",
			},
			{
				type: "switch",
				name: "TOTP Enabled",
				setting: "mfaTotpEnabled",
				description:
					"Enable Time-based One-Time Passwords with the use of an authenticator app.",
			},
			{
				type: "input",
				name: "Issuer",
				keyboardType: "default",
				setting: "mfaTotpIssuer",
				placeholder: "Zipline",
				description: "The issuer to use for the TOTP token.",
				skeletonWidth: "40%",
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
				description: "Allows the ability for users to compress images.",
			},
			{
				type: "switch",
				name: "/robots.txt",
				setting: "featuresRobotsTxt",
				description:
					"Enables a robots.txt file for search engine optimization. Requires a server restart.",
			},
			{
				type: "switch",
				name: "Healthcheck",
				setting: "featuresHealthcheck",
				description:
					"Enables a healthcheck route for uptime monitoring. Requires a server restart.",
			},
			{
				type: "switch",
				name: "User Registration",
				setting: "featuresUserRegistration",
				description: "Allows users to register an account on the server.",
			},
			{
				type: "switch",
				name: "OAuth Registration",
				setting: "featuresOauthRegistration",
				description:
					"Allows users to register an account using OAuth providers.",
			},
			{
				type: "switch",
				name: "Delete on Max Views",
				setting: "featuresDeleteOnMaxViews",
				description:
					"Automatically deletes files/urls after they reach the maximum view count. Requires a server restart.",
			},
			{
				type: "switch",
				name: "Enable Metrics",
				setting: "featuresMetricsEnabled",
				description:
					"Enables metrics for the server. Requires a server restart.",
			},
			{
				type: "switch",
				name: "Admin Only Metrics",
				setting: "featuresMetricsAdminOnly",
				description: "Requires an administrator to view metrics.",
			},
			{
				type: "switch",
				name: "Show User Specific Metrics",
				setting: "featuresMetricsShowUserSpecific",
				description: "Shows metrics specific to each user, for all users.",
			},
			{
				type: "switch",
				name: "Enable Thumbnails",
				setting: "featuresThumbnailsEnabled",
				description:
					"Enables thumbnail generation for images. Requires a server restart.",
			},
			{
				type: "input",
				name: "Thumbnails Number Threads",
				setting: "featuresThumbnailsNumberThreads",
				keyboardType: "numeric",
				placeholder: "Enter a number...",
				description:
					"Number of threads to use for thumbnail generation, usually the number of CPU threads. Requires a server restart.",
				skeletonWidth: 30,
			},
			{
				type: "switch",
				name: "Version Checking",
				setting: "featuresVersionChecking",
				description: "Enable version checking for the server. This will check for updates and display the status on the sidebar to all users.",
			},
			{
				type: "input",
				name: "Version API URL",
				setting: "featuresVersionAPI",
				keyboardType: "url",
				placeholder: "https://zipline-version.diced.sh",
				description:
					"The URL of the version checking server. The default is [https://zipline-version.diced.sh](https://zipline-version.diced.sh). Visit the [GitHub](https://github.com/diced/zipline-version-worker) to host your own version checking server.",
				skeletonWidth: "65%",
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
				description:
					"The route to use for file uploads. Requires a server restart.",
				skeletonWidth: 45,
			},
			{
				type: "input",
				name: "Length",
				setting: "filesLength",
				keyboardType: "numeric",
				description:
					"The length of the file name (for randomly generated names).",
				skeletonWidth: 40,
			},
			{
				type: "switch",
				name: "Assume Mimetypes",
				setting: "filesAssumeMimetypes",
				description: "Assume the mimetype of a file for its extension.",
			},
			{
				type: "switch",
				name: "Remove GPS Metadata",
				setting: "filesRemoveGpsMetadata",
				description: "Remove GPS metadata from files.",
			},
			{
				type: "select",
				options: formats,
				name: "Default Format",
				defaultValue: "random",
				setting: "filesDefaultFormat",
				placeholder: "Select format...",
				description: "The default format to use for file names.",
			},
			{
				type: "input",
				name: "Disabled Extensions",
				setting: "filesDisabledExtensions",
				keyboardType: "default",
				joinString: ", ",
				description: "Extensions to disable, separated by commas.",
				skeletonWidth: "70%",
			},
			{
				type: "input",
				name: "Max File Size",
				setting: "filesMaxFileSize",
				keyboardType: "default",
				displayType: "bytes",
				description: "The maximum file size allowed.",
				skeletonWidth: 40,
			},
			{
				type: "input",
				name: "Default Expiration",
				setting: "filesDefaultExpiration",
				keyboardType: "default",
				description: "The default expiration time for files.",
				skeletonWidth: 50,
			},
			{
				type: "input",
				name: "Default Date Format",
				setting: "filesDefaultDateFormat",
				keyboardType: "default",
				description: "The default date format to use.",
				skeletonWidth: "40%",
			},
			{
				type: "input",
				name: "Random Words Num Adjectives",
				setting: "filesRandomWordsNumAdjectives",
				keyboardType: "numeric",
				description:
					"The number of adjectives to use for the random-words/gfycat format.",
				skeletonWidth: 30,
			},
			{
				type: "input",
				name: "Random Words Separator",
				setting: "filesRandomWordsSeparator",
				placeholder: "-",
				description: "The separator to use for the random-words/gfycat format.",
				skeletonWidth: "20%",
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
				description:
					"The route to use for short URLs. Requires a server restart.",
				skeletonWidth: 40,
			},
			{
				type: "input",
				name: "Length",
				setting: "urlsLength",
				keyboardType: "numeric",
				description:
					"The length of the short URL (for randomly generated names).",
				skeletonWidth: 30,
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
				description: "Enable the use of invite links to register new users.",
			},
			{
				type: "input",
				name: "Length",
				setting: "invitesLength",
				keyboardType: "numeric",
				description: "The length of the invite code.",
				skeletonWidth: 30,
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
		description: "All options require a restart to take effect.",
		category: "ratelimitCategory",
		children: [
			{
				type: "switch",
				name: "Enable Ratelimit",
				setting: "ratelimitEnabled",
				description: "Enable ratelimiting for the server.",
			},
			{
				type: "switch",
				name: "Admin Bypass",
				setting: "ratelimitAdminBypass",
				description: "Allow admins to bypass the ratelimit.",
			},
			{
				type: "input",
				name: "Max Requests",
				setting: "ratelimitMax",
				keyboardType: "numeric",
				description:
					"The maximum number of requests allowed within the window. If no window is set, this is the maximum number of requests until it reaches the limit.",
				skeletonWidth: 40,
			},
			{
				type: "input",
				name: "Window",
				setting: "ratelimitWindow",
				keyboardType: "numeric",
				description: "The window in seconds to allow the max requests.",
				skeletonWidth: 50,
			},
			{
				type: "input",
				name: "Allow List",
				setting: "ratelimitAllowList",
				joinString: ", ",
				keyboardType: "default",
				description:
					"A comma-separated list of IP addresses to bypass the ratelimit.",
				skeletonWidth: "70%",
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
				description: "The title of the website in browser tabs and at the top.",
				skeletonWidth: "40%",
			},
			{
				type: "input",
				name: "Title Logo",
				setting: "websiteTitleLogo",
				keyboardType: "url",
				description:
					"The URL to use for the title logo. This is placed to the left of the title.",
				skeletonWidth: "60%",
			},
			{
				type: "externalUrls",
			},
			{
				type: "input",
				name: "Login Background",
				setting: "websiteLoginBackground",
				keyboardType: "url",
				description: "The URL to use for the login background.",
				skeletonWidth: "70%",
			},
			{
				type: "switch",
				name: "Login Background Blur",
				setting: "websiteLoginBackgroundBlur",
				description: "Whether to blur the login background.",
			},
			{
				type: "input",
				name: "Default Avatar",
				setting: "websiteDefaultAvatar",
				keyboardType: "default",
				description:
					"The path to use for the default avatar. This must be a path to an image, not a URL.",
				skeletonWidth: "55%",
			},
			{
				type: "input",
				name: "Terms of Service",
				setting: "websiteTos",
				keyboardType: "default",
				description:
					"Path to a Markdown (.md) file to use for the terms of service.",
				skeletonWidth: "40%",
			},
			{
				type: "input",
				name: "Default Theme",
				setting: "websiteThemeDefault",
				keyboardType: "default",
				description: "The default theme to use for the website.",
				skeletonWidth: "35%",
			},
			{
				type: "input",
				name: "Dark Theme",
				setting: "websiteThemeDark",
				keyboardType: "default",
				description:
					'The dark theme to use for the website when the default theme is "system".',
				skeletonWidth: "40%",
			},
			{
				type: "input",
				name: "Light Theme",
				setting: "websiteThemeLight",
				keyboardType: "default",
				description:
					'The light theme to use for the website when the default theme is "system".',
				skeletonWidth: "35%",
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
		description:
			'For OAuth to work, the "OAuth Registration" setting must be enabled in the Features section. If you have issues, try restarting Zipline after saving.',
		category: "oauthCategory",
		children: [
			{
				type: "switch",
				name: "Bypass Local Login",
				setting: "oauthBypassLocalLogin",
				description:
					"Skips the local login page and redirects to the OAuth provider, this only works with one provider enabled.",
			},
			{
				type: "switch",
				name: "Login Only",
				setting: "oauthLoginOnly",
				description:
					"Disables registration and only allows login with OAuth, existing users can link providers for example.",
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
						skeletonWidth: "40%",
					},
					{
						type: "input",
						name: "Discord Client Secret",
						setting: "oauthDiscordClientSecret",
						keyboardType: "default",
						passwordInput: true,
						skeletonWidth: "80%",
					},
					{
						type: "input",
						name: "Discord Redirect URI",
						setting: "oauthDiscordRedirectUri",
						keyboardType: "default",
						description:
							"The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
						skeletonWidth: "70%",
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
						skeletonWidth: "40%",
					},
					{
						type: "input",
						name: "Google Client Secret",
						setting: "oauthGoogleClientSecret",
						keyboardType: "default",
						passwordInput: true,
						skeletonWidth: "80%",
					},
					{
						type: "input",
						name: "Google Redirect URI",
						setting: "oauthGoogleRedirectUri",
						keyboardType: "default",
						description:
							"The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
						skeletonWidth: "70%",
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
						skeletonWidth: "40%",
					},
					{
						type: "input",
						name: "GitHub Client Secret",
						setting: "oauthGithubClientSecret",
						keyboardType: "default",
						passwordInput: true,
						skeletonWidth: "80%",
					},
					{
						type: "input",
						name: "GitHub Redirect URI",
						setting: "oauthGithubRedirectUri",
						keyboardType: "default",
						description:
							"The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
						skeletonWidth: "70%",
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
						skeletonWidth: "40%",
					},
					{
						type: "input",
						name: "OIDC Client Secret",
						setting: "oauthOidcClientSecret",
						keyboardType: "default",
						passwordInput: true,
						skeletonWidth: "80%",
					},
					{
						type: "input",
						name: "OIDC Authorize URL",
						setting: "oauthOidcAuthorizeUrl",
						keyboardType: "default",
						skeletonWidth: "70%",
					},
					{
						type: "input",
						name: "OIDC Token URL",
						setting: "oauthOidcTokenUrl",
						keyboardType: "default",
						skeletonWidth: "65%",
					},
					{
						type: "input",
						name: "OIDC Userinfo URL",
						setting: "oauthOidcUserinfoUrl",
						keyboardType: "default",
						skeletonWidth: "75%",
					},
					{
						type: "input",
						name: "OIDC Redirect URL",
						setting: "oauthOidcRedirectUri",
						keyboardType: "default",
						description:
							"The redirect URL to use instead of the host when logging in. This is not required if the URL generated by Zipline works as intended.",
						skeletonWidth: "70%",
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
		description: "Refresh the page after enabling PWA to see any changes.",
		category: "pwaCategory",
		children: [
			{
				type: "switch",
				name: "PWA Enabled",
				setting: "pwaEnabled",
				description: "Allow users to install the Zipline PWA on their devices.",
			},
			{
				type: "input",
				name: "Title",
				setting: "pwaTitle",
				keyboardType: "default",
				placeholder: "Zipline",
				description: "The title for the PWA",
				skeletonWidth: "40%",
			},
			{
				type: "input",
				name: "Short Name",
				setting: "pwaShortName",
				keyboardType: "default",
				placeholder: "Zipline",
				description: "The short name for the PWA",
				skeletonWidth: "30%",
			},
			{
				type: "input",
				name: "Description",
				setting: "pwaDescription",
				keyboardType: "default",
				placeholder: "Zipline",
				description: "The description for the PWA",
				skeletonWidth: "60%",
			},
			{
				type: "input",
				name: "Theme Color",
				setting: "pwaThemeColor",
				keyboardType: "default",
				placeholder: "#000000",
				description: "The theme color for the PWA",
				skeletonWidth: 70,
			},
			{
				type: "colorPicker",
				name: "Background Color",
				setting: "pwaBackgroundColor",
				description: "The background color for the PWA",
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
				description:
					"The URL to send a POST request to when a file is uploaded.",
				skeletonWidth: "80%",
			},
			{
				type: "input",
				name: "On Shorten",
				setting: "httpWebhookOnShorten",
				keyboardType: "url",
				placeholder: "https://example.com/shorten",
				description:
					"The URL to send a POST request to when a URL is shortened.",
				skeletonWidth: "80%",
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
				description: "The Discord webhook URL to send notifications to",
				skeletonWidth: "80%",
			},
			{
				type: "input",
				name: "Username",
				setting: "discordUsername",
				keyboardType: "default",
				placeholder: "Zipline",
				description: "The username to send notifications as",
				skeletonWidth: "30%",
			},
			{
				type: "input",
				name: "Avatar URL",
				setting: "discordAvatarUrl",
				keyboardType: "url",
				placeholder: "https://example.com/avatar.png",
				description: "The avatar for the webhook",
				skeletonWidth: "60%",
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
						description:
							"The Discord webhook URL to send notifications to. If this is left blank, the main webhook url will be used",
						skeletonWidth: "80%",
					},
					{
						type: "input",
						name: "Username",
						setting: "discordOnUploadUsername",
						keyboardType: "default",
						placeholder: "Zipline Uploads",
						description:
							"The username to send notifications as. If this is left blank, the main username will be used",
						skeletonWidth: "30%",
					},
					{
						type: "input",
						name: "Avatar URL",
						setting: "discordOnUploadAvatarUrl",
						keyboardType: "url",
						placeholder: "https://example.com/uploadAvatar.png",
						description:
							"The avatar for the webhook. If this is left blank, the main avatar will be used",
						skeletonWidth: "60%",
					},
					{
						type: "input",
						name: "Content",
						setting: "discordOnUploadContent",
						keyboardType: "default",
						multiline: true,
						description:
							"The content of the notification. This can be blank, but at least one of the content or embed fields must be filled out",
						skeletonWidth: "55%",
					},
					{
						type: "switch",
						name: "Embed",
						setting: "discordOnUploadEmbed",
						setType: "upload",
						description:
							"Send the notification as an embed. This will allow for more customization below.",
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
								description: "The title of the embed",
								skeletonWidth: "25%",
							},
							{
								type: "input",
								name: "Description",
								setting: "discordOnUploadEmbed.description",
								keyboardType: "default",
								description: "The description of the embed",
								skeletonWidth: "60%",
							},
							{
								type: "input",
								name: "Footer",
								setting: "discordOnUploadEmbed.footer",
								keyboardType: "default",
								description: "The footer of the embed",
								skeletonWidth: "45%",
							},
							{
								type: "colorPicker",
								name: "Color",
								setting: "discordOnUploadEmbed.color",
								description: "The color of the embed",
							},
							{
								type: "switch",
								name: "Thumbnail",
								setting: "discordOnUploadEmbed.thumbnail",
								description:
									"Show the thumbnail (it will show the file if it's an image) in the embed",
							},
							{
								type: "switch",
								name: "Image/Video",
								setting: "discordOnUploadEmbed.imageOrVideo",
								description: "Show the image or video in the embed",
							},
							{
								type: "switch",
								name: "Timestamp",
								setting: "discordOnUploadEmbed.timestamp",
								description: "Show the timestamp in the embed",
							},
							{
								type: "switch",
								name: "URL",
								setting: "discordOnUploadEmbed.url",
								description:
									"Makes the title clickable and links to the URL of the file",
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
						description:
							"The Discord webhook URL to send notifications to. If this is left blank, the main webhook url will be used",
						skeletonWidth: "80%",
					},
					{
						type: "input",
						name: "Username",
						setting: "discordOnShortenUsername",
						keyboardType: "default",
						placeholder: "Zipline Shortens",
						description:
							"The username to send notifications as. If this is left blank, the main username will be used",
						skeletonWidth: "30%",
					},
					{
						type: "input",
						name: "Avatar URL",
						setting: "discordOnShortenAvatarUrl",
						keyboardType: "url",
						placeholder: "https://example.com/shortenAvatar.png",
						description:
							"The avatar for the webhook. If this is left blank, the main avatar will be used",
						skeletonWidth: "60%",
					},
					{
						type: "input",
						name: "Content",
						setting: "discordOnShortenContent",
						keyboardType: "default",
						multiline: true,
						description:
							"The content of the notification. This can be blank, but at least one of the content or embed fields must be filled out",
						skeletonWidth: "35%",
					},
					{
						type: "switch",
						name: "Embed",
						setting: "discordOnShortenEmbed",
						description:
							"Send the notification as an embed. This will allow for more customization below.",
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
								description: "The title of the embed",
								skeletonWidth: "30%",
							},
							{
								type: "input",
								name: "Description",
								setting: "discordOnShortenEmbed.description",
								keyboardType: "default",
								description: "The description of the embed",
								skeletonWidth: "60%",
							},
							{
								type: "input",
								name: "Footer",
								setting: "discordOnShortenEmbed.footer",
								keyboardType: "default",
								description: "The footer of the embed",
								skeletonWidth: "45%",
							},
							{
								type: "colorPicker",
								name: "Color",
								setting: "discordOnShortenEmbed.color",
								description: "The color of the embed",
							},
							{
								type: "switch",
								name: "Timestamp",
								setting: "discordOnShortenEmbed.timestamp",
								description: "Show the timestamp in the embed",
							},
							{
								type: "switch",
								name: "URL",
								setting: "discordOnShortenEmbed.url",
								description:
									"Makes the title clickable and links to the URL of the file",
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
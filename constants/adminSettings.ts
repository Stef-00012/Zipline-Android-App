import type { SelectProps } from "@/components/Select";
import type { APISettings, ShortenEmbed, UploadEmbed } from "@/types/zipline";

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

export type SettingPath<T extends keyof APISettings = keyof APISettings> = T extends 'discordOnUploadEmbed'
	? T | `${T}.${keyof UploadEmbed}`
	: T extends "discordOnShortenEmbed"
		? T | `${T}.${keyof ShortenEmbed}`
		: T;

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
	"discordOnShortenEmbed.imageOrVideo": "Image/Video",
	"discordOnShortenEmbed.thumbnail": "Thumbnail",
	"discordOnShortenEmbed.timestamp": "Timestamp",
	"discordOnShortenEmbed.title": "Title",
	"discordOnShortenEmbed.url": "URL",
};

export const formats: SelectProps["data"] = [
	{ label: "Random", value: "random" },
	{ label: "Date", value: "date" },
	{ label: "UUID", value: "uuid" },
	{ label: "Use File Name", value: "name" },
	{ label: "Gfycat-style Name", value: "gfycat" },
];
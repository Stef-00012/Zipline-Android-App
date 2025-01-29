import type { SelectProps } from "@/components/Select";
import type { APISettings, ShortenEmbed, UploadEmbed } from "@/types/zipline";
import bytes from "bytes";
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

type SettingPath<T extends keyof APISettings = keyof APISettings> = T extends 'discordOnUploadEmbed'
	? T | `${T}.${keyof UploadEmbed}`
	: T extends "discordOnShortenEmbed"
		? T | `${T}.${keyof ShortenEmbed}`
		: T;

export type SettingTypes = APISettings[keyof APISettings] | UploadEmbed[keyof UploadEmbed] | ShortenEmbed[keyof ShortenEmbed]

interface CategorySetting {
	type: "category";
	name: string,
	children: Array<Setting>;
}

interface InputSetting {
	type: "input",
	name: string,
	keyboardType: KeyboardTypeOptions;
	setting: SettingPath
	multiline?: boolean;
	placeholder?: string
	joinString?: string
	setType?: "number" | "array"
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
	setType?: "upload" | "shorten"
}

interface SaveSetting {
	type: "save",
	category: SaveCategory
}

export type SaveCategory = "core" | "chunks" | "tasks" | "mfa" | "features" | "files" | "urlShortener" | "invites" | "ratelimit" | "website" | "oauth" | "pwa" | "httpWebhooks" | "discordWebhook" | "discordOnUploadWebhook" | "discordOnShortenWebhook";
export type Setting = CategorySetting | InputSetting | SelectSetting | SwitchSetting | SaveSetting

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
				setting: "chunksMax",
				placeholder: "95mb"
			},
			{
				type: "input",
				name: "Chunks Size",
				keyboardType: "numeric",
				setting: "chunksSize",
				placeholder: "25mb"
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
			// setType: "ms",
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
			{
				type: "switch",
				name: "Image Compression",
				setting: "featuresImageCompression"
			},
			{
				type: "switch",
				name: "/robots.txt",
				setting: "featuresRobotsTxt"
			},
			{
				type: "switch",
				name: "Healthcheck",
				setting: "featuresHealthcheck"
			},
			{
				type: "switch",
				name: "User Registration",
				setting: "featuresUserRegistration"
			},
			{
				type: "switch",
				name: "OAuth Registration",
				setting: "featuresOauthRegistration"
			},
			{
				type: "switch",
				name: "Delete on Max Views",
				setting: "featuresDeleteOnMaxViews"
			},
			{
				type: "switch",
				name: "Enable Metrics",
				setting: "featuresMetricsEnabled"
			},
			{
				type: "switch",
				name: "Admin Only Metrics",
				setting: "featuresMetricsAdminOnly"
			},
			{
				type: "switch",
				name: "Show User Specific Metrics",
				setting: "featuresMetricsShowUserSpecific"
			},
			{
				type: "switch",
				name: "Enable Thumbnails",
				setting: "featuresThumbnailsEnabled"
			},
			{
				type: "input",
				name: "Thumbnails Number Threads",
				setting: "featuresThumbnailsNumberThreads",
				keyboardType: "numeric",
				setType: "number",
				placeholder: "Enter a number..."
			},
			{
				type: "save",
				category: "features"
			}
		]
	},
	{
		type: "category",
		name: "Files",
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
				keyboardType: "numeric"
			},
			{
				type: "switch",
				name: "Assume Mimetypes",
				setting: "filesAssumeMimetypes"
			},
			{
				type: "switch",
				name: "Remove GPS Metadata",
				setting: "filesRemoveGpsMetadata"
			},
			{
				type: "select",
				options: formats,
				name: "Default Format",
				defaultValue: "random",
				setting: "filesDefaultFormat"
			},
			{
				type: "input",
				name: "Disabled Extensions",
				setting: "filesDisabledExtensions",
				keyboardType: "default",
				setType: "array",
				joinString: ", "
			},
			{
				type: "input",
				name: "Max File Size",
				setting: "filesMaxFileSize",
				keyboardType: "default"
			},
			{
				type: "input",
				name: "Default Expiration",
				setting: "filesDefaultExpiration",
				keyboardType: "default"
			},
			{
				type: "input",
				name: "Default Date Format",
				setting: "filesDefaultDateFormat",
				keyboardType: "default",
			},
			{
				type: "save",
				category: "files"
			}
		]
	},
	{
		type: "category",
		name: "URL Shortener",
		children: [
			{
				type: "input",
				name: "Route",
				setting: "urlsRoute",
				keyboardType: "default"
			},
			{
				type: "input",
				name: "Length",
				setting: "urlsLength",
				setType: "number",
				keyboardType: "numeric"
			},
		]
	},
	{
		type: "category",
		name: "Invites",
		children: [
			{
				type: "switch",
				name: "Enable Invites",
				setting: "invitesEnabled"
			},
			{
				type: "input",
				name: "Length",
				setting: "invitesLength",
				setType: "number",
				keyboardType: "numeric"
			},
		]
	},
	{
		type: "category",
		name: "Ratelimit",
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
				setType: "number",
				keyboardType: "numeric"
			},
			{
				type: "input",
				name: "Window",
				setting: "ratelimitWindow",
				setType: "number",
				keyboardType: "numeric"
			},
			{
				type: "input",
				name: "Allow List",
				setting: "ratelimitAllowList",
				setType: "array",
				joinString: ", ",
				keyboardType: "default"
			},
			{
				type: "save",
				category: "ratelimit"
			}
		]
	},
	{
		type: "category",
		name: "Website",
		children: [
			{
				type: "input",
				name: "Title",
				setting: "websiteTitle",
				keyboardType: "default"
			},
			{
				type: "input",
				name: "Title Logo",
				setting: "websiteTitleLogo",
				keyboardType: "url"
			},
			{
				type: "input",
				name: "External Links",
				setting: "websiteExternalLinks",
				keyboardType: "default",
				multiline: true,
			},
			{
				type: "input",
				name: "Login Backgorund",
				setting: "websiteLoginBackground",
				keyboardType: "url"
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
				keyboardType: "default"
			},
			{
				type: "input",
				name: "Terms of Service",
				setting: "websiteTos",
				keyboardType: "default"
			},
			{
				type: "input",
				name: "Default Theme",
				setting: "websiteThemeDefault",
				keyboardType: "default"
			},
			{
				type: "input",
				name: "Dark Theme",
				setting: "websiteThemeDark",
				keyboardType: "default"
			},
			{
				type: "input",
				name: "Light Theme",
				setting: "websiteThemeLight",
				keyboardType: "default"
			},
			{
				type: "save",
				category: "website"
			}
		]
	},
	{
		type: "category",
		name: "OAuth",
		children: [
			{
				type: "switch",
				name: "Bypass Local Login",
				setting: "oauthBypassLocalLogin"
			},
			{
				type: "switch",
				name: "Login Only",
				setting: "oauthLoginOnly"
			},
			{
				type: "category",
				name: "Discord",
				children: [
					{
						type: "input",
						name: "Discord Client ID",
						setting: "oauthDiscordClientId",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "Discord Client Secret",
						setting: "oauthDiscordClientSecret",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "Discord Redirect URI",
						setting: "oauthDiscordRedirectUri",
						keyboardType: "default"
					}
				]
			},
			{
				type: "category",
				name: "Google",
				children: [
					{
						type: "input",
						name: "Google Client ID",
						setting: "oauthGoogleClientId",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "Google Client Secret",
						setting: "oauthGoogleClientSecret",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "Google Redirect URI",
						setting: "oauthGoogleRedirectUri",
						keyboardType: "default"
					}
				]
			},
			{
				type: "category",
				name: "GitHub",
				children: [
					{
						type: "input",
						name: "GitHub Client ID",
						setting: "oauthGithubClientId",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "GitHub Client Secret",
						setting: "oauthGithubClientSecret",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "GitHub Redirect URI",
						setting: "oauthGithubRedirectUri",
						keyboardType: "default"
					}
				]
			},
			{
				type: "category",
				name: "OpenID Connect",
				children: [
					{
						type: "input",
						name: "OIDC Client ID",
						setting: "oauthOidcClientId",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "OIDC Client Secret",
						setting: "oauthOidcClientSecret",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "OIDC Authorize URL",
						setting: "oauthOidcAuthorizeUrl",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "OIDC Token URL",
						setting: "oauthOidcTokenUrl",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "OIDC Userinfo URL",
						setting: "oauthOidcUserinfoUrl",
						keyboardType: "default"
					},
					{
						type: "input",
						name: "OIDC Redirect URL",
						setting: "oauthOidcRedirectUri",
						keyboardType: "default"
					}
				]
			},
			{
				type: "save",
				category: "oauth"
			}
		]
	},
	{
		type: "category",
		name: "PWA",
		children: [
			{
				type: "switch",
				name: "PWA Enabled",
				setting: "pwaEnabled"
			},
			{
				type: "input",
				name: "Title",
				setting: "pwaTitle",
				keyboardType: "default",
				placeholder: "Zipline"
			},
			{
				type: "input",
				name: "Short Name",
				setting: "pwaShortName",
				keyboardType: "default",
				placeholder: "Zipline"
			},
			{
				type: "input",
				name: "Description",
				setting: "pwaDescription",
				keyboardType: "default",
				placeholder: "Zipline"
			},
			{
				type: "input",
				name: "Theme Color",
				setting: "pwaThemeColor",
				keyboardType: "default",
				placeholder: "#000000"
			},
			{
				type: "input",
				name: "Background Color",
				setting: "pwaBackgroundColor",
				keyboardType: "default",
				placeholder: "#000000"
			},
			{
				type: "save",
				category: "pwa"
			}
		]
	},
	{
		type: "category",
		name: "HTTP Webhooks",
		children: [
			{
				type: "input",
				name: "On Upload",
				setting: "httpWebhookOnUpload",
				keyboardType: "url",
				placeholder: "https://example.com/upload"
			},
			{
				type: "input",
				name: "On Shorten",
				setting: "httpWebhookOnShorten",
				keyboardType: "url",
				placeholder: "https://example.com/shorten"
			},
			{
				type: "save",
				category: "httpWebhooks"
			}
		]
	},
	{
		type: "category",
		name: "Discord Webhook",
		children: [
			{
				type: "input",
				name: "Webhook URL",
				setting: "discordWebhookUrl",
				keyboardType: "url",
				placeholder: "https://discord.com/api/webhooks/..."
			},
			{
				type: "input",
				name: "Username",
				setting: "discordUsername",
				keyboardType: "default",
				placeholder: "Zipline"
			},
			{
				type: "input",
				name: "Avatar URL",
				setting: "discordAvatarUrl",
				keyboardType: "url",
				placeholder: "https://example.com/avatar.png"
			},
			{
				type: "save",
				category: "discordWebhook"
			},
			{
				type: "category",
				name: "On Upload",
				children: [
					{
						type: "input",
						name: "Webhook URL",
						setting: "discordOnUploadWebhookUrl",
						keyboardType: "url",
						placeholder: "https://discord.com/api/webhooks/..."
					},
					{
						type: "input",
						name: "Username",
						setting: "discordOnUploadUsername",
						keyboardType: "default",
						placeholder: "Zipline Uploads"
					},
					{
						type: "input",
						name: "Avatar URL",
						setting: "discordOnUploadAvatarUrl",
						keyboardType: "url",
						placeholder: "https://example.com/uploadAvatar.png"
					},
					{
						type: "input",
						name: "Content",
						setting: "discordOnUploadContent",
						keyboardType: "default",
						multiline: true
					},
					{
						type: "switch",
						name: "Embed",
						setting: "discordOnUploadEmbed",
						setType: "upload"
					},
					{
						type: "category",
						name: "Embed Settings",
						children: [
							{
								type: "input",
								name: "Title",
								setting: "discordOnUploadEmbed.title",
								keyboardType: "default"
							},
							{
								type: "input",
								name: "Description",
								setting: "discordOnUploadEmbed.description",
								keyboardType: "default"
							},
							{
								type: "input",
								name: "Footer",
								setting: "discordOnUploadEmbed.footer",
								keyboardType: "default"
							},
							{
								type: "input",
								name: "Color",
								setting: "discordOnUploadEmbed.color",
								keyboardType: "default"
							},
							{
								type: "switch",
								name: "Thumbnail",
								setting: "discordOnUploadEmbed.thumbnail"
							},
							{
								type: "switch",
								name: "Image/Video",
								setting: "discordOnUploadEmbed.imageOrVideo"
							},
							{
								type: "switch",
								name: "Timestamp",
								setting: "discordOnUploadEmbed.timestamp"
							},
							{
								type: "switch",
								name: "URL",
								setting: "discordOnUploadEmbed.url"
							}
						]
					},
					{
						type: "save",
						category: "discordOnUploadWebhook"
					}
				]
			},
			{
				type: "category",
				name: "On Shorten",
				children: [
					{
						type: "input",
						name: "Webhook URL",
						setting: "discordOnShortenWebhookUrl",
						keyboardType: "url",
						placeholder: "https://discord.com/api/webhooks/..."
					},
					{
						type: "input",
						name: "Username",
						setting: "discordOnShortenUsername",
						keyboardType: "default",
						placeholder: "Zipline Shortens"
					},
					{
						type: "input",
						name: "Avatar URL",
						setting: "discordOnShortenAvatarUrl",
						keyboardType: "url",
						placeholder: "https://example.com/shortenAvatar.png"
					},
					{
						type: "input",
						name: "Content",
						setting: "discordOnShortenContent",
						keyboardType: "default",
						multiline: true
					},
					{
						type: "switch",
						name: "Embed",
						setting: "discordOnShortenEmbed"
					},
					{
						type: "category",
						name: "Embed Settings",
						children: [
							{
								type: "input",
								name: "Title",
								setting: "discordOnShortenEmbed.title",
								keyboardType: "default"
							},
							{
								type: "input",
								name: "Description",
								setting: "discordOnShortenEmbed.description",
								keyboardType: "default"
							},
							{
								type: "input",
								name: "Footer",
								setting: "discordOnShortenEmbed.footer",
								keyboardType: "default"
							},
							{
								type: "input",
								name: "Color",
								setting: "discordOnShortenEmbed.color",
								keyboardType: "default"
							},
							{
								type: "switch",
								name: "Thumbnail",
								setting: "discordOnShortenEmbed.thumbnail"
							},
							{
								type: "switch",
								name: "Image/Video",
								setting: "discordOnShortenEmbed.imageOrVideo"
							},
							{
								type: "switch",
								name: "Timestamp",
								setting: "discordOnShortenEmbed.timestamp"
							},
							{
								type: "switch",
								name: "URL",
								setting: "discordOnShortenEmbed.url"
							}
						]
					},
					{
						type: "save",
						category: "discordOnShortenWebhook"
					}
				]
			}
		]
	},
]
import type { UploadFileOptions } from "@/functions/zipline/files";
import type { Mimetypes } from "@/types/mimetypes";

export type DashURL = `http${"s" | ""}://${string}.${string}`;

export type APISelfUser = Omit<APIUser, "avatar">;

export interface APILoginSuccessResponse {
	user: APIUser;
	totp?: undefined;
}

export interface APILoginTotpResponse {
	totp: true;
	user?: undefined;
}

export type APILoginResponse = APILoginSuccessResponse | APILoginTotpResponse;

export interface APITokenResponse {
	token: string;
}

export interface APIUser {
	id: string;
	username: string;
	createdAt: string;
	updatedAt: string;
	role: "SUPERADMIN" | "ADMIN" | "USER";
	view: APIUserView;
	oauthProviders: OAuthProvider[];
	totpSecret: string | null;
	passkeys: Passkey[];
	quota: APIUserQuota | null;
	sessions: string[];
	avatar: string | null;
}

export interface APIUserQuota {
	id: string;
	createdAt: string;
	updatedAt: string;
	filesQuota: "BY_BYTES" | "BY_FILES";
	maxBytes: string | null;
	maxFiles: number | null;
	maxUrls: number | null;
	userId: "cm5ohf5r90003pl01r1vn6yus";
}

export interface APIUserView {
	enabled?: boolean;
	align?: "left" | "center" | "right";
	showMimetype?: boolean;
	content?: string;
	embed?: boolean;
	embedTitle?: string;
	embedDescription?: string;
	embedColor?: string;
	embedSiteName?: string;
}

export interface OAuthProvider {
	id: string;
	createdAt: string;
	updatedAt: string;
	userId: string;
	provider: string;
	username: string;
	accessToken: string;
	refreshToken: string;
	oauthId: string;
}

export interface Passkey {
	id: string;
	createdAt: string;
	updatedAt: string;
	lastUsed: string;
	name: string;
	reg: PasskeyReg;
	userId: string;
}

export interface PasskeyReg {
	id: string;
	type: string;
	rawId: string;
	response: PasskeyRegResponse;
	clientExtensionResults: PasskeyRegClientExtensionResults;
	authenticatorAttachment: string;
}

export interface PasskeyRegResponse {
	transports: string[];
	clientDataJSON: string;
	attestationObject: string;
}

export interface PasskeyRegClientExtensionResults {
	credProps: unknown | null;
}

export type APIRecentFiles = APIFile[];

export interface APIFile {
	createdAt: string;
	updatedAt: string;
	deletesAt: string | null;
	favorite: boolean;
	id: string;
	originalName: string | null;
	name: string;
	size: number;
	type: string;
	views: number;
	maxViews: number | null;
	folderId: string | null;
	thumbnail: string | null;
	tags: APITag[];
	password: boolean;
	url: string;
}

export interface APIUserStats {
	filesUploaded: number;
	favoriteFiles: number;
	views: number;
	avgViews: number;
	storageUsed: number;
	avgStorageUsed: number;
	urlsCreated: number;
	urlViews: number;
	sortTypeCount: {
		[key: string]: number;
	};
}

export interface APIUploadFile {
	id: APIFile["id"];
	type: keyof Mimetypes;
	url: string;
	pending: boolean;
}

export interface APIUploadResponse {
	files: APIUploadFile[];
	partialSuccess?: boolean;
	assumedMimetypes: boolean[];
	deletesAt?: string;
}

export type APIUploadPartialResponse = APIUploadResponse & {
	partialSuccess?: boolean;
	partialIdentifier?: string;
};

export interface APIShortenResponse {
	id: string;
	createdAt: string;
	updatedAt: string;
	code: string;
	vanity: string | null;
	destination: string;
	views: number;
	maxViews: number | null;
	enabled: true;
	userId: string;
	url: string;
}

export interface APITransactionResult {
	count: number;
	name?: string;
}

export interface APISettings {
	settings: {
		coreReturnHttpsUrls: boolean;
		coreDefaultDomain: string | null;
		coreTempDirectory: string;
		chunksEnabled: boolean;
		chunksMax: number | string;
		chunksSize: number | string;
		tasksDeleteInterval: number | string;
		tasksClearInvitesInterval: number | string;
		tasksMaxViewsInterval: number | string;
		tasksThumbnailsInterval: number | string;
		tasksMetricsInterval: number | string;
		filesRoute: string;
		filesLength: number;
		filesDefaultFormat: "random" | "uuid" | "date" | "name" | "gfycat";
		filesDisabledExtensions: string[]; // ???
		filesMaxFileSize: number | string;
		filesDefaultExpiration: string | null;
		filesAssumeMimetypes: boolean;
		filesDefaultDateFormat: string;
		filesRemoveGpsMetadata: boolean;
		filesRandomWordsNumAdjectives: number;
		filesRandomWordsSeparator: string;
		urlsRoute: string;
		urlsLength: number;
		featuresImageCompression: boolean;
		featuresRobotsTxt: boolean;
		featuresHealthcheck: boolean;
		featuresUserRegistration: boolean;
		featuresOauthRegistration: boolean;
		featuresDeleteOnMaxViews: boolean;
		featuresThumbnailsEnabled: boolean;
		featuresThumbnailsNumberThreads: number;
		featuresMetricsEnabled: boolean;
		featuresMetricsAdminOnly: boolean;
		featuresMetricsShowUserSpecific: boolean;
		featuresVersionChecking: boolean;
		featuresVersionAPI: string;
		invitesEnabled: boolean;
		invitesLength: number;
		websiteTitle: string;
		websiteTitleLogo: string | null;
		websiteExternalLinks: ExternalLink[];
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
		oauthDiscordAllowedIds: string[]; // +++
		oauthDiscordDeniedIds: string[]; // +++
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
		mfaTotpEnabled: boolean;
		mfaTotpIssuer: string;
		mfaPasskeys: boolean;
		ratelimitEnabled: boolean;
		ratelimitMax: number;
		ratelimitWindow: number | null;
		ratelimitAdminBypass: boolean;
		ratelimitAllowList: string[];
		httpWebhookOnUpload: string | null;
		httpWebhookOnShorten: string | null;
		discordWebhookUrl: string | null;
		discordUsername: string | null;
		discordAvatarUrl: string | null;
		discordOnUploadWebhookUrl: string | null;
		discordOnUploadUsername: string | null;
		discordOnUploadAvatarUrl: string | null;
		discordOnUploadContent: string | null;
		discordOnUploadEmbed: UploadEmbed | null; // check embed
		discordOnShortenWebhookUrl: string | null;
		discordOnShortenUsername: string | null;
		discordOnShortenAvatarUrl: string | null;
		discordOnShortenContent: string | null;
		discordOnShortenEmbed: ShortenEmbed | null; // check embed
		pwaEnabled: boolean;
		pwaTitle: string;
		pwaShortName: string;
		pwaDescription: string;
		pwaThemeColor: string;
		pwaBackgroundColor: string;
		domains: string[]; // +++
	};
	tampered: (keyof APISettings["settings"])[];
}

export interface ExternalLink {
	url: string;
	name: string;
}

export interface UploadEmbed {
	url: boolean;
	color: string | null;
	title: string | null;
	footer: string | null;
	thumbnail: boolean;
	timestamp: boolean;
	description: string | null;
	imageOrVideo: boolean;
}

export type ShortenEmbed = Omit<UploadEmbed, "imageOrVideo" | "thumbnail">;

export type APIUserNoIncl = Omit<
	APIUser,
	"view" | "oauthProviders" | "totpSecret" | "passkeys" | "sessions"
> & {
	passkeys: [];
	totpSecret: null;
	view: Record<string, never>;
	oauthProviders: [];
	sessions: [];
};

export type APIUsersNoIncl = APIUserNoIncl[];

export type APIUsers = APIUser[];

export interface APIURL {
	id: string;
	createdAt: string;
	updatedAt: string;
	code: string;
	vanity: string | null;
	destination: string;
	views: number;
	maxViews: number | null;
	password: string | null;
	enabled: boolean;
	userId: string;
}

export type APIURLs = APIURL[];

export interface APIFolder {
	id: string;
	createdAt: string;
	updatedAt: string;
	name: string;
	public: boolean;
	userId: string;
	files: APIFile[];
	allowUploads: boolean;
}

export type APIFolderNoIncl = Omit<APIFolder, "files">;

export type APIFolders = APIFolder[];

export type APIFoldersNoIncl = APIFolderNoIncl[];

export interface APITag {
	id: string;
	createdAt: string;
	updatedAt: string;
	name: string;
	color: string;
	files: TagFile[];
}

export type APITags = APITag[];

export interface TagFile {
	id: string;
}

export interface APIFiles {
	page: APIFile[];
	total: number;
	pages: number;
}

export interface APIStat {
	id: string;
	createdAt: string;
	updatedAt: string;
	data: APIStatData;
}

export type APIStats = APIStat[];

export interface APIStatData {
	users: number;
	files: number;
	fileViews: number;
	urls: number;
	urlViews: number;
	storage: number;
	filesUsers: APIStatUserFile[];
	urlsUsers: APIStatUserURL[];
	types: APIStatFileType[];
}

export interface APIStatUserFile {
	username: string;
	sum: number;
	storage: number;
	views: number;
}

export interface APIStatUserURL {
	username: string;
	sum: number;
	views: number;
}

export interface APIStatFileType {
	type: string;
	sum: number;
}

export interface APIInvite {
	id: string;
	createdAt: string;
	updatedAt: string;
	expiresAt: string | null;
	code: string;
	uses: number;
	maxUses: number;
	inviterId: string;
	inviter: APIInviteInvier;
}

export interface APIInviteInvier {
	username: string;
	id: string;
	role: APIUser["role"];
}

export type APIInvites = APIInvite[];

export interface APIExport {
	id: string;
	createdAt: string;
	updatedAt: string;
	completed: boolean;
	path: string;
	files: number;
	size: string;
	userId: string;
}

export type APIExports = APIExport[];

export interface ServerActionResponse {
	status: string;
}

export interface Preset {
	overrideDomain: UploadFileOptions["overrideDomain"];
	overrideFileName: UploadFileOptions["filename"];
	originalName: UploadFileOptions["originalName"];
	compression: UploadFileOptions["compression"];
	deletesAt: UploadFileOptions["expiresAt"];
	maxViews: UploadFileOptions["maxViews"];
	password: UploadFileOptions["password"];
	format: UploadFileOptions["format"];
	folder: UploadFileOptions["folder"];
	name: string;
}

export type APIVersion = OldV4APIVersion | NewV4APIVersion;

export interface OldV4APIVersion {
	version: string;
}

export interface NewV4APIVersion {
	data: APIVersionData;
	details: APIVersionDetails;
}

interface APIVersionDetails {
	version: string;
	sha: string;
}

interface APIVersionData {
	latest: APIVersionDataLatest;
	isUpstream: boolean;
	isRelease: boolean;
	isLatest: false;
	version: APIVersionDataVersion;
}

interface APIVersionDataLatest {
	tag: string;
	url: string;
	commit: APIVersionDataLatestCommit;
}

interface APIVersionDataLatestCommit {
	sha: string;
	url: string;
	pull: boolean;
}

interface APIVersionDataVersion {
	tag: string;
	sha: string;
	url: string;
}

export interface APIPublicSettings {
	oauth: {
		bypassLocalLogin: boolean;
		loginOnly: boolean;
	};
	oauthEnabled: {
		discord: boolean;
		github: boolean;
		google: boolean;
		oidc: boolean;
	};
	website: {
		loginBackground?: string | null;
		loginBackgroundBlur?: boolean;
		title?: string;
		tos: boolean;
	};
	features: {
		oauthRegistration: boolean;
		userRegistration: boolean;
	};
	mfa: {
		passkeys: boolean;
	};
	tos?: string | null;
	files: {
		maxFileSize: string;
		defaultFormat:
			| "uuid"
			| "date"
			| "random"
			| "name"
			| "gfycat"
			| "random-words";
	};
	chunks: {
		max?: string;
		size?: string;
		enabled?: boolean;
	};
	domains?: string[];
	firstSetup: boolean;
}

export interface APIWebSettings {
	config: {
		chunks: {
			max: string;
			size: string;
			enabled: boolean;
		};
		tasks: {
			deleteInterval: string;
			clearInvitesInterval: string;
			maxViewsInterval: string;
			thumbnailsInterval: string;
			metricsInterval: string;
		};
		files: {
			route: string;
			length: number;
			defaultFormat:
				| "uuid"
				| "date"
				| "random"
				| "name"
				| "gfycat"
				| "random-words";
			disabledExtensions: string[];
			maxFileSize: string;
			defaultExpiration: string | null;
			assumeMimetypes: boolean;
			defaultDateFormat: string;
			removeGpsMetadata: boolean;
			randomWordsNumAdjectives: number;
			randomWordsSeparator: string;
		};
		urls: {
			route: string;
			length: number;
		};
		features: {
			imageCompression: boolean;
			robotsTxt: boolean;
			healthcheck: boolean;
			userRegistration: boolean;
			oauthRegistration: boolean;
			deleteOnMaxViews: boolean;
			thumbnails: {
				enabled: boolean;
				num_threads: number;
			};
			metrics: {
				enabled: boolean;
				adminOnly: boolean;
				showUserSpecific: boolean;
			};
			versionChecking: boolean;
			versionAPI: string;
		};
		invites: {
			enabled: boolean;
			length: number;
		};
		website: {
			title: string;
			titleLogo: string | null;
			externalLinks: {
				name: string;
				url: string;
			}[];
			loginBackground: string | null;
			loginBackgroundBlur: boolean;
			defaultAvatar: string | null;
			theme: {
				default: string;
				dark: string;
				light: string;
			};
			tos: string | null;
		};
		mfa: {
			totp: {
				enabled: boolean;
				issuer: string;
			};
			passkeys: boolean;
		};
		pwa: {
			enabled: boolean;
			title: string;
			shortName: string;
			description: string;
			themeColor: string;
			backgroundColor: string;
		};
		oauthEnabled: {
			discord: boolean;
			github: boolean;
			google: boolean;
			oidc: boolean;
		};
		oauth: {
			bypassLocalLogin: boolean;
			loginOnly: boolean;
		};
		version: string;
	};
	codeMap: {
		ext: string;
		mime: string;
		name: string;
	}[];
}

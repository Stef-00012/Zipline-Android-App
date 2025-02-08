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
	oauthProviders: Array<OAuthProvider>;
	totpSecret: string | null;
	passkeys: Array<Passkey>;
	quota: APIUserQuota | null;
	sessions: Array<string>;
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
	transports: Array<string>;
	clientDataJSON: string;
	attestationObject: string;
}

export interface PasskeyRegClientExtensionResults {
	credProps: unknown | null;
}

export type APIRecentFiles = Array<APIFile>;

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
	tags: Array<APITag>;
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
	files: Array<APIUploadFile>;
	partialSuccess: boolean;
	assumedMimetypes: Array<string | null | APIUploadFile>;
}

export interface APISettings {
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
	filesDisabledExtensions: Array<string>;
	filesMaxFileSize: number | string;
	filesDefaultExpiration: string | null;
	filesAssumeMimetypes: boolean;
	filesDefaultDateFormat: string;
	filesRemoveGpsMetadata: boolean;
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
	invitesEnabled: boolean;
	invitesLength: number;
	websiteTitle: string;
	websiteTitleLogo: string | null;
	websiteExternalLinks: Array<ExternalLink>;
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
	mfaTotpEnabled: boolean;
	mfaTotpIssuer: string;
	mfaPasskeys: boolean;
	ratelimitEnabled: boolean;
	ratelimitMax: number;
	ratelimitWindow: number | null;
	ratelimitAdminBypass: boolean;
	ratelimitAllowList: Array<string>;
	httpWebhookOnUpload: string | null;
	httpWebhookOnShorten: string | null;
	discordWebhookUrl: string | null;
	discordUsername: string | null;
	discordAvatarUrl: string | null;
	discordOnUploadWebhookUrl: string | null;
	discordOnUploadUsername: string | null;
	discordOnUploadAvatarUrl: string | null;
	discordOnUploadContent: string | null;
	discordOnUploadEmbed: UploadEmbed | null;
	discordOnShortenWebhookUrl: string | null;
	discordOnShortenUsername: string | null;
	discordOnShortenAvatarUrl: string | null;
	discordOnShortenContent: string | null;
	discordOnShortenEmbed: ShortenEmbed | null;
	pwaEnabled: boolean;
	pwaTitle: string;
	pwaShortName: string;
	pwaDescription: string;
	pwaThemeColor: string;
	pwaBackgroundColor: string;
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

export type ShortenEmbed = UploadEmbed;

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

export type APIUsersNoIncl = Array<APIUserNoIncl>;

export type APIUsers = Array<APIUser>;

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

export type APIURLs = Array<APIURL>;

export interface APIFolder {
	id: string;
	createdAt: string;
	updatedAt: string;
	name: string;
	public: boolean;
	userId: string;
	files: Array<APIFile>;
}

export type APIFolderNoIncl = Omit<APIFolder, "files">;

export type APIFolders = Array<APIFolder>;

export type APIFoldersNoIncl = Array<APIFolderNoIncl>;

export interface APITag {
	id: string;
	createdAt: string;
	updatedAt: string;
	name: string;
	color: string;
	files: Array<TagFile>;
}

export type APITags = Array<APITag>;

export interface TagFile {
	id: string;
}

export interface APIFiles {
	page: Array<APIFile>;
	total: number;
	pages: number;
}

export interface APIStat {
	id: string;
	createdAt: string;
	updatedAt: string;
	data: APIStatData;
}

export type APIStats = Array<APIStat>;

export interface APIStatData {
	users: number;
	files: number;
	fileViews: number;
	urls: number;
	urlViews: number;
	storage: number;
	filesUsers: Array<APIStatUserFile>;
	urlsUsers: Array<APIStatUserURL>;
	types: Array<APIStatFileType>;
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

export type APIInvites = Array<APIInvite>;

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

export type APIExports = Array<APIExport>;

export interface ServerActionResponse {
	status: string;
}

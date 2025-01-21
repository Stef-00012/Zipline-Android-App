// /api/user

export interface APIUser {
	id: string;
	username: string;
	createdAt: string;
	updatedAt: string;
	role: "SUPERADMIN" | "ADMIN" | "USER";
	view: APIUserView;
	oauthProviders: Array<OAuthProvider>;
	totpSecret: string;
	passkeys: Array<Passkey>;
	quota: null;
	sessions: Array<string>;
}

export interface APIUserView {
	enabled: boolean;
	align: "left" | "center" | "right";
	showMimetype: boolean;
	content: string;
	embed: boolean;
	embedTitle: string;
	embedDescription: string;
	embedColor: string;
	embedSiteName: string;
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
	credProps: null;
}

export type APIRecentFiles = Array<APIFile>;

export interface APIFile {
	createdAt: string;
	updatedAt: string;
	deletesAt: string | null;
	favorite: boolean;
	id: string;
	originalName: string | null;
	size: number;
	type: string;
	views: number;
	maxViews: number | null;
	thumbnail: string | null;
	tags: Array<string>;
	password: string | null;
	url: string;
}

export interface APIStats {
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

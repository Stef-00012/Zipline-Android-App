import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import type {
	APISettings,
	ExternalLink,
	ShortenEmbed,
	UploadEmbed,
} from "@/types/zipline";
import { useState, useEffect } from "react";
import { getSettings, updateSettings } from "@/functions/zipline/settings";
import { View, Text, ToastAndroid } from "react-native";
import { styles } from "@/styles/admin/settings";
import Select from "@/components/Select";
import { formats } from "@/constants/adminSettings";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import {
	defaultUploadEmbed,
	defaultShortenEmbed,
} from "@/constants/adminSettings";
import { convertToBytes, convertToTime } from "@/functions/util";
import TextInput from "@/components/TextInput";
import Switch from "@/components/Switch";
import Button from "@/components/Button";

export default function ServerSettings() {
	useAuth("SUPERADMIN");
	useShareIntent();

	const [settings, setSettings] = useState<APISettings | null>(null);

	useEffect(() => {
		(async () => {
			const settings = await getSettings();

			setSettings(typeof settings === "string" ? null : settings);
		})();
	}, []);

	const [coreReturnHttpsUrls, setCoreReturnHttpsUrls] =
		useState<boolean>(false);
	const [coreDefaultDomain, setCoreDefaultDomain] = useState<string | null>(
		null,
	);
	const [coreTempDirectory, setCoreTempDirectory] = useState<string>("");
	const [chunksEnabled, setChunksEnabled] = useState<boolean>(false);
	const [chunksMax, setChunksMax] = useState<string>("");
	const [chunksSize, setChunksSize] = useState<string>("");
	const [tasksDeleteInterval, setTasksDeleteInterval] = useState<string>("");
	const [tasksClearInvitesInterval, setTasksClearInvitesInterval] =
		useState<string>("");
	const [tasksMaxViewsInterval, setTasksMaxViewsInterval] =
		useState<string>("");
	const [tasksThumbnailsInterval, setTasksThumbnailsInterval] =
		useState<string>("");
	const [tasksMetricsInterval, setTasksMetricsInterval] = useState<string>("");
	const [filesRoute, setFilesRoute] = useState<string | undefined>(undefined);
	const [filesLength, setFilesLength] = useState<number | undefined>(undefined);
	const [filesDefaultFormat, setFilesDefaultFormat] = useState<
		"random" | "uuid" | "date" | "name" | "gfycat"
	>("random");
	const [filesDisabledExtensions, setFilesDisabledExtensions] = useState<
		Array<string> | undefined
	>(undefined);
	const [filesMaxFileSize, setFilesMaxFileSize] = useState<string | undefined>(
		undefined,
	);
	const [filesDefaultExpiration, setFilesDefaultExpiration] = useState<
		string | undefined
	>(undefined);
	const [filesAssumeMimetypes, setFilesAssumeMimetypes] =
		useState<boolean>(false);
	const [filesDefaultDateFormat, setFilesDefaultDateFormat] = useState<
		string | undefined
	>(undefined);
	const [filesRemoveGpsMetadata, setFilesRemoveGpsMetadata] =
		useState<boolean>(false);
	const [urlsRoute, setUrlsRoute] = useState<string | undefined>(undefined);
	const [urlsLength, setUrlsLength] = useState<number | undefined>(undefined);
	const [featuresImageCompression, setFeaturesImageCompression] =
		useState<boolean>(false);
	const [featuresRobotsTxt, setFeaturesRobotsTxt] = useState<boolean>(false);
	const [featuresHealthcheck, setFeaturesHealthcheck] =
		useState<boolean>(false);
	const [featuresUserRegistration, setFeaturesUserRegistration] =
		useState<boolean>(false);
	const [featuresOauthRegistration, setFeaturesOauthRegistration] =
		useState<boolean>(false);
	const [featuresDeleteOnMaxViews, setFeaturesDeleteOnMaxViews] =
		useState<boolean>(false);
	const [featuresThumbnailsEnabled, setFeaturesThumbnailsEnabled] =
		useState<boolean>(false);
	const [featuresThumbnailsNumberThreads, setFeaturesThumbnailsNumberThreads] =
		useState<number | undefined>(undefined);
	const [featuresMetricsEnabled, setFeaturesMetricsEnabled] =
		useState<boolean>(false);
	const [featuresMetricsAdminOnly, setFeaturesMetricsAdminOnly] =
		useState<boolean>(false);
	const [featuresMetricsShowUserSpecific, setFeaturesMetricsShowUserSpecific] =
		useState<boolean>(false);
	const [invitesEnabled, setInvitesEnabled] = useState<boolean>(false);
	const [invitesLength, setInvitesLength] = useState<number | undefined>(
		undefined,
	);
	const [websiteTitle, setWebsiteTitle] = useState<string | undefined>(
		undefined,
	);
	const [websiteTitleLogo, setWebsiteTitleLogo] = useState<string | null>(null);
	const [websiteExternalLinks, setWebsiteExternalLinks] = useState<
		string | undefined
	>(undefined);
	const [originalWebsiteExternalLinks, setOriginalWebsiteExternalLinks] =
		useState<Array<ExternalLink> | undefined>(undefined);
	const [websiteLoginBackground, setWebsiteLoginBackground] = useState<
		string | null
	>(null);
	const [websiteLoginBackgroundBlur, setWebsiteLoginBackgroundBlur] =
		useState<boolean>(false);
	const [websiteDefaultAvatar, setWebsiteDefaultAvatar] = useState<
		string | null
	>(null);
	const [websiteTos, setWebsiteTos] = useState<string | null>(null);
	const [websiteThemeDefault, setWebsiteThemeDefault] = useState<
		string | undefined
	>(undefined);
	const [websiteThemeDark, setWebsiteThemeDark] = useState<string | undefined>(
		undefined,
	);
	const [websiteThemeLight, setWebsiteThemeLight] = useState<
		string | undefined
	>(undefined);
	const [oauthBypassLocalLogin, setOauthBypassLocalLogin] =
		useState<boolean>(false);
	const [oauthLoginOnly, setOauthLoginOnly] = useState<boolean>(false);
	const [oauthDiscordClientId, setOauthDiscordClientId] = useState<
		string | null
	>(null);
	const [oauthDiscordClientSecret, setOauthDiscordClientSecret] = useState<
		string | null
	>(null);
	const [oauthDiscordRedirectUri, setOauthDiscordRedirectUri] = useState<
		string | null
	>(null);
	const [oauthGoogleClientId, setOauthGoogleClientId] = useState<string | null>(
		null,
	);
	const [oauthGoogleClientSecret, setOauthGoogleClientSecret] = useState<
		string | null
	>(null);
	const [oauthGoogleRedirectUri, setOauthGoogleRedirectUri] = useState<
		string | null
	>(null);
	const [oauthGithubClientId, setOauthGithubClientId] = useState<string | null>(
		null,
	);
	const [oauthGithubClientSecret, setOauthGithubClientSecret] = useState<
		string | null
	>(null);
	const [oauthGithubRedirectUri, setOauthGithubRedirectUri] = useState<
		string | null
	>(null);
	const [oauthOidcClientId, setOauthOidcClientId] = useState<string | null>(
		null,
	);
	const [oauthOidcClientSecret, setOauthOidcClientSecret] = useState<
		string | null
	>(null);
	const [oauthOidcAuthorizeUrl, setOauthOidcAuthorizeUrl] = useState<
		string | null
	>(null);
	const [oauthOidcTokenUrl, setOauthOidcTokenUrl] = useState<string | null>(
		null,
	);
	const [oauthOidcUserinfoUrl, setOauthOidcUserinfoUrl] = useState<
		string | null
	>(null);
	const [oauthOidcRedirectUri, setOauthOidcRedirectUri] = useState<
		string | null
	>(null);
	const [mfaTotpEnabled, setMfaTotpEnabled] = useState<boolean>(false);
	const [mfaTotpIssuer, setMfaTotpIssuer] = useState<string | undefined>(
		undefined,
	);
	const [mfaPasskeys, setMfaPasskeys] = useState<boolean>(false);
	const [ratelimitEnabled, setRatelimitEnabled] = useState<boolean>(false);
	const [ratelimitMax, setRatelimitMax] = useState<number | undefined>(
		undefined,
	);
	const [ratelimitWindow, setRatelimitWindow] = useState<number | null>(null);
	const [ratelimitAdminBypass, setRatelimitAdminBypass] =
		useState<boolean>(false);
	const [ratelimitAllowList, setRatelimitAllowList] = useState<
		Array<string> | undefined
	>(undefined);
	const [httpWebhookOnUpload, setHttpWebhookOnUpload] = useState<string | null>(
		null,
	);
	const [httpWebhookOnShorten, setHttpWebhookOnShorten] = useState<
		string | null
	>(null);
	const [discordWebhookUrl, setDiscordWebhookUrl] = useState<string | null>(
		null,
	);
	const [discordUsername, setDiscordUsername] = useState<string | null>(null);
	const [discordAvatarUrl, setDiscordAvatarUrl] = useState<string | null>(null);
	const [discordOnUploadWebhookUrl, setDiscordOnUploadWebhookUrl] = useState<
		string | null
	>(null);
	const [discordOnUploadUsername, setDiscordOnUploadUsername] = useState<
		string | null
	>(null);
	const [discordOnUploadAvatarUrl, setDiscordOnUploadAvatarUrl] = useState<
		string | null
	>(null);
	const [discordOnUploadContent, setDiscordOnUploadContent] = useState<
		string | null
	>(null);
	const [discordOnUploadEmbed, setDiscordOnUploadEmbed] =
		useState<UploadEmbed | null>(null);
	const [originalDiscordOnUploadEmbed, setOriginalDiscordOnUploadEmbed] =
		useState<UploadEmbed | null>(null);
	const [discordOnShortenWebhookUrl, setDiscordOnShortenWebhookUrl] = useState<
		string | null
	>(null);
	const [discordOnShortenUsername, setDiscordOnShortenUsername] = useState<
		string | null
	>(null);
	const [discordOnShortenAvatarUrl, setDiscordOnShortenAvatarUrl] = useState<
		string | null
	>(null);
	const [discordOnShortenContent, setDiscordOnShortenContent] = useState<
		string | null
	>(null);
	const [discordOnShortenEmbed, setDiscordOnShortenEmbed] =
		useState<ShortenEmbed | null>(null);
	const [originalDiscordOnShortenEmbed, setOriginalDiscordOnShortenEmbed] =
		useState<ShortenEmbed | null>(null);
	const [pwaEnabled, setPwaEnabled] = useState<boolean>(false);
	const [pwaTitle, setPwaTitle] = useState<string | undefined>(undefined);
	const [pwaShortName, setPwaShortName] = useState<string | undefined>(
		undefined,
	);
	const [pwaDescription, setPwaDescription] = useState<string | undefined>(
		undefined,
	);
	const [pwaThemeColor, setPwaThemeColor] = useState<string | undefined>(
		undefined,
	);
	const [pwaBackgroundColor, setPwaBackgroundColor] = useState<
		string | undefined
	>(undefined);

	useEffect(() => {
		if (settings) {
			setCoreReturnHttpsUrls(settings.coreReturnHttpsUrls);
			setCoreDefaultDomain(settings.coreDefaultDomain);
			setCoreTempDirectory(settings.coreTempDirectory);
			setChunksEnabled(settings.chunksEnabled);
			setChunksMax(
				convertToBytes(settings.chunksMax, {
					unitSeparator: " ",
				}) || "",
			);
			setChunksSize(
				convertToBytes(settings.chunksSize, {
					unitSeparator: " ",
				}) || "",
			);
			setTasksDeleteInterval(
				convertToTime(settings.tasksDeleteInterval, {
					shortFormat: true,
				}) || "",
			);
			setTasksClearInvitesInterval(
				convertToTime(settings.tasksClearInvitesInterval, {
					shortFormat: true,
				}) || "",
			);
			setTasksMaxViewsInterval(
				convertToTime(settings.tasksMaxViewsInterval, {
					shortFormat: true,
				}) || "",
			);
			setTasksThumbnailsInterval(
				convertToTime(settings.tasksThumbnailsInterval, {
					shortFormat: true,
				}) || "",
			);
			setTasksMetricsInterval(
				convertToTime(settings.tasksMetricsInterval, {
					shortFormat: true,
				}) || "",
			);
			setFilesRoute(settings.filesRoute);
			setFilesLength(settings.filesLength);
			setFilesDefaultFormat(settings.filesDefaultFormat);
			setFilesDisabledExtensions(settings.filesDisabledExtensions);
			setFilesMaxFileSize(
				convertToBytes(settings.filesMaxFileSize, {
					unitSeparator: " ",
				}) || undefined,
			);
			setFilesDefaultExpiration(settings.filesDefaultExpiration || undefined);
			setFilesAssumeMimetypes(settings.filesAssumeMimetypes);
			setFilesDefaultDateFormat(settings.filesDefaultDateFormat);
			setFilesRemoveGpsMetadata(settings.filesRemoveGpsMetadata);
			setUrlsRoute(settings.urlsRoute);
			setUrlsLength(settings.urlsLength);
			setFeaturesImageCompression(settings.featuresImageCompression);
			setFeaturesRobotsTxt(settings.featuresRobotsTxt);
			setFeaturesHealthcheck(settings.featuresHealthcheck);
			setFeaturesUserRegistration(settings.featuresUserRegistration);
			setFeaturesOauthRegistration(settings.featuresOauthRegistration);
			setFeaturesDeleteOnMaxViews(settings.featuresDeleteOnMaxViews);
			setFeaturesThumbnailsEnabled(settings.featuresThumbnailsEnabled);
			setFeaturesThumbnailsNumberThreads(
				settings.featuresThumbnailsNumberThreads,
			);
			setFeaturesMetricsEnabled(settings.featuresMetricsEnabled);
			setFeaturesMetricsAdminOnly(settings.featuresMetricsAdminOnly);
			setFeaturesMetricsShowUserSpecific(
				settings.featuresMetricsShowUserSpecific,
			);
			setInvitesEnabled(settings.invitesEnabled);
			setInvitesLength(settings.invitesLength);
			setWebsiteTitle(settings.websiteTitle);
			setWebsiteTitleLogo(settings.websiteTitleLogo);
			setWebsiteExternalLinks(
				JSON.stringify(settings.websiteExternalLinks, null, 4),
			);
			setOriginalWebsiteExternalLinks(settings.websiteExternalLinks);
			setWebsiteLoginBackground(settings.websiteLoginBackground);
			setWebsiteLoginBackgroundBlur(settings.websiteLoginBackgroundBlur);
			setWebsiteDefaultAvatar(settings.websiteDefaultAvatar);
			setWebsiteTos(settings.websiteTos);
			setWebsiteThemeDefault(settings.websiteThemeDefault);
			setWebsiteThemeDark(settings.websiteThemeDark);
			setWebsiteThemeLight(settings.websiteThemeLight);
			setOauthBypassLocalLogin(settings.oauthBypassLocalLogin);
			setOauthLoginOnly(settings.oauthLoginOnly);
			setOauthDiscordClientId(settings.oauthDiscordClientId);
			setOauthDiscordClientSecret(settings.oauthDiscordClientSecret);
			setOauthDiscordRedirectUri(settings.oauthDiscordRedirectUri);
			setOauthGoogleClientId(settings.oauthGoogleClientId);
			setOauthGoogleClientSecret(settings.oauthGoogleClientSecret);
			setOauthGoogleRedirectUri(settings.oauthGoogleRedirectUri);
			setOauthGithubClientId(settings.oauthGithubClientId);
			setOauthGithubClientSecret(settings.oauthGithubClientSecret);
			setOauthGithubRedirectUri(settings.oauthGithubRedirectUri);
			setOauthOidcClientId(settings.oauthOidcClientId);
			setOauthOidcClientSecret(settings.oauthOidcClientSecret);
			setOauthOidcAuthorizeUrl(settings.oauthOidcAuthorizeUrl);
			setOauthOidcTokenUrl(settings.oauthOidcTokenUrl);
			setOauthOidcUserinfoUrl(settings.oauthOidcUserinfoUrl);
			setOauthOidcRedirectUri(settings.oauthOidcRedirectUri);
			setMfaTotpEnabled(settings.mfaTotpEnabled);
			setMfaTotpIssuer(settings.mfaTotpIssuer);
			setMfaPasskeys(settings.mfaPasskeys);
			setRatelimitEnabled(settings.ratelimitEnabled);
			setRatelimitMax(settings.ratelimitMax);
			setRatelimitWindow(settings.ratelimitWindow);
			setRatelimitAdminBypass(settings.ratelimitAdminBypass);
			setRatelimitAllowList(settings.ratelimitAllowList);
			setHttpWebhookOnUpload(settings.httpWebhookOnUpload);
			setHttpWebhookOnShorten(settings.httpWebhookOnShorten);
			setDiscordWebhookUrl(settings.discordWebhookUrl);
			setDiscordUsername(settings.discordUsername);
			setDiscordAvatarUrl(settings.discordAvatarUrl);
			setDiscordOnUploadWebhookUrl(settings.discordOnUploadWebhookUrl);
			setDiscordOnUploadUsername(settings.discordOnUploadUsername);
			setDiscordOnUploadAvatarUrl(settings.discordOnUploadAvatarUrl);
			setDiscordOnUploadContent(settings.discordOnUploadContent);
			setDiscordOnUploadEmbed(settings.discordOnUploadEmbed);
			setOriginalDiscordOnUploadEmbed(settings.discordOnUploadEmbed);
			setDiscordOnShortenWebhookUrl(settings.discordOnShortenWebhookUrl);
			setDiscordOnShortenUsername(settings.discordOnShortenUsername);
			setDiscordOnShortenAvatarUrl(settings.discordOnShortenAvatarUrl);
			setDiscordOnShortenContent(settings.discordOnShortenContent);
			setDiscordOnShortenEmbed(settings.discordOnShortenEmbed);
			setOriginalDiscordOnShortenEmbed(settings.discordOnShortenEmbed);
			setPwaEnabled(settings.pwaEnabled);
			setPwaTitle(settings.pwaTitle);
			setPwaShortName(settings.pwaShortName);
			setPwaDescription(settings.pwaDescription);
			setPwaThemeColor(settings.pwaThemeColor);
			setPwaBackgroundColor(settings.pwaBackgroundColor);
		}
	}, [settings]);

	const [saveError, setSaveError] = useState<Array<string> | null>(null);

	type SaveCategories =
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

	async function handleSave(category: SaveCategories) {
		setSaveError(null);
		let saveSettings: Partial<APISettings> = {};

		switch (category) {
			case "core": {
				saveSettings = {
					coreReturnHttpsUrls,
					coreDefaultDomain,
					coreTempDirectory,
				};

				break;
			}

			case "chunks": {
				saveSettings = {
					chunksEnabled,
					chunksMax,
					chunksSize,
				};

				break;
			}

			case "tasks": {
				saveSettings = {
					tasksClearInvitesInterval,
					tasksDeleteInterval,
					tasksMaxViewsInterval,
					tasksMetricsInterval,
					tasksThumbnailsInterval,
				};

				break;
			}

			case "mfa": {
				saveSettings = {
					mfaPasskeys,
					mfaTotpEnabled,
					mfaTotpIssuer,
				};

				break;
			}

			case "features": {
				saveSettings = {
					featuresImageCompression,
					featuresRobotsTxt,
					featuresHealthcheck,
					featuresUserRegistration,
					featuresOauthRegistration,
					featuresDeleteOnMaxViews,
					featuresThumbnailsEnabled,
					featuresThumbnailsNumberThreads,
					featuresMetricsEnabled,
					featuresMetricsAdminOnly,
					featuresMetricsShowUserSpecific,
				};

				break;
			}

			case "files": {
				saveSettings = {
					filesRoute,
					filesLength,
					filesAssumeMimetypes,
					filesRemoveGpsMetadata,
					filesDefaultFormat,
					filesDisabledExtensions,
					filesMaxFileSize,
					filesDefaultExpiration,
					filesDefaultDateFormat,
				};

				break;
			}

			case "urlShortener": {
				saveSettings = {
					urlsRoute,
					urlsLength,
				};

				break;
			}

			case "invites": {
				saveSettings = {
					invitesEnabled,
					invitesLength,
				};

				break;
			}

			case "ratelimit": {
				saveSettings = {
					ratelimitEnabled,
					ratelimitMax,
					ratelimitWindow,
					ratelimitAdminBypass,
					ratelimitAllowList,
				};

				break;
			}

			case "website": {
				saveSettings = {
					websiteTitle,
					websiteTitleLogo,
					websiteLoginBackground,
					websiteLoginBackgroundBlur,
					websiteDefaultAvatar,
					websiteTos,
					websiteThemeDefault,
					websiteThemeDark,
					websiteThemeLight,
				};

				try {
					saveSettings.websiteExternalLinks = JSON.parse(
						websiteExternalLinks || "",
					);
				} catch (e) {}

				break;
			}

			case "oauth": {
				saveSettings = {
					oauthBypassLocalLogin,
					oauthLoginOnly,
					oauthDiscordClientId,
					oauthDiscordClientSecret,
					oauthDiscordRedirectUri,
					oauthGoogleClientId,
					oauthGoogleClientSecret,
					oauthGoogleRedirectUri,
					oauthGithubClientId,
					oauthGithubClientSecret,
					oauthGithubRedirectUri,
					oauthOidcClientId,
					oauthOidcClientSecret,
					oauthOidcAuthorizeUrl,
					oauthOidcTokenUrl,
					oauthOidcUserinfoUrl,
					oauthOidcRedirectUri,
				};

				break;
			}

			case "pwa": {
				saveSettings = {
					pwaEnabled,
					pwaTitle,
					pwaShortName,
					pwaDescription,
					pwaThemeColor,
					pwaBackgroundColor,
				};

				break;
			}

			case "httpWebhooks": {
				saveSettings = {
					httpWebhookOnUpload,
					httpWebhookOnShorten,
				};

				break;
			}

			case "discordWebhook": {
				saveSettings = {
					discordWebhookUrl,
					discordUsername,
					discordAvatarUrl,
				};

				break;
			}

			case "discordOnShortenWebhook": {
				saveSettings = {
					discordOnShortenWebhookUrl,
					discordOnShortenUsername,
					discordOnShortenAvatarUrl,
					discordOnShortenContent,
					discordOnShortenEmbed,
				};

				break;
			}

			case "discordOnUploadWebhook": {
				saveSettings = {
					discordOnUploadWebhookUrl,
					discordOnUploadUsername,
					discordOnUploadAvatarUrl,
					discordOnUploadContent,
					discordOnUploadEmbed,
				};

				break;
			}
		}

		if (Object.keys(saveSettings).length <= 0) return "Something went wrong...";

		const success = await updateSettings(saveSettings);

		if (Array.isArray(success)) return setSaveError(success);

		return ToastAndroid.show(
			"Successfully saved the settings",
			ToastAndroid.SHORT,
		);
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				{settings ? (
					<View style={styles.settingsContainer}>
						<View style={styles.header}>
							<Text style={styles.headerText}>Server Settings</Text>

							{saveError && (
								<View>
									{saveError.map((error) => (
										<Text style={styles.errorText} key={error}>
											{error}
										</Text>
									))}
								</View>
							)}
						</View>

						<KeyboardAwareScrollView style={styles.scrollView}>
							{/* Core */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Core</Text>

								<Switch
									title="Return HTTPS URLs"
									value={coreReturnHttpsUrls || false}
									onValueChange={() => setCoreReturnHttpsUrls((prev) => !prev)}
								/>

								<TextInput
									title="Default Domain:"
									keyboardType="url"
									onValueChange={(content) => setCoreDefaultDomain(content)}
									value={coreDefaultDomain || ""}
									placeholder="example.com"
								/>

								<TextInput
									title="Temporary Directory:"
									onValueChange={(content) => setCoreTempDirectory(content)}
									value={coreTempDirectory || ""}
									placeholder="/tmp/zipline"
								/>

								<Button
									onPress={() => handleSave("core")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Chunks */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Chunks</Text>

								<Switch
									title="Enable Chunks"
									value={chunksEnabled || false}
									onValueChange={() => setChunksEnabled((prev) => !prev)}
								/>

								<TextInput
									title="Max Chunk Size:"
									onValueChange={(content) => setChunksMax(content)}
									value={chunksMax || ""}
									placeholder="95mb"
								/>

								<TextInput
									title="Chunk Size:"
									onValueChange={(content) => setChunksSize(content)}
									value={chunksSize || ""}
									placeholder="25mb"
								/>

								<Button
									onPress={() => handleSave("chunks")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Tasks */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Tasks</Text>

								<TextInput
									title="Delete Files Interval:"
									onValueChange={(content) => setTasksDeleteInterval(content)}
									value={tasksDeleteInterval || ""}
									placeholder="30m"
								/>

								<TextInput
									title="Clear Invites Interval:"
									onValueChange={(content) =>
										setTasksClearInvitesInterval(content)
									}
									value={tasksClearInvitesInterval || ""}
									placeholder="30m"
								/>

								<TextInput
									title="Max Views Interval:"
									onValueChange={(content) => setTasksMaxViewsInterval(content)}
									value={tasksMaxViewsInterval || ""}
									placeholder="30m"
								/>

								<TextInput
									title="Thumbnail Interval:"
									onValueChange={(content) =>
										setTasksThumbnailsInterval(content)
									}
									value={tasksThumbnailsInterval || ""}
									placeholder="5m"
								/>

								<Button
									onPress={() => handleSave("tasks")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* MFA */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>
									Multi-Factor Authentication
								</Text>

								<Switch
									title="Passkeys"
									value={mfaPasskeys || false}
									onValueChange={() => setMfaPasskeys((prev) => !prev)}
								/>

								<Switch
									title="Enable TOTP"
									value={mfaTotpEnabled || false}
									onValueChange={() => setMfaTotpEnabled((prev) => !prev)}
								/>

								<TextInput
									title="Issuer:"
									onValueChange={(content) => setMfaTotpIssuer(content)}
									value={mfaTotpIssuer || ""}
									placeholder="Zipline"
								/>

								<Button
									onPress={() => handleSave("mfa")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Features */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Features</Text>

								<Switch
									title="Image Compression"
									value={featuresImageCompression || false}
									onValueChange={() =>
										setFeaturesImageCompression((prev) => !prev)
									}
								/>

								<Switch
									title="/robots.txt"
									value={featuresRobotsTxt || false}
									onValueChange={() => setFeaturesRobotsTxt((prev) => !prev)}
								/>

								<Switch
									title="Healthcheck"
									value={featuresHealthcheck || false}
									onValueChange={() => setFeaturesHealthcheck((prev) => !prev)}
								/>

								<Switch
									title="User Registration"
									value={featuresUserRegistration || false}
									onValueChange={() =>
										setFeaturesUserRegistration((prev) => !prev)
									}
								/>

								<Switch
									title="OAuth Registration"
									value={featuresOauthRegistration || false}
									onValueChange={() =>
										setFeaturesOauthRegistration((prev) => !prev)
									}
								/>

								<Switch
									title="Delete on Max Views"
									value={featuresDeleteOnMaxViews || false}
									onValueChange={() =>
										setFeaturesDeleteOnMaxViews((prev) => !prev)
									}
								/>

								<Switch
									title="Enable Metrics"
									value={featuresMetricsEnabled || false}
									onValueChange={() =>
										setFeaturesMetricsEnabled((prev) => !prev)
									}
								/>

								<Switch
									title="Admin Only Metrics"
									value={featuresMetricsAdminOnly || false}
									onValueChange={() =>
										setFeaturesMetricsAdminOnly((prev) => !prev)
									}
								/>

								<Switch
									title="Show User Specific Metrics"
									value={featuresMetricsShowUserSpecific || false}
									onValueChange={() =>
										setFeaturesMetricsShowUserSpecific((prev) => !prev)
									}
								/>

								<Switch
									title="Enable Thumbnails"
									value={featuresThumbnailsEnabled || false}
									onValueChange={() =>
										setFeaturesThumbnailsEnabled((prev) => !prev)
									}
								/>

								<TextInput
									title="Thumbnails Number Threads:"
									keyboardType="numeric"
									onValueChange={(content) =>
										setFeaturesThumbnailsNumberThreads(
											Math.abs(Number.parseInt(content)),
										)
									}
									value={
										featuresThumbnailsNumberThreads
											? String(featuresThumbnailsNumberThreads) || ""
											: ""
									}
									placeholder="4"
								/>

								<Button
									onPress={() => handleSave("features")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Files */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Files</Text>

								<TextInput
									title="Route:"
									onValueChange={(content) => setFilesRoute(content)}
									value={filesRoute || ""}
									placeholder="/u"
								/>

								<TextInput
									title="Length:"
									keyboardType="numeric"
									onValueChange={(content) =>
										setFilesLength(Math.abs(Number.parseInt(content)))
									}
									value={filesLength ? String(filesLength) || "" : ""}
									placeholder="6"
								/>

								<Switch
									title="Assume Mimetypes"
									value={filesAssumeMimetypes || false}
									onValueChange={() => setFilesAssumeMimetypes((prev) => !prev)}
								/>

								<Switch
									title="Remove GPS Metadata"
									value={filesRemoveGpsMetadata || false}
									onValueChange={() =>
										setFilesRemoveGpsMetadata((prev) => !prev)
									}
								/>

								<Text style={styles.inputHeader}>Default Format:</Text>
								<Select
									data={formats}
									onSelect={(selectedFormat) => {
										if (selectedFormat.length <= 0) return;

										setFilesDefaultFormat(
											selectedFormat[0].value as typeof filesDefaultFormat,
										);
									}}
									placeholder="Select format..."
									defaultValue={formats.find(
										(format) => format.value === filesDefaultFormat,
									)}
								/>

								<TextInput
									title="Disabled Extensions:"
									onValueChange={(content) =>
										setFilesDisabledExtensions(content.split(", "))
									}
									value={filesDisabledExtensions?.join(", ") || ""}
									placeholder="exe, bat, sh"
								/>

								<TextInput
									title="Max File Size:"
									onValueChange={(content) => setFilesMaxFileSize(content)}
									value={filesMaxFileSize || ""}
									placeholder="100mb"
								/>

								<TextInput
									title="Default Expiration:"
									onValueChange={(content) =>
										setFilesDefaultExpiration(content)
									}
									value={filesDefaultExpiration || ""}
									placeholder="30d"
								/>

								<TextInput
									title="Default Date Format:"
									onValueChange={(content) =>
										setFilesDefaultDateFormat(content)
									}
									value={filesDefaultDateFormat || ""}
									placeholder="YYYY-MM-DD_HH:mm:ss"
								/>

								<Button
									onPress={() => handleSave("files")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Url Shortener */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>URL Shortener</Text>

								<TextInput
									title="Route:"
									onValueChange={(content) => setUrlsRoute(content)}
									value={urlsRoute || ""}
									placeholder="/go"
								/>

								<TextInput
									title="Length:"
									keyboardType="numeric"
									onValueChange={(content) =>
										setUrlsLength(Math.abs(Number.parseInt(content)))
									}
									value={urlsLength ? String(urlsLength) || "" : ""}
									placeholder="6"
								/>

								<Button
									onPress={() => handleSave("urlShortener")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Invites */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Invites</Text>

								<Switch
									title="Enable Invites"
									value={invitesEnabled || false}
									onValueChange={() => setInvitesEnabled((prev) => !prev)}
								/>

								<TextInput
									title="Length"
									keyboardType="numeric"
									onValueChange={(content) =>
										setInvitesLength(Math.abs(Number.parseInt(content)))
									}
									value={invitesLength ? String(invitesLength) || "" : ""}
									placeholder="6"
								/>

								<Button
									onPress={() => handleSave("invites")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Ratelimit */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Ratelimit</Text>

								<Switch
									title="Enable Ratelimit"
									value={ratelimitEnabled || false}
									onValueChange={() => setRatelimitEnabled((prev) => !prev)}
								/>

								<Switch
									title="Admin Bypass"
									value={ratelimitAdminBypass || false}
									onValueChange={() => setRatelimitAdminBypass((prev) => !prev)}
								/>

								<TextInput
									title="Max Requests:"
									keyboardType="numeric"
									onValueChange={(content) =>
										setRatelimitMax(Math.abs(Number.parseInt(content)))
									}
									value={ratelimitMax ? String(ratelimitMax) || "" : ""}
									placeholder="10"
								/>

								<TextInput
									title="Window:"
									keyboardType="numeric"
									onValueChange={(content) =>
										setRatelimitWindow(Math.abs(Number.parseInt(content)))
									}
									value={ratelimitWindow ? String(ratelimitWindow) || "" : ""}
									placeholder="60"
								/>

								<TextInput
									title="Allow List:"
									onValueChange={(content) =>
										setRatelimitAllowList(content.split(", "))
									}
									value={ratelimitAllowList?.join(", ") || ""}
									placeholder="1.1.1.1, 8.8.8.8"
								/>

								<Button
									onPress={() => handleSave("ratelimit")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Website */}
							<View style={styles.settingGroup}>
								<TextInput
									title="Title:"
									keyboardType="url"
									onValueChange={(content) => setWebsiteTitle(content)}
									value={websiteTitle || ""}
									placeholder="Zipline"
								/>

								<TextInput
									title="Title Logo:"
									keyboardType="url"
									onValueChange={(content) => setWebsiteTitleLogo(content)}
									value={websiteTitleLogo || ""}
									placeholder="https://example.com/logo.png"
								/>

								<TextInput
									title="External Links:"
									keyboardType="url"
									inputStyle={styles.multilneTextInput}
									multiline
									onValueChange={(content) => setWebsiteExternalLinks(content)}
									value={websiteExternalLinks || ""}
									placeholder="https://example.com/logo.png"
								/>

								<TextInput
									title="Login Background:"
									keyboardType="url"
									onValueChange={(content) =>
										setWebsiteLoginBackground(content)
									}
									value={websiteLoginBackground || ""}
									placeholder="https://example.com/background.png"
								/>

								<Switch
									title="Login Background Blur"
									value={websiteLoginBackgroundBlur || false}
									onValueChange={() =>
										setWebsiteLoginBackgroundBlur((prev) => !prev)
									}
								/>

								<TextInput
									title="Default Avatar:"
									onValueChange={(content) => setWebsiteDefaultAvatar(content)}
									value={websiteDefaultAvatar || ""}
									placeholder="/zipline/avatar.png"
								/>

								<TextInput
									title="Terms of Service:"
									onValueChange={(content) => setWebsiteTos(content)}
									value={websiteTos || ""}
									placeholder="/zipline/TOS.md"
								/>

								<TextInput
									title="Default Theme:"
									onValueChange={(content) => setWebsiteThemeDefault(content)}
									value={websiteThemeDefault || ""}
									placeholder="system"
								/>

								<TextInput
									title="Dark Theme:"
									onValueChange={(content) => setWebsiteThemeDark(content)}
									value={websiteThemeDark || ""}
									placeholder="builtin:dark_gray"
								/>

								<TextInput
									title="Light Theme:"
									onValueChange={(content) => setWebsiteThemeLight(content)}
									value={websiteThemeLight || ""}
									placeholder="builtin:light_gray"
								/>

								<Button
									onPress={() => handleSave("website")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* OAuth */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>OAuth</Text>

								<Switch
									title="Bypass Local Login"
									value={oauthBypassLocalLogin || false}
									onValueChange={() =>
										setOauthBypassLocalLogin((prev) => !prev)
									}
								/>

								<Switch
									title="Login Only"
									value={oauthLoginOnly || false}
									onValueChange={() => setOauthLoginOnly((prev) => !prev)}
								/>

								<View style={styles.settingGroup}>
									<Text
										style={{
											...styles.oauthSubSettingText,
											...styles.oauthSubSettingTextColored,
										}}
									>
										Discord
									</Text>

									<TextInput
										title="Discord Client ID:"
										onValueChange={(content) =>
											setOauthDiscordClientId(content)
										}
										value={oauthDiscordClientId || ""}
									/>

									<TextInput
										title="Discord Client Secret:"
										onValueChange={(content) =>
											setOauthDiscordClientSecret(content)
										}
										value={oauthDiscordClientSecret || ""}
									/>

									<TextInput
										title="Discord Redirect URI:"
										onValueChange={(content) =>
											setOauthDiscordRedirectUri(content)
										}
										value={oauthDiscordRedirectUri || ""}
									/>
								</View>

								<View style={styles.settingGroup}>
									<Text
										style={{
											...styles.oauthSubSettingText,
											...styles.oauthSubSettingTextColored,
										}}
									>
										Google
									</Text>

									<TextInput
										title="Google Client ID:"
										onValueChange={(content) => setOauthGoogleClientId(content)}
										value={oauthGoogleClientId || ""}
									/>

									<TextInput
										title="Google Client Secret:"
										onValueChange={(content) =>
											setOauthGoogleClientSecret(content)
										}
										value={oauthGoogleClientSecret || ""}
									/>

									<TextInput
										title="Google Redirect URI:"
										onValueChange={(content) =>
											setOauthGoogleRedirectUri(content)
										}
										value={oauthGoogleRedirectUri || ""}
									/>
								</View>

								<View style={styles.settingGroup}>
									<Text
										style={{
											...styles.oauthSubSettingText,
											...styles.oauthSubSettingTextColored,
										}}
									>
										GitHub
									</Text>

									<TextInput
										title="GitHub Client ID:"
										onValueChange={(content) => setOauthGithubClientId(content)}
										value={oauthGithubClientId || ""}
									/>

									<TextInput
										title="GitHub Client Secret:"
										onValueChange={(content) =>
											setOauthGithubClientSecret(content)
										}
										value={oauthGithubClientSecret || ""}
									/>

									<TextInput
										title="GitHub Redirect URI:"
										onValueChange={(content) =>
											setOauthGithubRedirectUri(content)
										}
										value={oauthGithubRedirectUri || ""}
									/>
								</View>

								<View style={styles.settingGroup}>
									<Text style={styles.oauthSubSettingText}>OpenID Connect</Text>

									<TextInput
										title="OIDC Client ID:"
										onValueChange={(content) => setOauthOidcClientId(content)}
										value={oauthOidcClientId || ""}
									/>

									<TextInput
										title="OIDC Client Secret:"
										onValueChange={(content) =>
											setOauthOidcClientSecret(content)
										}
										value={oauthOidcClientSecret || ""}
									/>

									<TextInput
										title="OIDC Authorize URL:"
										onValueChange={(content) =>
											setOauthOidcAuthorizeUrl(content)
										}
										value={oauthOidcAuthorizeUrl || ""}
									/>

									<TextInput
										title="OIDC Token URL:"
										onValueChange={(content) => setOauthOidcTokenUrl(content)}
										value={oauthOidcTokenUrl || ""}
									/>

									<TextInput
										title="OIDC Userinfo URL:"
										onValueChange={(content) =>
											setOauthOidcUserinfoUrl(content)
										}
										value={oauthOidcUserinfoUrl || ""}
									/>

									<TextInput
										title="OIDC Redirect URL:"
										onValueChange={(content) =>
											setOauthOidcRedirectUri(content)
										}
										value={oauthOidcRedirectUri || ""}
									/>
								</View>

								<Button
									onPress={() => handleSave("oauth")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* PWA */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>PWA</Text>

								<Switch
									title="PWA Enabled"
									value={pwaEnabled || false}
									onValueChange={() => setPwaEnabled((prev) => !prev)}
								/>

								<TextInput
									title="Title:"
									onValueChange={(content) => setPwaTitle(content)}
									value={pwaTitle || ""}
									placeholder="Zipline"
								/>

								<TextInput
									title="Short Name:"
									onValueChange={(content) => setPwaShortName(content)}
									value={pwaShortName || ""}
									placeholder="Zipline"
								/>

								<TextInput
									title="Description:"
									onValueChange={(content) => setPwaDescription(content)}
									value={pwaDescription || ""}
									placeholder="Zipline"
								/>

								<TextInput
									title="Theme Color:"
									onValueChange={(content) => setPwaThemeColor(content)}
									value={pwaThemeColor || ""}
									placeholder="#000000"
								/>

								<TextInput
									title="Background Color:"
									onValueChange={(content) => setPwaBackgroundColor(content)}
									value={pwaBackgroundColor || ""}
									placeholder="#000000"
								/>

								<Button
									onPress={() => handleSave("pwa")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* HTTP Webhooks */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>HTTP Webhooks</Text>

								<TextInput
									title="On Upload:"
									keyboardType="url"
									onValueChange={(content) => setHttpWebhookOnUpload(content)}
									value={httpWebhookOnUpload || ""}
									placeholder="https://example.com/upload"
								/>

								<TextInput
									title="On Shorten:"
									keyboardType="url"
									onValueChange={(content) => setHttpWebhookOnShorten(content)}
									value={httpWebhookOnShorten || ""}
									placeholder="https://example.com/shorten"
								/>

								<Button
									onPress={() => handleSave("httpWebhooks")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>
							</View>

							{/* Discord Webhook */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Discord Webhook</Text>

								<TextInput
									title="Webhook URL:"
									keyboardType="url"
									onValueChange={(content) => setDiscordWebhookUrl(content)}
									value={discordWebhookUrl || ""}
									placeholder="https://discord.com/api/webhooks/..."
								/>

								<TextInput
									title="Username:"
									onValueChange={(content) => setDiscordUsername(content)}
									value={discordUsername || ""}
									placeholder="Zipline"
								/>

								<TextInput
									title="Avatar URL:"
									keyboardType="url"
									onValueChange={(content) => setDiscordAvatarUrl(content)}
									value={discordAvatarUrl || ""}
									placeholder="https://example.com/avatar.png"
								/>

								<Button
									onPress={() => handleSave("discordWebhook")}
									color="#323ea8"
									text="Save"
									icon="save"
									margin={{
										top: 10,
									}}
								/>

								{/* On Upload */}
								<View style={styles.settingGroup}>
									<Text style={styles.headerText}>On Upload</Text>

									<TextInput
										title="Webhook URL:"
										onValueChange={(content) =>
											setDiscordOnUploadWebhookUrl(content)
										}
										value={discordOnUploadWebhookUrl || ""}
										placeholder="https://discord.com/api/webhooks/..."
									/>

									<TextInput
										title="Username:"
										onValueChange={(content) =>
											setDiscordOnUploadUsername(content)
										}
										value={discordOnUploadUsername || ""}
										placeholder="Zipline Uploads"
									/>

									<TextInput
										title="Avatar URL:"
										keyboardType="url"
										onValueChange={(content) =>
											setDiscordOnUploadAvatarUrl(content)
										}
										value={discordOnUploadAvatarUrl || ""}
										placeholder="https://example.com/uploadAvatar.png"
									/>

									<TextInput
										title="Content:"
										inputStyle={styles.multilneTextInput}
										multiline
										onValueChange={(content) =>
											setDiscordOnUploadContent(content)
										}
										value={discordOnUploadContent || ""}
									/>

									<Switch
										title="Embed"
										value={!!discordOnUploadEmbed || false}
										onValueChange={() =>
											setDiscordOnUploadEmbed((prev) => {
												if (prev) return null;

												return (
													originalDiscordOnUploadEmbed || defaultUploadEmbed
												);
											})
										}
									/>

									{/* On Upload Embed */}
									<View
										style={{
											...styles.settingGroup,
											...(!discordOnUploadEmbed && { display: "none" }),
										}}
									>
										<TextInput
											title="Title:"
											onValueChange={(content) =>
												setDiscordOnUploadEmbed(
													(embed) =>
														({
															...embed,
															title: content,
														}) as UploadEmbed,
												)
											}
											value={discordOnUploadEmbed?.title || ""}
										/>

										<TextInput
											title="Description:"
											onValueChange={(content) =>
												setDiscordOnUploadEmbed(
													(embed) =>
														({
															...embed,
															description: content,
														}) as UploadEmbed,
												)
											}
											value={discordOnUploadEmbed?.description || ""}
										/>

										<TextInput
											title="Footer:"
											onValueChange={(content) =>
												setDiscordOnUploadEmbed(
													(embed) =>
														({
															...embed,
															footer: content,
														}) as UploadEmbed,
												)
											}
											value={discordOnUploadEmbed?.footer || ""}
										/>

										<TextInput
											title="Color:"
											onValueChange={(content) =>
												setDiscordOnUploadEmbed(
													(embed) =>
														({
															...embed,
															color: content,
														}) as UploadEmbed,
												)
											}
											value={discordOnUploadEmbed?.color || ""}
										/>

										<Switch
											title="Thumbnail"
											value={!!discordOnUploadEmbed?.thumbnail || false}
											onValueChange={() =>
												setDiscordOnUploadEmbed(
													(embed) =>
														({
															...embed,
															thumbnail: !embed?.thumbnail,
														}) as UploadEmbed,
												)
											}
										/>

										<Switch
											title="Image/Video"
											value={!!discordOnUploadEmbed?.imageOrVideo || false}
											onValueChange={() =>
												setDiscordOnUploadEmbed(
													(embed) =>
														({
															...embed,
															imageOrVideo: !embed?.imageOrVideo,
														}) as UploadEmbed,
												)
											}
										/>

										<Switch
											title="Timestamp"
											value={!!discordOnUploadEmbed?.timestamp || false}
											onValueChange={() =>
												setDiscordOnUploadEmbed(
													(embed) =>
														({
															...embed,
															timestamp: !embed?.timestamp,
														}) as UploadEmbed,
												)
											}
										/>

										<Switch
											title="URL"
											value={!!discordOnUploadEmbed?.url || false}
											onValueChange={() =>
												setDiscordOnUploadEmbed(
													(embed) =>
														({
															...embed,
															url: !embed?.url,
														}) as UploadEmbed,
												)
											}
										/>
									</View>

									<Button
										onPress={() => handleSave("discordOnUploadWebhook")}
										color="#323ea8"
										text="Save"
										icon="save"
										margin={{
											top: 10,
										}}
									/>
								</View>

								{/* On Shorten */}
								<View style={styles.settingGroup}>
									<Text style={styles.headerText}>On Shorten</Text>

									<TextInput
										title="Webhook URL:"
										keyboardType="url"
										onValueChange={(content) =>
											setDiscordOnShortenWebhookUrl(content)
										}
										value={discordOnShortenWebhookUrl || ""}
										placeholder="https://discord.com/api/webhooks/..."
									/>

									<TextInput
										title="Username:"
										onValueChange={(content) =>
											setDiscordOnShortenUsername(content)
										}
										value={discordOnShortenUsername || ""}
										placeholder="Zipline Shortens"
									/>

									<TextInput
										title="Avatar URL:"
										onValueChange={(content) =>
											setDiscordOnShortenAvatarUrl(content)
										}
										value={discordOnShortenAvatarUrl || ""}
										placeholder="https://example.com/shortenAvatar.png"
									/>

									<TextInput
										title="Content:"
										inputStyle={styles.multilneTextInput}
										multiline
										onValueChange={(content) =>
											setDiscordOnShortenContent(content)
										}
										value={discordOnShortenContent || ""}
									/>

									<Switch
										title="Embed"
										value={!!discordOnShortenEmbed}
										onValueChange={() =>
											setDiscordOnShortenEmbed((prev) => {
												if (prev) return null;

												return (
													originalDiscordOnShortenEmbed || defaultShortenEmbed
												);
											})
										}
									/>

									{/* On Shorten Embed */}
									<View
										style={{
											...styles.settingGroup,
											...(!discordOnShortenEmbed && { display: "none" }),
										}}
									>
										<TextInput
											title="Title:"
											onValueChange={(content) =>
												setDiscordOnShortenEmbed(
													(embed) =>
														({
															...embed,
															title: content,
														}) as ShortenEmbed,
												)
											}
											value={discordOnShortenEmbed?.title || ""}
										/>

										<TextInput
											title="Description:"
											onValueChange={(content) =>
												setDiscordOnShortenEmbed(
													(embed) =>
														({
															...embed,
															description: content,
														}) as ShortenEmbed,
												)
											}
											value={discordOnShortenEmbed?.description || ""}
										/>

										<TextInput
											title="Footer:"
											onValueChange={(content) =>
												setDiscordOnShortenEmbed(
													(embed) =>
														({
															...embed,
															footer: content,
														}) as ShortenEmbed,
												)
											}
											value={discordOnShortenEmbed?.footer || ""}
										/>

										<TextInput
											title="Color:"
											onValueChange={(content) =>
												setDiscordOnShortenEmbed(
													(embed) =>
														({
															...embed,
															color: content,
														}) as ShortenEmbed,
												)
											}
											value={discordOnShortenEmbed?.color || ""}
										/>

										<Switch
											title="Thumbnail"
											value={!!discordOnShortenEmbed?.thumbnail || false}
											onValueChange={() =>
												setDiscordOnShortenEmbed(
													(embed) =>
														({
															...embed,
															thumbnail: !embed?.thumbnail,
														}) as ShortenEmbed,
												)
											}
										/>

										<Switch
											title="Image/Video"
											value={!!discordOnShortenEmbed?.imageOrVideo || false}
											onValueChange={() =>
												setDiscordOnShortenEmbed(
													(embed) =>
														({
															...embed,
															imageOrVideo: !embed?.imageOrVideo,
														}) as ShortenEmbed,
												)
											}
										/>

										<Switch
											title="Timestamp"
											value={!!discordOnShortenEmbed?.timestamp || false}
											onValueChange={() =>
												setDiscordOnShortenEmbed(
													(embed) =>
														({
															...embed,
															timestamp: !embed?.timestamp,
														}) as ShortenEmbed,
												)
											}
										/>

										<Switch
											title="URL"
											value={!!discordOnShortenEmbed?.url || false}
											onValueChange={() =>
												setDiscordOnShortenEmbed(
													(embed) =>
														({
															...embed,
															url: !embed?.url,
														}) as ShortenEmbed,
												)
											}
										/>
									</View>

									<Button
										onPress={() => handleSave("discordOnShortenWebhook")}
										color="#323ea8"
										text="Save"
										icon="save"
										margin={{
											top: 10,
										}}
									/>
								</View>
							</View>
						</KeyboardAwareScrollView>
					</View>
				) : (
					<View style={styles.loadingContainer}>
						<Text style={styles.loadingText}>Loading...</Text>
					</View>
				)}
			</View>
		</View>
	);
}

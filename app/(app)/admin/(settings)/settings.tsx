import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import type { APISettings, ShortenEmbed, UploadEmbed } from "@/types/zipline";
import { useState, useEffect } from "react";
import { getSettings, updateSettings } from "@/functions/zipline/settings";
import { View, Text, Pressable, ToastAndroid } from "react-native";
import bytes from "bytes";
import ms from "enhanced-ms";
import { styles } from "@/styles/admin/settings";
import { Switch } from "@react-native-material/core";
import { TextInput } from "react-native";
import Select from "@/components/Select";
import { formats } from "@/constants/settings";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { defaultUploadEmbed, defaultShortenEmbed } from "@/constants/adminSettings";

export default function ServerSettings() {
	useAuth(true);
	useShareIntent();

	const [settings, setSettings] = useState<APISettings | null>(null);

	useEffect(() => {
		(async () => {
			const settings = await getSettings();

			setSettings(typeof settings === "string" ? null : settings);
		})();
	}, []);

	const [coreReturnHttpsUrls, setCoreReturnHttpsUrls] = useState<
		boolean
	>(false);
	const [coreDefaultDomain, setCoreDefaultDomain] = useState<string | null>(
		null,
	);
	const [coreTempDirectory, setCoreTempDirectory] = useState<string>(
		"",
	);
	const [chunksEnabled, setChunksEnabled] = useState<boolean>(false);
	const [chunksMax, setChunksMax] = useState<string>("");
	const [chunksSize, setChunksSize] = useState<string>("");
	const [tasksDeleteInterval, setTasksDeleteInterval] = useState<string>(
		"",
	);
	const [tasksClearInvitesInterval, setTasksClearInvitesInterval] = useState<
		string
	>("");
	const [tasksMaxViewsInterval, setTasksMaxViewsInterval] = useState<
		string
	>("");
	const [tasksThumbnailsInterval, setTasksThumbnailsInterval] = useState<
		string
	>("");
	const [tasksMetricsInterval, setTasksMetricsInterval] = useState<
		string
	>("");
	const [filesRoute, setFilesRoute] = useState<string | undefined>(undefined);
	const [filesLength, setFilesLength] = useState<number | undefined>(undefined);
	const [filesDefaultFormat, setFilesDefaultFormat] = useState<
		"random" | "uuid" | "date" | "name" | "gfycat" | undefined
	>(undefined);
	const [filesDisabledExtensions, setFilesDisabledExtensions] =
		useState<Array<string> | undefined>(undefined);
	const [filesMaxFileSize, setFilesMaxFileSize] = useState<string | undefined>(undefined);
	const [filesDefaultExpiration, setFilesDefaultExpiration] = useState<
		string | undefined
	>(undefined);
	const [filesAssumeMimetypes, setFilesAssumeMimetypes] = useState<
		boolean
	>(false);
	const [filesDefaultDateFormat, setFilesDefaultDateFormat] = useState<
		string | undefined
	>(undefined);
	const [filesRemoveGpsMetadata, setFilesRemoveGpsMetadata] = useState<
		boolean
	>(false);
	const [urlsRoute, setUrlsRoute] = useState<string | null>(null);
	const [urlsLength, setUrlsLength] = useState<number | null>(null);
	const [featuresImageCompression, setFeaturesImageCompression] = useState<
		boolean
	>(false);
	const [featuresRobotsTxt, setFeaturesRobotsTxt] = useState<boolean>(
		false,
	);
	const [featuresHealthcheck, setFeaturesHealthcheck] = useState<
		boolean
	>(false);
	const [featuresUserRegistration, setFeaturesUserRegistration] = useState<
		boolean
	>(false);
	const [featuresOauthRegistration, setFeaturesOauthRegistration] = useState<
		boolean
	>(false);
	const [featuresDeleteOnMaxViews, setFeaturesDeleteOnMaxViews] = useState<
		boolean
	>(false);
	const [featuresThumbnailsEnabled, setFeaturesThumbnailsEnabled] = useState<
		boolean
	>(false);
	const [featuresThumbnailsNumberThreads, setFeaturesThumbnailsNumberThreads] =
		useState<number | undefined>(undefined);
	const [featuresMetricsEnabled, setFeaturesMetricsEnabled] = useState<
		boolean
	>(false);
	const [featuresMetricsAdminOnly, setFeaturesMetricsAdminOnly] = useState<
		boolean
	>(false);
	const [featuresMetricsShowUserSpecific, setFeaturesMetricsShowUserSpecific] =
		useState<boolean>(false);
	const [invitesEnabled, setInvitesEnabled] = useState<boolean>(false);
	const [invitesLength, setInvitesLength] = useState<number | null>(null);
	const [websiteTitle, setWebsiteTitle] = useState<string | null>(null);
	const [websiteTitleLogo, setWebsiteTitleLogo] = useState<string | null>(null);
	const [websiteExternalLinks, setWebsiteExternalLinks] = useState<
		string | null
	>(null);
	const [websiteLoginBackground, setWebsiteLoginBackground] = useState<
		string | null
	>(null);
	const [websiteLoginBackgroundBlur, setWebsiteLoginBackgroundBlur] = useState<
		boolean
	>(false);
	const [websiteDefaultAvatar, setWebsiteDefaultAvatar] = useState<
		string | null
	>(null);
	const [websiteTos, setWebsiteTos] = useState<string | null>(null);
	const [websiteThemeDefault, setWebsiteThemeDefault] = useState<string | null>(
		null,
	);
	const [websiteThemeDark, setWebsiteThemeDark] = useState<string | null>(null);
	const [websiteThemeLight, setWebsiteThemeLight] = useState<string | null>(
		null,
	);
	const [oauthBypassLocalLogin, setOauthBypassLocalLogin] = useState<
		boolean
	>(false);
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
	const [mfaTotpIssuer, setMfaTotpIssuer] = useState<string | undefined>(undefined);
	const [mfaPasskeys, setMfaPasskeys] = useState<boolean>(false);
	const [ratelimitEnabled, setRatelimitEnabled] = useState<boolean>(
		false,
	);
	const [ratelimitMax, setRatelimitMax] = useState<number | null>(null);
	const [ratelimitWindow, setRatelimitWindow] = useState<number | null>(null);
	const [ratelimitAdminBypass, setRatelimitAdminBypass] = useState<
		boolean
	>(false);
	const [ratelimitAllowList, setRatelimitAllowList] =
		useState<Array<string> | null>(null);
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
	const [discordOnUploadEmbed, setDiscordOnUploadEmbed] = useState<UploadEmbed | null>(null);
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
	const [discordOnShortenEmbed, setDiscordOnShortenEmbed] = useState<ShortenEmbed | null>(null);
	const [pwaEnabled, setPwaEnabled] = useState<boolean>(false);
	const [pwaTitle, setPwaTitle] = useState<string | null>(null);
	const [pwaShortName, setPwaShortName] = useState<string | null>(null);
	const [pwaDescription, setPwaDescription] = useState<string | null>(null);
	const [pwaThemeColor, setPwaThemeColor] = useState<string | null>(null);
	const [pwaBackgroundColor, setPwaBackgroundColor] = useState<string | null>(
		null,
	);

	useEffect(() => {
		if (settings) {
			setCoreReturnHttpsUrls(settings.coreReturnHttpsUrls);
			setCoreDefaultDomain(settings.coreDefaultDomain);
			setCoreTempDirectory(settings.coreTempDirectory);
			setChunksEnabled(settings.chunksEnabled);
			setChunksMax(
				typeof settings.chunksMax === "number"
					? bytes(settings.chunksMax) || ""
					: settings.chunksMax,
			);
			setChunksSize(
				typeof settings.chunksSize === "number"
					? bytes(settings.chunksSize) || ""
					: settings.chunksSize,
			);
			setTasksDeleteInterval(
				typeof settings.tasksDeleteInterval === "number"
					? ms(settings.tasksDeleteInterval) || ""
					: settings.tasksDeleteInterval,
			);
			setTasksClearInvitesInterval(
				typeof settings.tasksClearInvitesInterval === "number"
					? ms(settings.tasksClearInvitesInterval) || ""
					: settings.tasksClearInvitesInterval,
			);
			setTasksMaxViewsInterval(
				typeof settings.tasksMaxViewsInterval === "number"
					? ms(settings.tasksMaxViewsInterval) || ""
					: settings.tasksMaxViewsInterval,
			);
			setTasksThumbnailsInterval(
				typeof settings.tasksThumbnailsInterval === "number"
					? ms(settings.tasksThumbnailsInterval) || ""
					: settings.tasksThumbnailsInterval,
			);
			setTasksMetricsInterval(
				typeof settings.tasksMetricsInterval === "number"
					? ms(settings.tasksMetricsInterval) || ""
					: settings.tasksMetricsInterval,
			);
			setFilesRoute(settings.filesRoute);
			setFilesLength(settings.filesLength);
			setFilesDefaultFormat(settings.filesDefaultFormat);
			setFilesDisabledExtensions(settings.filesDisabledExtensions);
			setFilesMaxFileSize(
				typeof settings.filesMaxFileSize === "number"
					? bytes(settings.filesMaxFileSize) || undefined
					: settings.filesMaxFileSize,
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
			setDiscordOnShortenWebhookUrl(settings.discordOnShortenWebhookUrl);
			setDiscordOnShortenUsername(settings.discordOnShortenUsername);
			setDiscordOnShortenAvatarUrl(settings.discordOnShortenAvatarUrl);
			setDiscordOnShortenContent(settings.discordOnShortenContent);
			setDiscordOnShortenEmbed(settings.discordOnShortenEmbed);
			setPwaEnabled(settings.pwaEnabled);
			setPwaTitle(settings.pwaTitle);
			setPwaShortName(settings.pwaShortName);
			setPwaDescription(settings.pwaDescription);
			setPwaThemeColor(settings.pwaThemeColor);
			setPwaBackgroundColor(settings.pwaBackgroundColor);
		}
	}, [settings]);

	const [saveError, setSaveError] = useState<string | null>(null)

	type SaveCategories = "core" | "chunks" | "tasks" | "mfa" | "features" | "files" | "urlShortener" | "invites" | "ratelimit" | "website" | "oauth" | "pwa" | "httpWebhooks" | "discordWebhook" | "discordOnUploadWebhook" | "discordOnShortenWebhook";

	async function handleSave(category: SaveCategories) {
		setSaveError(null)
		let saveSettings: Partial<APISettings> = {}

		switch(category) {

			case "core": {
				saveSettings = {
					coreReturnHttpsUrls,
					coreDefaultDomain,
					coreTempDirectory
				}

				break;
			}

			case "chunks": {
				saveSettings = {
					chunksEnabled,
					chunksMax,
					chunksSize
				}

				break;
			}

			case "tasks": {
				saveSettings = {
					tasksClearInvitesInterval,
					tasksDeleteInterval,
					tasksMaxViewsInterval,
					tasksMetricsInterval,
					tasksThumbnailsInterval
				}

				break;
			}

			case "mfa": {
				saveSettings = {
					mfaPasskeys,
					mfaTotpEnabled,
					mfaTotpIssuer
				}

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
					featuresMetricsShowUserSpecific
				}

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
				}
			}
		}

		if (Object.keys(saveSettings).length <= 0) return "Something went wrong...";

		const success = await updateSettings(saveSettings)

		if (typeof success === "string") return setSaveError(success)

		return ToastAndroid.show(
			"Successfully saved the settings",
			ToastAndroid.SHORT
		)
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				{settings ? (
					<View style={styles.settingsContainer}>
						<Text style={styles.headerText}>Server Settings</Text>
						<KeyboardAwareScrollView style={styles.scrollView}>
							{/* Core */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Core</Text>

								<View style={styles.switchContainer}>
									<Switch
										value={coreReturnHttpsUrls || false}
										onValueChange={() =>
											setCoreReturnHttpsUrls((prev) => !prev)
										}
										thumbColor={coreReturnHttpsUrls ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Return HTTPS URLs</Text>
								</View>

								<Text style={styles.inputHeader}>Default Domain:</Text>
								<TextInput
									keyboardType="url"
									style={styles.textInput}
									onChangeText={(content) => setCoreDefaultDomain(content)}
									value={coreDefaultDomain || ""}
									placeholderTextColor="#222c47"
									placeholder="example.com"
								/>

								<Text style={styles.inputHeader}>Temporary Directory:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setCoreTempDirectory(content)}
									value={coreTempDirectory || ""}
									placeholderTextColor="#222c47"
									placeholder="/tmp/zipline"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* Chunks */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Chunks</Text>

								<View style={styles.switchContainer}>
									<Switch
										value={chunksEnabled || false}
										onValueChange={() => setChunksEnabled((prev) => !prev)}
										thumbColor={chunksEnabled ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Enable Chunks</Text>
								</View>

								<Text style={styles.inputHeader}>Max Chunk Size:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setChunksMax(content)}
									value={chunksMax || ""}
									placeholderTextColor="#222c47"
									placeholder="95mb"
								/>

								<Text style={styles.inputHeader}>Chunk Size:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setChunksSize(content)}
									value={chunksSize || ""}
									placeholderTextColor="#222c47"
									placeholder="25mb"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* Tasks */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Tasks</Text>

								<Text style={styles.inputHeader}>Delete Files Interval:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setTasksDeleteInterval(content)}
									value={tasksDeleteInterval || ""}
									placeholderTextColor="#222c47"
									placeholder="30m"
								/>

								<Text style={styles.inputHeader}>Clear Invites Interval:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) =>
										setTasksClearInvitesInterval(content)
									}
									value={tasksClearInvitesInterval || ""}
									placeholderTextColor="#222c47"
									placeholder="30m"
								/>

								<Text style={styles.inputHeader}>Max Views Interval:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setTasksMaxViewsInterval(content)}
									value={tasksMaxViewsInterval || ""}
									placeholderTextColor="#222c47"
									placeholder="30m"
								/>

								<Text style={styles.inputHeader}>Thumbnail Interval:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) =>
										setTasksThumbnailsInterval(content)
									}
									value={tasksThumbnailsInterval || ""}
									placeholderTextColor="#222c47"
									placeholder="5m"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* MFA */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>
									Multi-Factor Authentication
								</Text>

								<View style={styles.switchContainer}>
									<Switch
										value={mfaPasskeys || false}
										onValueChange={() => setMfaPasskeys((prev) => !prev)}
										thumbColor={mfaPasskeys ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Passkeys</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={mfaTotpEnabled || false}
										onValueChange={() => setMfaTotpEnabled((prev) => !prev)}
										thumbColor={mfaTotpEnabled ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Enable TOTP</Text>
								</View>

								<Text style={styles.inputHeader}>Issuer:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setMfaTotpIssuer(content)}
									value={mfaTotpIssuer || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* Features */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Features</Text>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresImageCompression || false}
										onValueChange={() =>
											setFeaturesImageCompression((prev) => !prev)
										}
										thumbColor={
											featuresImageCompression ? "#2e3e6b" : "#222c47"
										}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Image Compression</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresRobotsTxt || false}
										onValueChange={() => setFeaturesRobotsTxt((prev) => !prev)}
										thumbColor={featuresRobotsTxt ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>/robots.txt</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresHealthcheck || false}
										onValueChange={() =>
											setFeaturesHealthcheck((prev) => !prev)
										}
										thumbColor={featuresHealthcheck ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Healthcheck</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresUserRegistration || false}
										onValueChange={() =>
											setFeaturesUserRegistration((prev) => !prev)
										}
										thumbColor={
											featuresUserRegistration ? "#2e3e6b" : "#222c47"
										}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>User Registration</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresOauthRegistration || false}
										onValueChange={() =>
											setFeaturesOauthRegistration((prev) => !prev)
										}
										thumbColor={
											featuresOauthRegistration ? "#2e3e6b" : "#222c47"
										}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>OAuth Registration</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresDeleteOnMaxViews || false}
										onValueChange={() =>
											setFeaturesDeleteOnMaxViews((prev) => !prev)
										}
										thumbColor={
											featuresDeleteOnMaxViews ? "#2e3e6b" : "#222c47"
										}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Delete on Max Views</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresMetricsEnabled || false}
										onValueChange={() =>
											setFeaturesMetricsEnabled((prev) => !prev)
										}
										thumbColor={featuresMetricsEnabled ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Enable Metrics</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresMetricsAdminOnly || false}
										onValueChange={() =>
											setFeaturesMetricsAdminOnly((prev) => !prev)
										}
										thumbColor={
											featuresMetricsAdminOnly ? "#2e3e6b" : "#222c47"
										}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Admin Only Metrics</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresMetricsShowUserSpecific || false}
										onValueChange={() =>
											setFeaturesMetricsShowUserSpecific((prev) => !prev)
										}
										thumbColor={
											featuresMetricsShowUserSpecific ? "#2e3e6b" : "#222c47"
										}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>
										Show User Specific Metrics
									</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={featuresThumbnailsEnabled || false}
										onValueChange={() =>
											setFeaturesThumbnailsEnabled((prev) => !prev)
										}
										thumbColor={
											featuresThumbnailsEnabled ? "#2e3e6b" : "#222c47"
										}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>
										Enable Thumbnails
									</Text>
								</View>

								<Text style={styles.inputHeader}>
									Thumnails Number Threads:
								</Text>
								<TextInput
									keyboardType="numeric"
									style={styles.textInput}
									onChangeText={(content) =>
										setFeaturesThumbnailsNumberThreads(
											Math.abs(Number.parseInt(content)),
										)
									}
									value={
										featuresThumbnailsNumberThreads
											? String(featuresThumbnailsNumberThreads) || ""
											: ""
									}
									placeholderTextColor="#222c47"
									placeholder="4"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* Files */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Files</Text>

								<Text style={styles.inputHeader}>Route:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFilesRoute(content)}
									value={filesRoute || ""}
									placeholderTextColor="#222c47"
									placeholder="/u"
								/>

								<Text style={styles.inputHeader}>Length:</Text>
								<TextInput
									keyboardType="numeric"
									style={styles.textInput}
									onChangeText={(content) =>
										setFilesLength(Math.abs(Number.parseInt(content)))
									}
									value={filesLength ? String(filesLength) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="6"
								/>

								<View style={styles.switchContainer}>
									<Switch
										value={filesAssumeMimetypes || false}
										onValueChange={() =>
											setFilesAssumeMimetypes((prev) => !prev)
										}
										thumbColor={filesAssumeMimetypes ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Assume Mimetypes</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={filesRemoveGpsMetadata || false}
										onValueChange={() =>
											setFilesRemoveGpsMetadata((prev) => !prev)
										}
										thumbColor={filesRemoveGpsMetadata ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Remove GPS Metadata</Text>
								</View>

								<Text style={styles.inputHeader}>Default Format:</Text>
								<Select
									data={formats}
									onSelect={(selectedFormat) =>
										setFilesDefaultFormat(
											selectedFormat.value as typeof filesDefaultFormat,
										)
									}
									placeholder="Select format..."
									defaultValue={formats.find(
										(format) => format.value === "random",
									)}
								/>

								<Text style={styles.inputHeader}>Disabled Extensions:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) =>
										setFilesDisabledExtensions(content.split(", "))
									}
									value={filesDisabledExtensions?.join(", ") || ""}
									placeholderTextColor="#222c47"
									placeholder="exe, bat, sh"
								/>

								<Text style={styles.inputHeader}>Max File Size:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFilesMaxFileSize(content)}
									value={filesMaxFileSize || ""}
									placeholderTextColor="#222c47"
									placeholder="100mb"
								/>

								<Text style={styles.inputHeader}>Default Expiration:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFilesDefaultExpiration(content)}
									value={filesDefaultExpiration || ""}
									placeholderTextColor="#222c47"
									placeholder="30d"
								/>

								<Text style={styles.inputHeader}>Default Date Format:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFilesDefaultDateFormat(content)}
									value={filesDefaultDateFormat || ""}
									placeholderTextColor="#222c47"
									placeholder="YYYY-MM-DD_HH:mm:ss"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* Url Shortener */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>URL Shortener</Text>

								<Text style={styles.inputHeader}>Route:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setUrlsRoute(content)}
									value={urlsRoute || ""}
									placeholderTextColor="#222c47"
									placeholder="/go"
								/>

								<Text style={styles.inputHeader}>Length:</Text>
								<TextInput
									keyboardType="numeric"
									style={styles.textInput}
									onChangeText={(content) =>
										setUrlsLength(Math.abs(Number.parseInt(content)))
									}
									value={urlsLength ? String(urlsLength) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="6"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* Invites */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Invites</Text>

								<View style={styles.switchContainer}>
									<Switch
										value={invitesEnabled || false}
										onValueChange={() => setInvitesEnabled((prev) => !prev)}
										thumbColor={invitesEnabled ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Enable Invites</Text>
								</View>

								<Text style={styles.inputHeader}>Length:</Text>
								<TextInput
									keyboardType="numeric"
									style={styles.textInput}
									onChangeText={(content) =>
										setInvitesLength(Math.abs(Number.parseInt(content)))
									}
									value={invitesLength ? String(invitesLength) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="6"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* Ratelimit */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Ratelimit</Text>

								<View style={styles.switchContainer}>
									<Switch
										value={ratelimitEnabled || false}
										onValueChange={() => setRatelimitEnabled((prev) => !prev)}
										thumbColor={ratelimitEnabled ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Enable Ratelimit</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={ratelimitAdminBypass || false}
										onValueChange={() =>
											setRatelimitAdminBypass((prev) => !prev)
										}
										thumbColor={ratelimitAdminBypass ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Admin Bypass</Text>
								</View>

								<Text style={styles.inputHeader}>Max Requests:</Text>
								<TextInput
									keyboardType="numeric"
									style={styles.textInput}
									onChangeText={(content) =>
										setRatelimitMax(Math.abs(Number.parseInt(content)))
									}
									value={ratelimitMax ? String(ratelimitMax) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="10"
								/>

								<Text style={styles.inputHeader}>Window:</Text>
								<TextInput
									keyboardType="numeric"
									style={styles.textInput}
									onChangeText={(content) =>
										setRatelimitWindow(Math.abs(Number.parseInt(content)))
									}
									value={ratelimitWindow ? String(ratelimitWindow) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="60"
								/>

								<Text style={styles.inputHeader}>Allow List:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) =>
										setRatelimitAllowList(content.split(", "))
									}
									value={ratelimitAllowList?.join(", ") || ""}
									placeholderTextColor="#222c47"
									placeholder="1.1.1.1, 8.8.8.8"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* Website */}
							<View style={styles.settingGroup}>
								<Text style={styles.inputHeader}>Title:</Text>
								<TextInput
									keyboardType="url"
									style={styles.textInput}
									onChangeText={(content) => setWebsiteTitle(content)}
									value={websiteTitle || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Text style={styles.inputHeader}>Title Logo:</Text>
								<TextInput
									keyboardType="url"
									style={styles.textInput}
									onChangeText={(content) => setWebsiteTitleLogo(content)}
									value={websiteTitleLogo || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/logo.png"
								/>

								<Text style={styles.inputHeader}>External Links:</Text>
								<TextInput
									keyboardType="url"
									style={{
										...styles.textInput,
										...styles.multilneTextInput,
									}}
									multiline
									onChangeText={(content) => setWebsiteExternalLinks(content)}
									value={websiteExternalLinks || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/logo.png"
								/>

								<Text style={styles.inputHeader}>Login Background:</Text>
								<TextInput
									keyboardType="url"
									style={styles.textInput}
									onChangeText={(content) => setWebsiteLoginBackground(content)}
									value={websiteLoginBackground || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/background.png"
								/>

								<View style={styles.switchContainer}>
									<Switch
										value={websiteLoginBackgroundBlur || false}
										onValueChange={() =>
											setWebsiteLoginBackgroundBlur((prev) => !prev)
										}
										thumbColor={
											websiteLoginBackgroundBlur ? "#2e3e6b" : "#222c47"
										}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Login Background Blur</Text>
								</View>

								<Text style={styles.inputHeader}>Default Avatar:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteDefaultAvatar(content)}
									value={websiteDefaultAvatar || ""}
									placeholderTextColor="#222c47"
									placeholder="/zipline/avatar.png"
								/>

								<Text style={styles.inputHeader}>Terms of Service:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteTos(content)}
									value={websiteTos || ""}
									placeholderTextColor="#222c47"
									placeholder="/zipline/TOS.md"
								/>

								<Text style={styles.inputHeader}>Default Theme:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteThemeDefault(content)}
									value={websiteThemeDefault || ""}
									placeholderTextColor="#222c47"
									placeholder="system"
								/>

								<Text style={styles.inputHeader}>Dark Theme:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteThemeDark(content)}
									value={websiteThemeDark || ""}
									placeholderTextColor="#222c47"
									placeholder="builtin:dark_gray"
								/>

								<Text style={styles.inputHeader}>Light Theme:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteThemeLight(content)}
									value={websiteThemeLight || ""}
									placeholderTextColor="#222c47"
									placeholder="builtin:light_gray"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* OAuth */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>OAuth</Text>

								<View style={styles.switchContainer}>
									<Switch
										value={oauthBypassLocalLogin || false}
										onValueChange={() =>
											setOauthBypassLocalLogin((prev) => !prev)
										}
										thumbColor={oauthBypassLocalLogin ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Bypass Local Login</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={oauthLoginOnly || false}
										onValueChange={() => setOauthLoginOnly((prev) => !prev)}
										thumbColor={oauthLoginOnly ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>Login Only</Text>
								</View>

								<View style={styles.settingGroup}>
									<Text
										style={{
											...styles.oauthSubSettingText,
											...styles.oauthSubSettingTextColored,
										}}
									>
										Discord
									</Text>

									<Text style={styles.inputHeader}>Discord Client ID:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setOauthDiscordClientId(content)}
										value={oauthDiscordClientId || ""}
									/>

									<Text style={styles.inputHeader}>Discord Client Secret:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) =>
											setOauthDiscordClientSecret(content)
										}
										value={oauthDiscordClientSecret || ""}
									/>

									<Text style={styles.inputHeader}>Discord Redirect URI:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) =>
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

									<Text style={styles.inputHeader}>Google Client ID:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setOauthGoogleClientId(content)}
										value={oauthGoogleClientId || ""}
									/>

									<Text style={styles.inputHeader}>Google Client Secret:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) =>
											setOauthGoogleClientSecret(content)
										}
										value={oauthGoogleClientSecret || ""}
									/>

									<Text style={styles.inputHeader}>Google Redirect URI:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) =>
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

									<Text style={styles.inputHeader}>GitHub Client ID:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setOauthGithubClientId(content)}
										value={oauthGithubClientId || ""}
									/>

									<Text style={styles.inputHeader}>GitHub Client Secret:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) =>
											setOauthGithubClientSecret(content)
										}
										value={oauthGithubClientSecret || ""}
									/>

									<Text style={styles.inputHeader}>GitHub Redirect URI:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) =>
											setOauthGithubRedirectUri(content)
										}
										value={oauthGithubRedirectUri || ""}
									/>
								</View>

								<View style={styles.settingGroup}>
									<Text style={styles.oauthSubSettingText}>OpenID Connect</Text>

									<Text style={styles.inputHeader}>OIDC Client ID:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setOauthOidcClientId(content)}
										value={oauthOidcClientId || ""}
									/>

									<Text style={styles.inputHeader}>OIDC Client Secret:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) =>
											setOauthOidcClientSecret(content)
										}
										value={oauthOidcClientSecret || ""}
									/>

									<Text style={styles.inputHeader}>OIDC Authorize URL:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) =>
											setOauthOidcAuthorizeUrl(content)
										}
										value={oauthOidcAuthorizeUrl || ""}
									/>

									<Text style={styles.inputHeader}>OIDC Token URL:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setOauthOidcTokenUrl(content)}
										value={oauthOidcTokenUrl || ""}
									/>

									<Text style={styles.inputHeader}>OIDC Userinfo URL:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setOauthOidcUserinfoUrl(content)}
										value={oauthOidcUserinfoUrl || ""}
									/>

									<Text style={styles.inputHeader}>OIDC Redirect URL:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setOauthOidcRedirectUri(content)}
										value={oauthOidcRedirectUri || ""}
									/>
								</View>
							</View>

							{/* PWA */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>PWA</Text>

								<View style={styles.switchContainer}>
									<Switch
										value={pwaEnabled || false}
										onValueChange={() => setPwaEnabled((prev) => !prev)}
										thumbColor={pwaEnabled ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text style={styles.switchText}>PWA Enabled</Text>
								</View>

								<Text style={styles.inputHeader}>Title:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setPwaTitle(content)}
									value={pwaTitle || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Text style={styles.inputHeader}>Short Name:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setPwaShortName(content)}
									value={pwaShortName || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Text style={styles.inputHeader}>Description:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setPwaDescription(content)}
									value={pwaDescription || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Text style={styles.inputHeader}>Theme Color:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setPwaThemeColor(content)}
									value={pwaThemeColor || ""}
									placeholderTextColor="#222c47"
									placeholder="#000000"
								/>

								<Text style={styles.inputHeader}>Background Color:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setPwaBackgroundColor(content)}
									value={pwaBackgroundColor || ""}
									placeholderTextColor="#222c47"
									placeholder="#000000"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* HTTP Webhooks */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>HTTP Webhooks</Text>

								<Text style={styles.inputHeader}>On Upload:</Text>
								<TextInput
									keyboardType="url"
									style={styles.textInput}
									onChangeText={(content) => setHttpWebhookOnUpload(content)}
									value={httpWebhookOnUpload || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/upload"
								/>

								<Text style={styles.inputHeader}>On Shorten:</Text>
								<TextInput
									keyboardType="url"
									style={styles.textInput}
									onChangeText={(content) => setHttpWebhookOnShorten(content)}
									value={httpWebhookOnShorten || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/shorten"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* Discord Webhook */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Discord Webhook</Text>

								<Text style={styles.inputHeader}>Webhook URL:</Text>
								<TextInput
									keyboardType="url"
									style={styles.textInput}
									onChangeText={(content) => setDiscordWebhookUrl(content)}
									value={discordWebhookUrl || ""}
									placeholderTextColor="#222c47"
									placeholder="https://discord.com/api/webhooks/..."
								/>

								<Text style={styles.inputHeader}>Username:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setDiscordUsername(content)}
									value={discordUsername || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Text style={styles.inputHeader}>Avatar URL:</Text>
								<TextInput
									keyboardType="url"
									style={styles.textInput}
									onChangeText={(content) => setDiscordAvatarUrl(content)}
									value={discordAvatarUrl || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/avatar.png"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>

								{/* On Upload */}
								<View style={styles.settingGroup}>
									<Text style={styles.headerText}>On Upload</Text>

									<Text style={styles.inputHeader}>Webhook URL:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setDiscordOnUploadWebhookUrl(content)}
										value={discordOnUploadWebhookUrl || ""}
										placeholderTextColor="#222c47"
										placeholder="https://discord.com/api/webhooks/..."
									/>

									<Text style={styles.inputHeader}>Username:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setDiscordOnUploadUsername(content)}
										value={discordOnUploadUsername || ""}
										placeholderTextColor="#222c47"
										placeholder="Zipline Uploads"
									/>

									<Text style={styles.inputHeader}>Avatar URL:</Text>
									<TextInput
										keyboardType="url"
										style={styles.textInput}
										onChangeText={(content) => setDiscordOnUploadAvatarUrl(content)}
										value={discordOnUploadAvatarUrl || ""}
										placeholderTextColor="#222c47"
										placeholder="https://example.com/uploadAvatar.png"
									/>

									<Text style={styles.inputHeader}>Content:</Text>
									<TextInput
										style={{
											...styles.textInput,
											...styles.multilneTextInput
										}}
										multiline
										onChangeText={(content) => setDiscordOnUploadContent(content)}
										value={discordOnUploadContent || ""}
									/>

									<View style={styles.switchContainer}>
										<Switch
											value={!!discordOnUploadEmbed || false}
											onValueChange={() => setDiscordOnUploadEmbed((prev) => {
												if (prev) return null;
												
												return defaultUploadEmbed;
											})}
											thumbColor={discordOnUploadEmbed ? "#2e3e6b" : "#222c47"}
											trackColor={{
												true: "#21273b",
												false: "#181c28",
											}}
										/>
										<Text style={styles.switchText}>Embed</Text>
									</View>

									{/* On Upload Embed */}
									<View style={{
										...styles.settingGroup,
										...(!discordOnUploadEmbed && { display: "none" }),
									}}>
										<Text style={styles.inputHeader}>Title:</Text>
										<TextInput
											style={styles.textInput}
											onChangeText={(content) => setDiscordOnUploadEmbed((embed) => ({
												...embed,
												title: content
											} as UploadEmbed))}
											value={discordOnUploadEmbed?.title || ""}
										/>

										<Text style={styles.inputHeader}>Description:</Text>
										<TextInput
											style={styles.textInput}
											onChangeText={(content) => setDiscordOnUploadEmbed((embed) => ({
												...embed,
												description: content
											} as UploadEmbed))}
											value={discordOnUploadEmbed?.description || ""}
										/>

										<Text style={styles.inputHeader}>Footer:</Text>
										<TextInput
											style={styles.textInput}
											onChangeText={(content) => setDiscordOnUploadEmbed((embed) => ({
												...embed,
												footer: content
											} as UploadEmbed))}
											value={discordOnUploadEmbed?.footer || ""}
										/>

										<Text style={styles.inputHeader}>Color:</Text>
										<TextInput
											style={styles.textInput}
											onChangeText={(content) => setDiscordOnUploadEmbed((embed) => ({
												...embed,
												color: content
											} as UploadEmbed))}
											value={discordOnUploadEmbed?.color || ""}
										/>

										<View style={styles.switchContainer}>
											<Switch
												value={!!discordOnUploadEmbed?.thumbnail || false}
												onValueChange={() => setDiscordOnUploadEmbed((embed) => ({
													...embed,
													thumbnail: !embed?.thumbnail
												} as UploadEmbed))}
												thumbColor={discordOnUploadEmbed?.thumbnail ? "#2e3e6b" : "#222c47"}
												trackColor={{
													true: "#21273b",
													false: "#181c28",
												}}
											/>
											<Text style={styles.switchText}>Thumbnail</Text>
										</View>

										<View style={styles.switchContainer}>
											<Switch
												value={!!discordOnUploadEmbed?.imageOrVideo || false}
												onValueChange={() => setDiscordOnUploadEmbed((embed) => ({
													...embed,
													imageOrVideo: !embed?.imageOrVideo
												} as UploadEmbed))}
												thumbColor={discordOnUploadEmbed?.imageOrVideo ? "#2e3e6b" : "#222c47"}
												trackColor={{
													true: "#21273b",
													false: "#181c28",
												}}
											/>
											<Text style={styles.switchText}>Image/Video</Text>
										</View>

										<View style={styles.switchContainer}>
											<Switch
												value={!!discordOnUploadEmbed?.timestamp || false}
												onValueChange={() => setDiscordOnUploadEmbed((embed) => ({
													...embed,
													timestamp: !embed?.timestamp
												} as UploadEmbed))}
												thumbColor={discordOnUploadEmbed?.timestamp ? "#2e3e6b" : "#222c47"}
												trackColor={{
													true: "#21273b",
													false: "#181c28",
												}}
											/>
											<Text style={styles.switchText}>Timestamp</Text>
										</View>

										<View style={styles.switchContainer}>
											<Switch
												value={!!discordOnUploadEmbed?.url || false}
												onValueChange={() => setDiscordOnUploadEmbed((embed) => ({
													...embed,
													url: !embed?.url
												} as UploadEmbed))}
												thumbColor={discordOnUploadEmbed?.url ? "#2e3e6b" : "#222c47"}
												trackColor={{
													true: "#21273b",
													false: "#181c28",
												}}
											/>
											<Text style={styles.switchText}>URL</Text>
										</View>
									</View>

									<Pressable style={styles.settingSaveButton}>
										<Text style={styles.settingSaveButtonText}>Save</Text>
									</Pressable>
								</View>

								{/* On Shorten */}
								<View style={styles.settingGroup}>
									<Text style={styles.headerText}>On Shorten</Text>

									<Text style={styles.inputHeader}>Webhook URL:</Text>
									<TextInput
										keyboardType="url"
										style={styles.textInput}
										onChangeText={(content) => setDiscordOnShortenWebhookUrl(content)}
										value={discordOnShortenWebhookUrl || ""}
										placeholderTextColor="#222c47"
										placeholder="https://discord.com/api/webhooks/..."
									/>

									<Text style={styles.inputHeader}>Username:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setDiscordOnShortenUsername(content)}
										value={discordOnShortenUsername || ""}
										placeholderTextColor="#222c47"
										placeholder="Zipline Shortens"
									/>

									<Text style={styles.inputHeader}>Avatar URL:</Text>
									<TextInput
										style={styles.textInput}
										onChangeText={(content) => setDiscordOnShortenAvatarUrl(content)}
										value={discordOnShortenAvatarUrl || ""}
										placeholderTextColor="#222c47"
										placeholder="https://example.com/shortenAvatar.png"
									/>

									<Text style={styles.inputHeader}>Content:</Text>
									<TextInput
										style={{
											...styles.textInput,
											...styles.multilneTextInput
										}}
										multiline
										onChangeText={(content) => setDiscordOnShortenContent(content)}
										value={discordOnShortenContent || ""}
									/>

									<View style={styles.switchContainer}>
										<Switch
											value={!!discordOnShortenEmbed}
											onValueChange={() => setDiscordOnShortenEmbed((prev) => {
												if (prev) return null;
												
												return defaultShortenEmbed;
											})}
											thumbColor={discordOnShortenEmbed ? "#2e3e6b" : "#222c47"}
											trackColor={{
												true: "#21273b",
												false: "#181c28",
											}}
										/>
										<Text style={styles.switchText}>Embed</Text>
									</View>

									{/* On Shorten Embed */}
									<View style={{
										...styles.settingGroup,
										...(!discordOnShortenEmbed && { display: "none" }),
									}}>
										<Text style={styles.inputHeader}>Title:</Text>
										<TextInput
											style={styles.textInput}
											onChangeText={(content) => setDiscordOnShortenEmbed((embed) => ({
												...embed,
												title: content
											} as ShortenEmbed))}
											value={discordOnShortenEmbed?.title || ""}
										/>

										<Text style={styles.inputHeader}>Description:</Text>
										<TextInput
											style={styles.textInput}
											onChangeText={(content) => setDiscordOnShortenEmbed((embed) => ({
												...embed,
												description: content
											} as ShortenEmbed))}
											value={discordOnShortenEmbed?.description || ""}
										/>

										<Text style={styles.inputHeader}>Footer:</Text>
										<TextInput
											style={styles.textInput}
											onChangeText={(content) => setDiscordOnShortenEmbed((embed) => ({
												...embed,
												footer: content
											} as ShortenEmbed))}
											value={discordOnShortenEmbed?.footer || ""}
										/>

										<Text style={styles.inputHeader}>Color:</Text>
										<TextInput
											style={styles.textInput}
											onChangeText={(content) => setDiscordOnShortenEmbed((embed) => ({
												...embed,
												color: content
											} as ShortenEmbed))}
											value={discordOnShortenEmbed?.color || ""}
										/>

										<View style={styles.switchContainer}>
											<Switch
												value={!!discordOnShortenEmbed?.thumbnail || false}
												onValueChange={() => setDiscordOnShortenEmbed((embed) => ({
													...embed,
													thumbnail: !embed?.thumbnail
												} as ShortenEmbed))}
												thumbColor={discordOnShortenEmbed?.thumbnail ? "#2e3e6b" : "#222c47"}
												trackColor={{
													true: "#21273b",
													false: "#181c28",
												}}
											/>
											<Text style={styles.switchText}>Thumbnail</Text>
										</View>

										<View style={styles.switchContainer}>
											<Switch
												value={!!discordOnShortenEmbed?.imageOrVideo || false}
												onValueChange={() => setDiscordOnShortenEmbed((embed) => ({
													...embed,
													imageOrVideo: !embed?.imageOrVideo
												} as ShortenEmbed))}
												thumbColor={discordOnShortenEmbed?.imageOrVideo ? "#2e3e6b" : "#222c47"}
												trackColor={{
													true: "#21273b",
													false: "#181c28",
												}}
											/>
											<Text style={styles.switchText}>Image/Video</Text>
										</View>

										<View style={styles.switchContainer}>
											<Switch
												value={!!discordOnShortenEmbed?.timestamp || false}
												onValueChange={() => setDiscordOnShortenEmbed((embed) => ({
													...embed,
													timestamp: !embed?.timestamp
												} as ShortenEmbed))}
												thumbColor={discordOnShortenEmbed?.timestamp ? "#2e3e6b" : "#222c47"}
												trackColor={{
													true: "#21273b",
													false: "#181c28",
												}}
											/>
											<Text style={styles.switchText}>Timestamp</Text>
										</View>

										<View style={styles.switchContainer}>
											<Switch
												value={!!discordOnShortenEmbed?.url || false}
												onValueChange={() => setDiscordOnShortenEmbed((embed) => ({
													...embed,
													url: !embed?.url
												} as ShortenEmbed))}
												thumbColor={discordOnShortenEmbed?.url ? "#2e3e6b" : "#222c47"}
												trackColor={{
													true: "#21273b",
													false: "#181c28",
												}}
											/>
											<Text style={styles.switchText}>URL</Text>
										</View>
									</View>

									<Pressable style={styles.settingSaveButton}>
										<Text style={styles.settingSaveButtonText}>Save</Text>
									</Pressable>
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

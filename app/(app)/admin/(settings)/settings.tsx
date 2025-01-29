import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import type { APISettings, ExternalLink, ShortenEmbed, UploadEmbed } from "@/types/zipline";
import { useState, useEffect, Fragment } from "react";
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
import { defaultUploadEmbed, defaultShortenEmbed, type Setting, type SettingTypes } from "@/constants/adminSettings";
import React from "react";
import { getObjectValue, setObjectValue } from "@/functions/util";
import { settings as ziplineSettings } from "@/constants/adminSettings";

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
	const [urlsRoute, setUrlsRoute] = useState<string | undefined>(undefined);
	const [urlsLength, setUrlsLength] = useState<number | undefined>(undefined);
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
	const [invitesLength, setInvitesLength] = useState<number | undefined>(undefined);
	const [websiteTitle, setWebsiteTitle] = useState<string | undefined>(undefined);
	const [websiteTitleLogo, setWebsiteTitleLogo] = useState<string | null>(null);
	const [websiteExternalLinks, setWebsiteExternalLinks] = useState<
		string | undefined
	>(undefined);
	const [originalwebsiteExternalLinks, setOriginalWebsiteExternalLinks] = useState<
		Array<ExternalLink> | undefined
	>(undefined);
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
	const [websiteThemeDefault, setWebsiteThemeDefault] = useState<string | undefined>(
		undefined,
	);
	const [websiteThemeDark, setWebsiteThemeDark] = useState<string | undefined>(undefined);
	const [websiteThemeLight, setWebsiteThemeLight] = useState<string | undefined>(
		undefined,
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
	const [ratelimitMax, setRatelimitMax] = useState<number | undefined>(undefined);
	const [ratelimitWindow, setRatelimitWindow] = useState<number | null>(null);
	const [ratelimitAdminBypass, setRatelimitAdminBypass] = useState<
		boolean
	>(false);
	const [ratelimitAllowList, setRatelimitAllowList] =
		useState<Array<string> | undefined>(undefined);
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
	const [pwaTitle, setPwaTitle] = useState<string | undefined>(undefined);
	const [pwaShortName, setPwaShortName] = useState<string | undefined>(undefined);
	const [pwaDescription, setPwaDescription] = useState<string | undefined>(undefined);
	const [pwaThemeColor, setPwaThemeColor] = useState<string | undefined>(undefined);
	const [pwaBackgroundColor, setPwaBackgroundColor] = useState<string | undefined>(
		undefined,
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

				break;
			}

			case "urlShortener": {
				saveSettings = {
					urlsRoute,
					urlsLength
				}

				break;
			}

			case "invites": {
				saveSettings = {
					invitesEnabled,
					invitesLength
				}

				break;
			}

			case "ratelimit": {
				saveSettings = {
					ratelimitEnabled,
					ratelimitMax,
					ratelimitWindow,
					ratelimitAdminBypass,
					ratelimitAllowList
				}

				break;
			}

			case "website": {
				let externalLinks = originalwebsiteExternalLinks

				try {
					externalLinks = JSON.parse(websiteExternalLinks || "")
				} catch(e) {}

				saveSettings = {
					websiteTitle,
					websiteTitleLogo,
					websiteExternalLinks: externalLinks,
					websiteLoginBackground,
					websiteLoginBackgroundBlur,
					websiteDefaultAvatar,
					websiteTos,
					websiteThemeDefault,
					websiteThemeDark,
					websiteThemeLight
				}

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
					oauthOidcRedirectUri
				}

				break;
			}

			case "pwa": {
				saveSettings = {
					pwaEnabled,
					pwaTitle,
					pwaShortName,
					pwaDescription,
					pwaThemeColor,
					pwaBackgroundColor
				}

				break;
			}

			case "httpWebhooks": {
				saveSettings = {
					httpWebhookOnUpload,
					httpWebhookOnShorten
				}

				break;
			}

			case "discordWebhook": {
				saveSettings = {
					discordWebhookUrl,
					discordUsername,
					discordAvatarUrl
				}

				break;
			}

			case "discordOnShortenWebhook": {
				saveSettings = {
					discordOnShortenWebhookUrl,
					discordOnShortenUsername,
					discordOnShortenAvatarUrl,
					discordOnShortenContent,
					discordOnShortenEmbed
				}

				break;
			}

			case "discordOnUploadWebhook": {
				saveSettings = {
					discordOnUploadWebhookUrl,
					discordOnUploadUsername,
					discordOnUploadAvatarUrl,
					discordOnUploadContent,
					discordOnUploadEmbed
				}

				break;
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

	function renderSetting(setting: Setting) {
		if (!settings) return <View />

		switch(setting.type) {
			case "category": {
				return (
					<View key={setting.name} style={styles.settingsContainer}>
						<Text style={styles.headerText}>{setting.name}</Text>

						{setting.children.map(child => renderSetting(child))}
					</View>
				)
			}

			case "input": {
				const value = getObjectValue<APISettings>(settings, setting.setting)

				return (
					<Fragment key={setting.setting}>
						<Text style={styles.inputHeader}>{setting.name}:</Text>
						<TextInput
							style={{
								...styles.textInput,
								...(setting.multiline && styles.multilneTextInput)
							}}
							onChangeText={(content) => setSettings(currentSettings => {
								if (!currentSettings) return currentSettings;

								let newValue: SettingTypes = content

								switch(setting.setType) {
									case "array": {
										if (setting.joinString) newValue = newValue.split(setting.joinString);

										break;
									}

									case "number": {
										newValue = Number.parseInt(newValue)

										break;
									}
								}

								const newSettings = setObjectValue<APISettings>(currentSettings, setting.setting, newValue)

								return newSettings
							})}
							value={String(value) || ""}
							placeholderTextColor="#222c47"
							multiline={setting.multiline}
							placeholder={setting.placeholder}
						/>
					</Fragment>
				)
			}

			case "switch": {
				const value = getObjectValue<APISettings>(settings, setting.setting)

				return (
					<View key={setting.setting} style={styles.switchContainer}>
						<Switch
							value={!!value || false}
							onValueChange={() =>
								setSettings(currentSettings => {
									if (!currentSettings) return currentSettings;

									const currentValue = getObjectValue<APISettings>(currentSettings, setting.setting)

									let newValue: SettingTypes = !currentValue

									switch(setting.setType) {
										case "shorten": {
											newValue = currentValue ? null : defaultShortenEmbed

											break;
										}

										case "upload": {
											newValue = currentValue ? null : defaultUploadEmbed
										}
									}

									const newSettings = setObjectValue<APISettings>(currentSettings, setting.setting, newValue)

									return newSettings;
								})
							}
							thumbColor={value ? "#2e3e6b" : "#222c47"}
							trackColor={{
								true: "#21273b",
								false: "#181c28",
							}}
						/>
						<Text style={styles.switchText}>{setting.name}</Text>
					</View>
				)
			}

			case "select": {
				return (
					<Fragment key={setting.setting}>
						<Text style={styles.inputHeader}>{setting.name}</Text>
						<Select
							data={setting.options}
							onSelect={(selectedFormat) =>
								setSettings(currentSettings => {
									if (!currentSettings) return currentSettings

									const newSettings = setObjectValue<APISettings>(currentSettings, setting.setting, selectedFormat.value)

									return newSettings;
								})
							}
							placeholder="Select..."
							defaultValue={formats.find(
								(format) => format.value === setting.defaultValue,
							)}
						/>
					</Fragment>
				)
			}

			case "save": {
				return (
					<Pressable key={setting.category} style={styles.settingSaveButton} onPress={() => handleSave(setting.category)}>
						<Text style={styles.settingSaveButtonText}>Save</Text>
					</Pressable>
				)
			}
		}
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				{settings ? (
					<View style={styles.settingsContainer}>
						<Text style={styles.headerText}>Server Settings</Text>
						<KeyboardAwareScrollView style={styles.scrollView}>
							{ziplineSettings.map(setting => renderSetting(setting))}
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

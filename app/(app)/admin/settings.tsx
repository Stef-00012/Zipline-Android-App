import { getSettings, updateSettings } from "@/functions/zipline/settings";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { settings as zlSettings } from "@/constants/adminSettings";
import { convertToBytes, convertToTime } from "@/functions/util";
import { useShareIntent } from "@/hooks/useShareIntent";
import { View, Text, ToastAndroid } from "react-native";
import type { APISettings } from "@/types/zipline";
import { styles } from "@/styles/admin/settings";
import TextInput from "@/components/TextInput";
import { useState, useEffect } from "react";
import { useAuth } from "@/hooks/useAuth";
import Select from "@/components/Select";
import Switch from "@/components/Switch";
import Button from "@/components/Button";
import type {
	SaveCategories,
	SaveSettings,
	Setting,
} from "@/constants/adminSettings";

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

	const [saveSettings, setSaveSettings] = useState<SaveSettings | null>(null);

	useEffect(() => {
		if (settings) {
			setSaveSettings({
				coreReturnHttpsUrls: settings.coreReturnHttpsUrls,
				coreDefaultDomain: settings.coreDefaultDomain,
				coreTempDirectory: settings.coreTempDirectory,

				chunksEnabled: settings.chunksEnabled,
				chunksMax:
					convertToBytes(settings.chunksMax, {
						unitSeparator: " ",
					}) || settings.chunksMax,
				chunksSize:
					convertToBytes(settings.chunksSize, {
						unitSeparator: " ",
					}) || settings.chunksSize,

				tasksDeleteInterval:
					convertToTime(settings.tasksDeleteInterval, {
						useAbbreviations: true,
					}) || settings.tasksDeleteInterval,
				tasksClearInvitesInterval:
					convertToTime(settings.tasksClearInvitesInterval, {
						useAbbreviations: true,
					}) || settings.tasksClearInvitesInterval,
				tasksMaxViewsInterval:
					convertToTime(settings.tasksMaxViewsInterval, {
						useAbbreviations: true,
					}) || settings.tasksMaxViewsInterval,
				tasksThumbnailsInterval:
					convertToTime(settings.tasksThumbnailsInterval, {
						useAbbreviations: true,
					}) || settings.tasksThumbnailsInterval,
				tasksMetricsInterval:
					convertToTime(settings.tasksMetricsInterval, {
						useAbbreviations: true,
					}) || settings.tasksMetricsInterval,

				mfaPasskeys: settings.mfaPasskeys,
				mfaTotpEnabled: settings.mfaTotpEnabled,
				mfaTotpIssuer: settings.mfaTotpIssuer,

				featuresImageCompression: settings.featuresImageCompression,
				featuresRobotsTxt: settings.featuresRobotsTxt,
				featuresHealthcheck: settings.featuresHealthcheck,
				featuresUserRegistration: settings.featuresUserRegistration,
				featuresOauthRegistration: settings.featuresOauthRegistration,
				featuresDeleteOnMaxViews: settings.featuresDeleteOnMaxViews,
				featuresMetricsEnabled: settings.featuresMetricsEnabled,
				featuresMetricsAdminOnly: settings.featuresMetricsAdminOnly,
				featuresMetricsShowUserSpecific:
					settings.featuresMetricsShowUserSpecific,
				featuresThumbnailsEnabled: settings.featuresThumbnailsEnabled,
				featuresThumbnailsNumberThreads:
					settings.featuresThumbnailsNumberThreads,

				filesRoute: settings.filesRoute,
				filesLength: settings.filesLength,
				filesAssumeMimetypes: settings.filesAssumeMimetypes,
				filesRemoveGpsMetadata: settings.filesRemoveGpsMetadata,
				filesDefaultFormat: settings.filesDefaultFormat,
				filesDisabledExtensions: settings.filesDisabledExtensions.join(", "),
				filesMaxFileSize:
					convertToBytes(settings.filesMaxFileSize, {
						unitSeparator: " ",
					}) || settings.filesMaxFileSize,
				filesDefaultExpiration: settings.filesDefaultExpiration,
				filesDefaultDateFormat: settings.filesDefaultDateFormat,

				urlsRoute: settings.urlsRoute,
				urlsLength: settings.urlsLength,

				invitesEnabled: settings.invitesEnabled,
				invitesLength: settings.invitesLength,

				ratelimitEnabled: settings.ratelimitEnabled,
				ratelimitAdminBypass: settings.ratelimitAdminBypass,
				ratelimitMax: settings.ratelimitMax,
				ratelimitWindow: settings.ratelimitWindow,
				ratelimitAllowList: settings.ratelimitAllowList.join(", "),

				websiteTitle: settings.websiteTitle,
				websiteTitleLogo: settings.websiteTitleLogo,
				websiteExternalLinks:
					JSON.stringify(settings.websiteExternalLinks, null, 2) || "",
				websiteLoginBackground: settings.websiteLoginBackground,
				websiteLoginBackgroundBlur: settings.websiteLoginBackgroundBlur,
				websiteDefaultAvatar: settings.websiteDefaultAvatar,
				websiteTos: settings.websiteTos,
				websiteThemeDefault: settings.websiteThemeDefault,
				websiteThemeDark: settings.websiteThemeDark,
				websiteThemeLight: settings.websiteThemeLight,

				oauthBypassLocalLogin: settings.oauthBypassLocalLogin,
				oauthLoginOnly: settings.oauthLoginOnly,

				oauthDiscordClientId: settings.oauthDiscordClientId,
				oauthDiscordClientSecret: settings.oauthDiscordClientSecret,
				oauthDiscordRedirectUri: settings.oauthDiscordRedirectUri,

				oauthGoogleClientId: settings.oauthGoogleClientId,
				oauthGoogleClientSecret: settings.oauthGoogleClientSecret,
				oauthGoogleRedirectUri: settings.oauthGoogleRedirectUri,

				oauthGithubClientId: settings.oauthGithubClientId,
				oauthGithubClientSecret: settings.oauthGithubClientSecret,
				oauthGithubRedirectUri: settings.oauthGithubRedirectUri,

				oauthOidcClientId: settings.oauthOidcClientId,
				oauthOidcClientSecret: settings.oauthOidcClientSecret,
				oauthOidcAuthorizeUrl: settings.oauthOidcAuthorizeUrl,
				oauthOidcTokenUrl: settings.oauthOidcTokenUrl,
				oauthOidcUserinfoUrl: settings.oauthOidcUserinfoUrl,
				oauthOidcRedirectUri: settings.oauthOidcRedirectUri,

				pwaEnabled: settings.pwaEnabled,
				pwaTitle: settings.pwaTitle,
				pwaShortName: settings.pwaShortName,
				pwaDescription: settings.pwaDescription,
				pwaThemeColor: settings.pwaThemeColor,
				pwaBackgroundColor: settings.pwaBackgroundColor,

				httpWebhookOnUpload: settings.httpWebhookOnUpload,
				httpWebhookOnShorten: settings.httpWebhookOnShorten,

				discordWebhookUrl: settings.discordWebhookUrl,
				discordUsername: settings.discordUsername,
				discordAvatarUrl: settings.discordAvatarUrl,

				discordOnUploadWebhookUrl: settings.discordOnUploadWebhookUrl,
				discordOnUploadAvatarUrl: settings.discordOnUploadAvatarUrl,
				discordOnUploadUsername: settings.discordOnUploadUsername,
				discordOnUploadContent: settings.discordOnUploadContent,
				discordOnUploadEmbed: !!settings.discordOnUploadEmbed,

				"discordOnUploadEmbed.color":
					settings.discordOnUploadEmbed?.color ?? null,
				"discordOnUploadEmbed.description":
					settings.discordOnUploadEmbed?.description ?? null,
				"discordOnUploadEmbed.footer":
					settings.discordOnUploadEmbed?.footer ?? null,
				"discordOnUploadEmbed.imageOrVideo":
					settings.discordOnUploadEmbed?.imageOrVideo ?? false,
				"discordOnUploadEmbed.thumbnail":
					settings.discordOnUploadEmbed?.thumbnail ?? false,
				"discordOnUploadEmbed.timestamp":
					settings.discordOnUploadEmbed?.timestamp ?? false,
				"discordOnUploadEmbed.title":
					settings.discordOnUploadEmbed?.title ?? null,
				"discordOnUploadEmbed.url": settings.discordOnUploadEmbed?.url ?? false,

				discordOnShortenWebhookUrl: settings.discordOnShortenWebhookUrl,
				discordOnShortenUsername: settings.discordOnShortenUsername,
				discordOnShortenAvatarUrl: settings.discordOnShortenAvatarUrl,
				discordOnShortenContent: settings.discordOnShortenContent,
				discordOnShortenEmbed: !!settings.discordOnShortenEmbed,

				"discordOnShortenEmbed.color":
					settings.discordOnShortenEmbed?.color ?? null,
				"discordOnShortenEmbed.description":
					settings.discordOnShortenEmbed?.description ?? null,
				"discordOnShortenEmbed.footer":
					settings.discordOnShortenEmbed?.footer ?? null,
				"discordOnShortenEmbed.imageOrVideo":
					settings.discordOnShortenEmbed?.imageOrVideo ?? false,
				"discordOnShortenEmbed.thumbnail":
					settings.discordOnShortenEmbed?.thumbnail ?? false,
				"discordOnShortenEmbed.timestamp":
					settings.discordOnShortenEmbed?.timestamp ?? false,
				"discordOnShortenEmbed.title":
					settings.discordOnShortenEmbed?.title ?? null,
				"discordOnShortenEmbed.url":
					settings.discordOnShortenEmbed?.url ?? false,
			});
		}
	}, [settings]);

	const [saveError, setSaveError] = useState<Array<string> | null>(null);

	async function handleSave(category: SaveCategories) {
		setSaveError(null);

		if (!saveSettings) return;
		let settingsToSave: Partial<APISettings> = {};

		switch (category) {
			case "core": {
				settingsToSave = {
					coreReturnHttpsUrls: saveSettings.coreReturnHttpsUrls,
					coreDefaultDomain: saveSettings.coreDefaultDomain,
					coreTempDirectory: saveSettings.coreTempDirectory,
				};

				break;
			}

			case "chunks": {
				settingsToSave = {
					chunksEnabled: saveSettings.chunksEnabled,
					chunksMax: saveSettings.chunksMax,
					chunksSize: saveSettings.chunksSize,
				};

				break;
			}

			case "tasks": {
				settingsToSave = {
					tasksClearInvitesInterval: saveSettings.tasksClearInvitesInterval,
					tasksDeleteInterval: saveSettings.tasksDeleteInterval,
					tasksMaxViewsInterval: saveSettings.tasksMaxViewsInterval,
					tasksMetricsInterval: saveSettings.tasksMetricsInterval,
					tasksThumbnailsInterval: saveSettings.tasksThumbnailsInterval,
				};

				break;
			}

			case "mfa": {
				settingsToSave = {
					mfaPasskeys: saveSettings.mfaPasskeys,
					mfaTotpEnabled: saveSettings.mfaTotpEnabled,
					mfaTotpIssuer: saveSettings.mfaTotpIssuer,
				};

				break;
			}

			case "features": {
				settingsToSave = {
					featuresImageCompression: saveSettings.featuresImageCompression,
					featuresRobotsTxt: saveSettings.featuresRobotsTxt,
					featuresHealthcheck: saveSettings.featuresHealthcheck,
					featuresUserRegistration: saveSettings.featuresUserRegistration,
					featuresOauthRegistration: saveSettings.featuresOauthRegistration,
					featuresDeleteOnMaxViews: saveSettings.featuresDeleteOnMaxViews,
					featuresThumbnailsEnabled: saveSettings.featuresThumbnailsEnabled,
					featuresThumbnailsNumberThreads:
						saveSettings.featuresThumbnailsNumberThreads,
					featuresMetricsEnabled: saveSettings.featuresMetricsEnabled,
					featuresMetricsAdminOnly: saveSettings.featuresMetricsAdminOnly,
					featuresMetricsShowUserSpecific:
						saveSettings.featuresMetricsShowUserSpecific,
				};

				break;
			}

			case "files": {
				settingsToSave = {
					filesRoute: saveSettings.filesRoute,
					filesLength: saveSettings.filesLength,
					filesAssumeMimetypes: saveSettings.filesAssumeMimetypes,
					filesRemoveGpsMetadata: saveSettings.filesRemoveGpsMetadata,
					filesDefaultFormat: saveSettings.filesDefaultFormat,
					filesDisabledExtensions:
						saveSettings.filesDisabledExtensions.split(", "),
					filesMaxFileSize: saveSettings.filesMaxFileSize,
					filesDefaultExpiration: saveSettings.filesDefaultExpiration,
					filesDefaultDateFormat: saveSettings.filesDefaultDateFormat,
				};

				break;
			}

			case "urlShortener": {
				settingsToSave = {
					urlsRoute: saveSettings.urlsRoute,
					urlsLength: saveSettings.urlsLength,
				};

				break;
			}

			case "invites": {
				settingsToSave = {
					invitesEnabled: saveSettings.invitesEnabled,
					invitesLength: saveSettings.invitesLength,
				};

				break;
			}

			case "ratelimit": {
				settingsToSave = {
					ratelimitEnabled: saveSettings.ratelimitEnabled,
					ratelimitMax: saveSettings.ratelimitMax,
					ratelimitWindow: saveSettings.ratelimitWindow,
					ratelimitAdminBypass: saveSettings.ratelimitAdminBypass,
					ratelimitAllowList: saveSettings.ratelimitAllowList.split(", "),
				};

				break;
			}

			case "website": {
				settingsToSave = {
					websiteTitle: saveSettings.websiteTitle,
					websiteTitleLogo: saveSettings.websiteTitleLogo,
					websiteLoginBackground: saveSettings.websiteLoginBackground,
					websiteLoginBackgroundBlur: saveSettings.websiteLoginBackgroundBlur,
					websiteDefaultAvatar: saveSettings.websiteDefaultAvatar,
					websiteTos: saveSettings.websiteTos,
					websiteThemeDefault: saveSettings.websiteThemeDefault,
					websiteThemeDark: saveSettings.websiteThemeDark,
					websiteThemeLight: saveSettings.websiteThemeLight,
				};

				try {
					settingsToSave.websiteExternalLinks = JSON.parse(
						saveSettings.websiteExternalLinks || "",
					);
				} catch (e) {}

				break;
			}

			case "oauth": {
				settingsToSave = {
					oauthBypassLocalLogin: saveSettings.oauthBypassLocalLogin,
					oauthLoginOnly: saveSettings.oauthLoginOnly,
					oauthDiscordClientId: saveSettings.oauthDiscordClientId,
					oauthDiscordClientSecret: saveSettings.oauthDiscordClientSecret,
					oauthDiscordRedirectUri: saveSettings.oauthDiscordRedirectUri,
					oauthGoogleClientId: saveSettings.oauthGoogleClientId,
					oauthGoogleClientSecret: saveSettings.oauthGoogleClientSecret,
					oauthGoogleRedirectUri: saveSettings.oauthGoogleRedirectUri,
					oauthGithubClientId: saveSettings.oauthGithubClientId,
					oauthGithubClientSecret: saveSettings.oauthGithubClientSecret,
					oauthGithubRedirectUri: saveSettings.oauthGithubRedirectUri,
					oauthOidcClientId: saveSettings.oauthOidcClientId,
					oauthOidcClientSecret: saveSettings.oauthOidcClientSecret,
					oauthOidcAuthorizeUrl: saveSettings.oauthOidcAuthorizeUrl,
					oauthOidcTokenUrl: saveSettings.oauthOidcTokenUrl,
					oauthOidcUserinfoUrl: saveSettings.oauthOidcUserinfoUrl,
					oauthOidcRedirectUri: saveSettings.oauthOidcRedirectUri,
				};

				break;
			}

			case "pwa": {
				settingsToSave = {
					pwaEnabled: saveSettings.pwaEnabled,
					pwaTitle: saveSettings.pwaTitle,
					pwaShortName: saveSettings.pwaShortName,
					pwaDescription: saveSettings.pwaDescription,
					pwaThemeColor: saveSettings.pwaThemeColor,
					pwaBackgroundColor: saveSettings.pwaBackgroundColor,
				};

				break;
			}

			case "httpWebhooks": {
				settingsToSave = {
					httpWebhookOnUpload: saveSettings.httpWebhookOnUpload,
					httpWebhookOnShorten: saveSettings.httpWebhookOnShorten,
				};

				break;
			}

			case "discordWebhook": {
				settingsToSave = {
					discordWebhookUrl: saveSettings.discordWebhookUrl,
					discordUsername: saveSettings.discordUsername,
					discordAvatarUrl: saveSettings.discordAvatarUrl,
				};

				break;
			}

			case "discordOnShortenWebhook": {
				settingsToSave = {
					discordOnShortenWebhookUrl: saveSettings.discordOnShortenWebhookUrl,
					discordOnShortenUsername: saveSettings.discordOnShortenUsername,
					discordOnShortenAvatarUrl: saveSettings.discordOnShortenAvatarUrl,
					discordOnShortenContent: saveSettings.discordOnShortenContent,
					discordOnShortenEmbed: saveSettings.discordOnShortenEmbed
						? {
								color: saveSettings["discordOnShortenEmbed.color"],
								description: saveSettings["discordOnShortenEmbed.description"],
								footer: saveSettings["discordOnShortenEmbed.footer"],
								imageOrVideo:
									saveSettings["discordOnShortenEmbed.imageOrVideo"],
								thumbnail: saveSettings["discordOnShortenEmbed.thumbnail"],
								timestamp: saveSettings["discordOnShortenEmbed.timestamp"],
								title: saveSettings["discordOnShortenEmbed.title"],
								url: saveSettings["discordOnShortenEmbed.url"],
							}
						: null,
				};

				break;
			}

			case "discordOnUploadWebhook": {
				settingsToSave = {
					discordOnUploadWebhookUrl: saveSettings.discordOnUploadWebhookUrl,
					discordOnUploadUsername: saveSettings.discordOnUploadUsername,
					discordOnUploadAvatarUrl: saveSettings.discordOnUploadAvatarUrl,
					discordOnUploadContent: saveSettings.discordOnUploadContent,
					discordOnUploadEmbed: saveSettings.discordOnUploadEmbed
						? {
								color: saveSettings["discordOnUploadEmbed.color"],
								description: saveSettings["discordOnUploadEmbed.description"],
								footer: saveSettings["discordOnUploadEmbed.footer"],
								imageOrVideo: saveSettings["discordOnUploadEmbed.imageOrVideo"],
								thumbnail: saveSettings["discordOnUploadEmbed.thumbnail"],
								timestamp: saveSettings["discordOnUploadEmbed.timestamp"],
								title: saveSettings["discordOnUploadEmbed.title"],
								url: saveSettings["discordOnUploadEmbed.url"],
							}
						: null,
				};

				break;
			}
		}

		if (Object.keys(settingsToSave).length <= 0)
			return "Something went wrong...";

		const success = await updateSettings(settingsToSave);

		if (Array.isArray(success)) return setSaveError(success);

		const newSettings = await getSettings();

		setSettings(typeof newSettings === "string" ? null : newSettings);

		return ToastAndroid.show(
			"Successfully saved the settings",
			ToastAndroid.SHORT,
		);
	}

	function renderSetting(setting: Setting) {
		switch (setting.type) {
			case "category": {
				return (
					<View
						key={setting.category}
						style={{
							...styles.settingGroup,
							...(setting.if &&
								!saveSettings?.[setting.if] && {
									display: "none",
								}),
						}}
					>
						<Text style={styles.headerText}>{setting.name}</Text>

						{setting.children.map((childSetting) =>
							renderSetting(childSetting),
						)}
					</View>
				);
			}

			case "input": {
				return (
					<TextInput
						key={setting.setting}
						title={setting.name}
						keyboardType={setting.keyboardType}
						onValueChange={(content) =>
							setSaveSettings((prev) => {
								return {
									...prev,
									[setting.setting]: content,
								} as SaveSettings;
							})
						}
						value={
							saveSettings?.[setting.setting]
								? String(saveSettings?.[setting.setting]) || ""
								: ""
						}
						placeholder={setting.placeholder}
						multiline={setting.multiline}
						inputStyle={
							setting.multiline
								? {
										maxHeight: 400,
										height: undefined,
										fontFamily: "monospace",
									}
								: undefined
						}
					/>
				);
			}

			case "select": {
				return (
					<Select
						key={setting.setting}
						data={setting.options}
						onSelect={(selectedItem) => {
							setSaveSettings((prev) => {
								return {
									...prev,
									[setting.setting]: selectedItem[0].value,
								} as SaveSettings;
							});
						}}
						placeholder={setting.placeholder}
						defaultValue={setting.options.find(
							(option) => option.value === setting.defaultValue,
						)}
					/>
				);
			}

			case "switch": {
				return (
					<Switch
						key={setting.setting}
						onValueChange={() =>
							setSaveSettings((prev) => {
								return {
									...prev,
									[setting.setting]: !prev?.[setting.setting],
								} as SaveSettings;
							})
						}
						value={!!saveSettings?.[setting.setting]}
						title={setting.name}
					/>
				);
			}

			case "save": {
				return (
					<Button
						key={setting.category}
						onPress={() => handleSave(setting.category)}
						color="#323ea8"
						text="Save"
						icon="save"
						margin={{
							top: 10,
						}}
					/>
				);
			}
		}
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
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

				{saveSettings ? (
					<View style={styles.settingsContainer}>
						<KeyboardAwareScrollView style={styles.scrollView}>
							{zlSettings.map((setting) => renderSetting(setting))}
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

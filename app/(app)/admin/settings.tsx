import Button from "@/components/Button";
import ColorPicker from "@/components/ColorPicker";
import Domain from "@/components/Domain";
import ExternalUrl from "@/components/ExternalUrl";
import Popup from "@/components/Popup";
import Select from "@/components/Select";
import SkeletonColorPicker from "@/components/skeleton/ColorPicker";
import Skeleton from "@/components/skeleton/Skeleton";
import SkeletonTextInput from "@/components/skeleton/TextInput";
import Switch from "@/components/Switch";
import TextInput from "@/components/TextInput";
import type {
	SaveCategories,
	SaveSettings,
	Setting,
} from "@/constants/adminSettings";
import { settings as zlSettings } from "@/constants/adminSettings";
import { ZiplineContext } from "@/contexts/ZiplineProvider";
import { parseMarkdownLinks } from "@/functions/componentUtil";
import { convertToBytes, convertToTime } from "@/functions/util";
import {
	getSettings,
	updateSettings,
} from "@/functions/zipline/settings";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { styles } from "@/styles/admin/settings";
import type { APISettings, ExternalLink } from "@/types/zipline";
import { useContext, useEffect, useState, type ReactNode } from "react";
import { Pressable, ScrollView, Text, ToastAndroid, View } from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";

const urlRegex = /http(s)?:\/\/(.+)\.(.+)/;

export default function ServerSettings() {
	useAuth("SUPERADMIN");
	useShareIntent();

	const [settings, setSettings] = useState<APISettings | null>(null);
	const { updateSettings: updateContextSettings } = useContext(ZiplineContext)

	useEffect(() => {
		(async () => {
			const settings = await getSettings();

			setSettings(typeof settings === "string" ? null : settings);
		})();
	}, []);

	const [saveSettings, setSaveSettings] = useState<SaveSettings | null>(null);
	const [saving, setSaving] = useState<boolean>(false);

	useEffect(() => {
		if (settings) {
			setSaveSettings({
				coreReturnHttpsUrls: settings.settings.coreReturnHttpsUrls,
				coreDefaultDomain: settings.settings.coreDefaultDomain,
				coreTempDirectory: settings.settings.coreTempDirectory,
				coreTrustProxy: settings.settings.coreTrustProxy,

				chunksEnabled: settings.settings.chunksEnabled,
				chunksMax:
					convertToBytes(settings.settings.chunksMax, {
						unitSeparator: " ",
					}) || settings.settings.chunksMax,
				chunksSize:
					convertToBytes(settings.settings.chunksSize, {
						unitSeparator: " ",
					}) || settings.settings.chunksSize,

				tasksDeleteInterval:
					convertToTime(settings.settings.tasksDeleteInterval, {
						useAbbreviations: true,
					}) || settings.settings.tasksDeleteInterval,
				tasksClearInvitesInterval:
					convertToTime(settings.settings.tasksClearInvitesInterval, {
						useAbbreviations: true,
					}) || settings.settings.tasksClearInvitesInterval,
				tasksMaxViewsInterval:
					convertToTime(settings.settings.tasksMaxViewsInterval, {
						useAbbreviations: true,
					}) || settings.settings.tasksMaxViewsInterval,
				tasksThumbnailsInterval:
					convertToTime(settings.settings.tasksThumbnailsInterval, {
						useAbbreviations: true,
					}) || settings.settings.tasksThumbnailsInterval,
				tasksMetricsInterval:
					convertToTime(settings.settings.tasksMetricsInterval, {
						useAbbreviations: true,
					}) || settings.settings.tasksMetricsInterval,

				mfaPasskeys: settings.settings.mfaPasskeys,
				mfaTotpEnabled: settings.settings.mfaTotpEnabled,
				mfaTotpIssuer: settings.settings.mfaTotpIssuer,

				featuresImageCompression: settings.settings.featuresImageCompression,
				featuresRobotsTxt: settings.settings.featuresRobotsTxt,
				featuresHealthcheck: settings.settings.featuresHealthcheck,
				featuresUserRegistration: settings.settings.featuresUserRegistration,
				featuresOauthRegistration: settings.settings.featuresOauthRegistration,
				featuresDeleteOnMaxViews: settings.settings.featuresDeleteOnMaxViews,
				featuresMetricsEnabled: settings.settings.featuresMetricsEnabled,
				featuresMetricsAdminOnly: settings.settings.featuresMetricsAdminOnly,
				featuresMetricsShowUserSpecific:
					settings.settings.featuresMetricsShowUserSpecific,
				featuresThumbnailsEnabled: settings.settings.featuresThumbnailsEnabled,
				featuresThumbnailsNumberThreads:
					settings.settings.featuresThumbnailsNumberThreads,
				featuresThumbnailsFormat: settings.settings.featuresThumbnailsFormat,
				featuresVersionChecking: settings.settings.featuresVersionChecking,
				featuresVersionAPI: settings.settings.featuresVersionAPI,

				filesRoute: settings.settings.filesRoute,
				filesLength: settings.settings.filesLength,
				filesAssumeMimetypes: settings.settings.filesAssumeMimetypes,
				filesDefaultCompressionFormat: settings.settings.filesDefaultCompressionFormat,
				filesRemoveGpsMetadata: settings.settings.filesRemoveGpsMetadata,
				filesDefaultFormat: settings.settings.filesDefaultFormat,
				filesDisabledExtensions:
					settings.settings.filesDisabledExtensions.join(", "),
				filesMaxFileSize:
					convertToBytes(settings.settings.filesMaxFileSize, {
						unitSeparator: " ",
					}) || settings.settings.filesMaxFileSize,
				filesDefaultExpiration: settings.settings.filesDefaultExpiration,
				filesDefaultDateFormat: settings.settings.filesDefaultDateFormat,
				filesRandomWordsNumAdjectives:
					settings.settings.filesRandomWordsNumAdjectives,
				filesRandomWordsSeparator: settings.settings.filesRandomWordsSeparator,

				urlsRoute: settings.settings.urlsRoute,
				urlsLength: settings.settings.urlsLength,

				invitesEnabled: settings.settings.invitesEnabled,
				invitesLength: settings.settings.invitesLength,

				ratelimitEnabled: settings.settings.ratelimitEnabled,
				ratelimitAdminBypass: settings.settings.ratelimitAdminBypass,
				ratelimitMax: settings.settings.ratelimitMax,
				ratelimitWindow: settings.settings.ratelimitWindow,
				ratelimitAllowList: settings.settings.ratelimitAllowList.join(", "),

				websiteTitle: settings.settings.websiteTitle,
				websiteTitleLogo: settings.settings.websiteTitleLogo,
				websiteExternalLinks:
					JSON.stringify(settings.settings.websiteExternalLinks, null, 2) || "",
				websiteLoginBackground: settings.settings.websiteLoginBackground,
				websiteLoginBackgroundBlur:
					settings.settings.websiteLoginBackgroundBlur,
				websiteDefaultAvatar: settings.settings.websiteDefaultAvatar,
				websiteTos: settings.settings.websiteTos,
				websiteThemeDefault: settings.settings.websiteThemeDefault,
				websiteThemeDark: settings.settings.websiteThemeDark,
				websiteThemeLight: settings.settings.websiteThemeLight,

				oauthBypassLocalLogin: settings.settings.oauthBypassLocalLogin,
				oauthLoginOnly: settings.settings.oauthLoginOnly,

				oauthDiscordClientId: settings.settings.oauthDiscordClientId,
				oauthDiscordClientSecret: settings.settings.oauthDiscordClientSecret,
				oauthDiscordRedirectUri: settings.settings.oauthDiscordRedirectUri,
				oauthDiscordAllowedIds:
					settings.settings.oauthDiscordAllowedIds.join(", "),
				oauthDiscordDeniedIds:
					settings.settings.oauthDiscordDeniedIds.join(", "),

				oauthGoogleClientId: settings.settings.oauthGoogleClientId,
				oauthGoogleClientSecret: settings.settings.oauthGoogleClientSecret,
				oauthGoogleRedirectUri: settings.settings.oauthGoogleRedirectUri,

				oauthGithubClientId: settings.settings.oauthGithubClientId,
				oauthGithubClientSecret: settings.settings.oauthGithubClientSecret,
				oauthGithubRedirectUri: settings.settings.oauthGithubRedirectUri,

				oauthOidcClientId: settings.settings.oauthOidcClientId,
				oauthOidcClientSecret: settings.settings.oauthOidcClientSecret,
				oauthOidcAuthorizeUrl: settings.settings.oauthOidcAuthorizeUrl,
				oauthOidcTokenUrl: settings.settings.oauthOidcTokenUrl,
				oauthOidcUserinfoUrl: settings.settings.oauthOidcUserinfoUrl,
				oauthOidcRedirectUri: settings.settings.oauthOidcRedirectUri,

				pwaEnabled: settings.settings.pwaEnabled,
				pwaTitle: settings.settings.pwaTitle,
				pwaShortName: settings.settings.pwaShortName,
				pwaDescription: settings.settings.pwaDescription,
				pwaThemeColor: settings.settings.pwaThemeColor,
				pwaBackgroundColor: settings.settings.pwaBackgroundColor,

				httpWebhookOnUpload: settings.settings.httpWebhookOnUpload,
				httpWebhookOnShorten: settings.settings.httpWebhookOnShorten,

				domains: JSON.stringify(settings.settings.domains),

				discordWebhookUrl: settings.settings.discordWebhookUrl,
				discordUsername: settings.settings.discordUsername,
				discordAvatarUrl: settings.settings.discordAvatarUrl,

				discordOnUploadWebhookUrl: settings.settings.discordOnUploadWebhookUrl,
				discordOnUploadAvatarUrl: settings.settings.discordOnUploadAvatarUrl,
				discordOnUploadUsername: settings.settings.discordOnUploadUsername,
				discordOnUploadContent: settings.settings.discordOnUploadContent,
				discordOnUploadEmbed: !!settings.settings.discordOnUploadEmbed,

				"discordOnUploadEmbed.color":
					settings.settings.discordOnUploadEmbed?.color ?? null,
				"discordOnUploadEmbed.description":
					settings.settings.discordOnUploadEmbed?.description ?? null,
				"discordOnUploadEmbed.footer":
					settings.settings.discordOnUploadEmbed?.footer ?? null,
				"discordOnUploadEmbed.imageOrVideo":
					settings.settings.discordOnUploadEmbed?.imageOrVideo ?? false,
				"discordOnUploadEmbed.thumbnail":
					settings.settings.discordOnUploadEmbed?.thumbnail ?? false,
				"discordOnUploadEmbed.timestamp":
					settings.settings.discordOnUploadEmbed?.timestamp ?? false,
				"discordOnUploadEmbed.title":
					settings.settings.discordOnUploadEmbed?.title ?? null,
				"discordOnUploadEmbed.url":
					settings.settings.discordOnUploadEmbed?.url ?? false,

				discordOnShortenWebhookUrl:
					settings.settings.discordOnShortenWebhookUrl,
				discordOnShortenUsername: settings.settings.discordOnShortenUsername,
				discordOnShortenAvatarUrl: settings.settings.discordOnShortenAvatarUrl,
				discordOnShortenContent: settings.settings.discordOnShortenContent,
				discordOnShortenEmbed: !!settings.settings.discordOnShortenEmbed,

				"discordOnShortenEmbed.color":
					settings.settings.discordOnShortenEmbed?.color ?? null,
				"discordOnShortenEmbed.description":
					settings.settings.discordOnShortenEmbed?.description ?? null,
				"discordOnShortenEmbed.footer":
					settings.settings.discordOnShortenEmbed?.footer ?? null,
				"discordOnShortenEmbed.timestamp":
					settings.settings.discordOnShortenEmbed?.timestamp ?? false,
				"discordOnShortenEmbed.title":
					settings.settings.discordOnShortenEmbed?.title ?? null,
				"discordOnShortenEmbed.url":
					settings.settings.discordOnShortenEmbed?.url ?? false,
			});
		}
	}, [settings]);

	const [saveError, setSaveError] = useState<string[] | null>(null);

	async function handleSave(category: SaveCategories) {
		setSaveError(null);
		setSaving(true);

		if (!saveSettings) return setSaving(false);
		let settingsToSave: Partial<APISettings["settings"]> = {};

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
					featuresVersionAPI: saveSettings.featuresVersionAPI,
					featuresVersionChecking: saveSettings.featuresVersionChecking,
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
					filesRandomWordsSeparator: saveSettings.filesRandomWordsSeparator,
					filesRandomWordsNumAdjectives:
						saveSettings.filesRandomWordsNumAdjectives,
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
						saveSettings.websiteExternalLinks || "[]",
					);
				} catch (_e) {}

				break;
			}

			case "oauth": {
				settingsToSave = {
					oauthBypassLocalLogin: saveSettings.oauthBypassLocalLogin,
					oauthLoginOnly: saveSettings.oauthLoginOnly,
					oauthDiscordClientId: saveSettings.oauthDiscordClientId,
					oauthDiscordAllowedIds:
						saveSettings.oauthDiscordAllowedIds.split(", "),
					oauthDiscordDeniedIds: saveSettings.oauthDiscordDeniedIds.split(", "),
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

			case "domains": {
				settingsToSave = {
					domains: JSON.parse(saveSettings.domains) || "[]",
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

		if (Object.keys(settingsToSave).length <= 0) {
			setSaveError(["Something went wrong..."]);
			return setSaving(false);
		}

		const success = await updateSettings(settingsToSave);

		if (Array.isArray(success)) {
			setSaveError(success);
			setSaving(false);
		}

		const newSettings = await getSettings();

		setSettings(typeof newSettings === "string" ? null : newSettings);
		setSaving(false);

		await updateContextSettings();

		return ToastAndroid.show(
			"Successfully saved the settings",
			ToastAndroid.SHORT,
		);
	}

	const [editUrlIndex, setEditUrlIndex] = useState<number>(-1);
	const [editUrlName, setEditUrlName] = useState<string | null>(null);
	const [editUrlURL, setEditUrlURL] = useState<string | null>(null);

	const [editUrlError, setEditUrlError] = useState<string | null>(null);

	const [createNewUrl, setCreateNewUrl] = useState<boolean>(false);
	const [newUrlName, setNewUrlName] = useState<string | null>(null);
	const [newUrlURL, setNewUrlURL] = useState<string | null>(null);

	const [newUrlError, setNewUrlError] = useState<string | null>(null);

	const [addNewDomain, setAddNewDomain] = useState<boolean>(false);
	const [newDomain, setNewDomain] = useState<string | null>(null);

	const [newDomainError, setNewDomainError] = useState<string | null>(null);

	const [showTamperedKeys, setShowTamperedKeys] = useState<boolean>(false);

	function renderSetting(setting: Setting, skeleton = false) {
		let description: ReactNode;

		if (
			setting.type !== "category" &&
			setting.type !== "externalUrls" &&
			setting.type !== "domain" &&
			setting.type !== "save" &&
			typeof setting.description === "string"
		)
			description = (
				<Text>
					{parseMarkdownLinks(setting.description, {
						color: "#575db5",
					})}
				</Text>
			);

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

						{setting.description && (
							<Text style={styles.headerDescription}>
								{setting.description}
							</Text>
						)}

						{setting.children.map((childSetting) =>
							renderSetting(childSetting, skeleton),
						)}
					</View>
				);
			}

			case "input": {
				if (skeleton)
					return (
						<SkeletonTextInput
							key={setting.setting}
							title={setting.name}
							description={description}
							skeletonWidth={setting.skeletonWidth}
							disableAnimation
						/>
					);

				return (
					<TextInput
						key={setting.setting}
						title={setting.name}
						description={description}
						password={setting.passwordInput}
						disabled={
							saving ||
							settings?.tampered.includes(
								setting.setting as keyof APISettings["settings"],
							)
						}
						keyboardType={setting.keyboardType}
						onValueChange={(content) =>
							setSaveSettings((prev) => {
								return {
									...prev,
									[setting.setting]: [
										"number-pad",
										"decimal-pad",
										"numeric",
									].includes(setting.keyboardType || "")
										? Number.parseFloat(content)
										: content,
								} as SaveSettings;
							})
						}
						defaultValue={
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
										minHeight: 150,
										height: "auto",
										textAlignVertical: "top",
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
						title={setting.name}
						key={setting.setting}
						data={setting.options}
						description={setting.description}
						disabled={
							skeleton ||
							saving ||
							settings?.tampered.includes(
								setting.setting as keyof APISettings["settings"],
							)
						}
						onSelect={(selectedItem) => {
							if (!selectedItem || selectedItem.length <= 0) return;

							setSaveSettings((prev) => {
								return {
									...prev,
									[setting.setting]: selectedItem[0].value,
								} as SaveSettings;
							});
						}}
						placeholder={setting.placeholder}
						defaultValue={
							skeleton
								? undefined
								: setting.options.find(
										(option) => option.value === setting.defaultValue,
									)
						}
					/>
				);
			}

			case "switch": {
				return (
					<Switch
						key={setting.setting}
						disabled={
							skeleton ||
							saving ||
							settings?.tampered.includes(
								setting.setting as keyof APISettings["settings"],
							)
						}
						description={setting.description}
						onValueChange={() =>
							setSaveSettings((prev) => {
								return {
									...prev,
									[setting.setting]: !prev?.[setting.setting],
								} as SaveSettings;
							})
						}
						value={skeleton ? false : !!saveSettings?.[setting.setting]}
						title={setting.name}
					/>
				);
			}

			case "externalUrls": {
				return (
					<>
						<View
							style={{
								flexDirection: "row",
								alignItems: "center",
								justifyContent: "space-between",
							}}
						>
							<View>
								<Text style={styles.externalLinkTitle}>External Links</Text>
								<Text style={styles.externalLinkDescription}>
									The external links to show in the footer.
								</Text>
							</View>

							<Button
								color={
									skeleton ||
									saving ||
									settings?.tampered.includes("websiteExternalLinks")
										? "#373d79"
										: "#323ea8"
								}
								iconColor={
									skeleton ||
									saving ||
									settings?.tampered.includes("websiteExternalLinks")
										? "gray"
										: "white"
								}
								disabled={
									skeleton ||
									saving ||
									settings?.tampered.includes("websiteExternalLinks")
								}
								onPress={() => {
									setCreateNewUrl(true);
								}}
								containerStyle={{
									width: 30,
									height: 30,
									marginRight: 10
								}}
								iconStyle={{
									marginTop: -7,
									marginLeft: -4
								}}
								icon="add"
								iconSize={30}
							/>
						</View>

						<View
							style={{
								...styles.settingGroup,
								marginTop: 0,
							}}
						>
							<ScrollView
								style={styles.externalUrlsScrollView}
								showsVerticalScrollIndicator={false}
								nestedScrollEnabled
							>
								{skeleton ? (
									// biome-ignore lint/complexity/noUselessFragments: The fragment is required
									<>
										{[...Array(2).keys()].map((index) => (
											<View
												key={index}
												style={{
													marginVertical: 5,
												}}
											>
												<Skeleton width="100%" height={100} />
											</View>
										))}
									</>
								) : (
									// biome-ignore lint/complexity/noUselessFragments: The fragment is required
									<>
										{(
											JSON.parse(
												saveSettings?.websiteExternalLinks || "[]",
											) as ExternalLink[]
										).map((url, index) => (
											<ExternalUrl
												externalUrl={url}
												key={`${url.url}-${index}`}
												id={index}
												disabled={
													saving ||
													settings?.tampered.includes("websiteExternalLinks")
												}
												onChange={(type, id) => {
													switch (type) {
														case "delete": {
															const newUrls: ExternalLink[] = JSON.parse(
																saveSettings?.websiteExternalLinks || "[]",
															);

															const name = newUrls[id].name;

															newUrls.splice(id, 1);

															setSaveSettings((prev) => {
																if (!prev) return prev;

																return {
																	...prev,
																	websiteExternalLinks: JSON.stringify(newUrls),
																};
															});

															return ToastAndroid.show(
																`Deleted the external URL ${name}`,
																ToastAndroid.SHORT,
															);
														}

														case "edit": {
															const urls: ExternalLink[] = JSON.parse(
																saveSettings?.websiteExternalLinks || "[]",
															);

															setEditUrlIndex(id);
															setEditUrlName(urls[id].name);
															setEditUrlURL(urls[id].url);
														}
													}
												}}
												onMove={(type, id) => {
													const urls: ExternalLink[] = JSON.parse(
														saveSettings?.websiteExternalLinks || "[]",
													);

													switch (type) {
														case "down": {
															[urls[id], urls[id + 1]] = [
																urls[id + 1],
																urls[id],
															];

															break;
														}

														case "up": {
															[urls[id], urls[id - 1]] = [
																urls[id - 1],
																urls[id],
															];
														}
													}

													setSaveSettings((prev) => {
														if (!prev) return prev;

														return {
															...prev,
															websiteExternalLinks: JSON.stringify(urls),
														};
													});
												}}
											/>
										))}
									</>
								)}
							</ScrollView>
						</View>
					</>
				);
			}

			case "domain": {
				return (
					<>
						<Button
							text="Add a Domain"
							icon="add"
							iconColor={skeleton || saving ? "gray" : "white"}
							textColor={skeleton || saving ? "gray" : "white"}
							disabled={
								skeleton || saving || settings?.tampered.includes("domains")
							}
							containerStyle={{
								marginHorizontal: 10,
								marginTop: 5,
								marginBottom: 10
							}}
							onPress={() => {
								setAddNewDomain(true);
							}}
							color={skeleton || saving ? "#373d79" : "#323ea8"}
						/>

						{(JSON.parse(saveSettings?.domains || "[]") as string[]).length >
							0 && (
							<View
								style={{
									...styles.settingGroup,
									marginTop: 0,
								}}
							>
								<ScrollView
									style={styles.externalUrlsScrollView}
									showsVerticalScrollIndicator={false}
									nestedScrollEnabled
								>
									{!skeleton && (
										// biome-ignore lint/complexity/noUselessFragments: The fragment is required
										<>
											{(
												JSON.parse(saveSettings?.domains || "[]") as string[]
											).map((domain, index) => (
												<Domain
													// biome-ignore lint/suspicious/noArrayIndexKey: index is the only unique identifier here
													key={index}
													domain={domain}
													disabled={
														saving || settings?.tampered.includes("domains")
													}
													onDelete={() => {
														const newDomains: string[] = JSON.parse(
															saveSettings?.domains || "[]",
														);

														const domainIndex = newDomains.indexOf(domain);

														newDomains.splice(domainIndex, 1);

														setSaveSettings((prev) => {
															if (!prev) return prev;

															return {
																...prev,
																domains: JSON.stringify(newDomains),
															};
														});

														return ToastAndroid.show(
															`Deleted the Domain ${domain}`,
															ToastAndroid.SHORT,
														);
													}}
												/>
											))}
										</>
									)}
								</ScrollView>
							</View>
						)}
					</>
				);
			}

			case "colorPicker": {
				if (skeleton)
					return (
						<SkeletonColorPicker
							title={setting.name}
							description={setting.description}
						/>
					);

				return (
					<ColorPicker
						title={setting.name}
						description={setting.description}
						disabled={saving}
						initialColor={saveSettings?.[setting.setting] as string | undefined}
						onSelectColor={(color) => {
							setSaveSettings((prev) => {
								return {
									...prev,
									[setting.setting]: color.hex,
								} as SaveSettings;
							});
						}}
					/>
				);
			}

			case "save": {
				return (
					<Button
						key={setting.category}
						disabled={skeleton || saving}
						onPress={() => handleSave(setting.category)}
						color={skeleton || saving ? "#373d79" : "#323ea8"}
						textColor={skeleton || saving ? "gray" : "white"}
						iconColor={skeleton || saving ? "gray" : "white"}
						text="Save"
						icon="save"
						containerStyle={{
							marginTop: 10
						}}
					/>
				);
			}
		}
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup
					onClose={() => {
						setSaveError(null);
					}}
					hidden={!saveError || saveError.length <= 0}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Errors</Text>

						<ScrollView style={styles.popupScrollView}>
							{saveError?.map((error) => (
								<Text key={error} style={styles.errorText}>
									{error}
								</Text>
							))}
						</ScrollView>

						<Text style={styles.popupSubHeaderText}>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup
					hidden={editUrlIndex < 0}
					onClose={() => {
						setEditUrlIndex(-1);
						setEditUrlName(null);
						setEditUrlURL(null);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Edit External URL</Text>
						{editUrlError && (
							<Text style={styles.errorText}>{editUrlError}</Text>
						)}

						<TextInput
							title="Name:"
							onValueChange={(content) => {
								setEditUrlName(content);
							}}
							value={editUrlName || ""}
							placeholder="Google"
						/>

						<TextInput
							title="URL:"
							onValueChange={(content) => {
								setEditUrlURL(content);
							}}
							value={editUrlURL || ""}
							keyboardType="url"
							placeholder="https://google.com"
						/>

						<Button
							color="#323ea8"
							text="Edit"
							icon="edit"
							containerStyle={{
								marginTop: 10
							}}
							onPress={async () => {
								setEditUrlError(null);

								if (!editUrlName)
									return setEditUrlError("Please insert a name");
								if (!editUrlURL) return setEditUrlError("Please insert a URL");
								if (!urlRegex.test(editUrlURL))
									return setEditUrlError("Please insert a valid URL");

								const newUrls: ExternalLink[] = JSON.parse(
									saveSettings?.websiteExternalLinks || "[]",
								);

								newUrls[editUrlIndex] = {
									name: editUrlName,
									url: editUrlURL,
								};

								setSaveSettings((prev) => {
									if (!prev) return prev;

									return {
										...prev,
										websiteExternalLinks: JSON.stringify(newUrls),
									};
								});

								ToastAndroid.show(
									`Edited the external URL "${newUrls[editUrlIndex].name}"`,
									ToastAndroid.SHORT,
								);

								setEditUrlName(null);
								setEditUrlURL(null);
								setEditUrlIndex(-1);
							}}
						/>

						<Text style={styles.popupSubHeaderText}>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup
					hidden={!createNewUrl}
					onClose={() => {
						setCreateNewUrl(false);
						setNewUrlName(null);
						setNewUrlURL(null);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Add External URL</Text>
						{newUrlError && <Text style={styles.errorText}>{newUrlError}</Text>}

						<TextInput
							title="Name:"
							onValueChange={(content) => {
								setNewUrlName(content);
							}}
							value={newUrlName || ""}
							placeholder="Google"
						/>

						<TextInput
							title="URL:"
							onValueChange={(content) => {
								setNewUrlURL(content);
							}}
							value={newUrlURL || ""}
							keyboardType="url"
							placeholder="https://google.com"
						/>

						<Button
							color="#323ea8"
							text="Add"
							icon="add"
							containerStyle={{
								marginTop: 10
							}}
							onPress={async () => {
								setNewUrlError(null);

								if (!newUrlName) return setNewUrlError("Please insert a name");
								if (!newUrlURL) return setNewUrlError("Please insert a URL");
								if (!urlRegex.test(newUrlURL))
									return setNewUrlError("Please insert a valid URL");

								const newUrls: ExternalLink[] = JSON.parse(
									saveSettings?.websiteExternalLinks || "[]",
								);

								newUrls.push({
									name: newUrlName,
									url: newUrlURL,
								});

								setSaveSettings((prev) => {
									if (!prev) return prev;

									return {
										...prev,
										websiteExternalLinks: JSON.stringify(newUrls),
									};
								});

								ToastAndroid.show(
									`Added the external URL "${newUrlName}"`,
									ToastAndroid.SHORT,
								);

								setNewUrlName(null);
								setNewUrlURL(null);
								setCreateNewUrl(false);
							}}
						/>

						<Text style={styles.popupSubHeaderText}>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup
					hidden={!addNewDomain}
					onClose={() => {
						setAddNewDomain(false);
						setNewDomain(null);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Add New Domain</Text>
						{newDomainError && (
							<Text style={styles.errorText}>{newDomainError}</Text>
						)}

						<TextInput
							title="Domain:"
							onValueChange={(content) => {
								setNewDomain(content);
							}}
							value={newDomain || ""}
							placeholder="google.com"
						/>

						<Button
							color="#323ea8"
							text="Add"
							icon="add"
							containerStyle={{
								marginTop: 10
							}}
							onPress={async () => {
								setNewDomainError(null);

								if (!newDomain)
									return setNewDomainError("Please insert a domain");

								const newDomains: string[] = JSON.parse(
									saveSettings?.domains || "[]",
								);

								newDomains.push(newDomain);

								setSaveSettings((prev) => {
									if (!prev) return prev;

									return {
										...prev,
										domains: JSON.stringify(newDomains),
									};
								});

								ToastAndroid.show(
									`Added the domain "${newDomain}"`,
									ToastAndroid.SHORT,
								);

								setNewDomain(null);
								setAddNewDomain(false);
							}}
						/>

						<Text style={styles.popupSubHeaderText}>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup
					hidden={!showTamperedKeys}
					onClose={() => {
						setShowTamperedKeys(false);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Overridden Settings</Text>

						<View>
							{settings?.tampered.map((setting) => (
								<Text key={setting} style={styles.tamperedSettingText}>
									- {setting}
								</Text>
							))}
						</View>

						<Text style={styles.popupSubHeaderText}>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<View style={styles.header}>
					<View
						style={{
							flexDirection: "row",
							justifyContent: "space-between",
							width: "100%",
						}}
					>
						<Text style={styles.headerText}>Server Settings</Text>

						<View style={styles.headerButtons}>
							<Button
								onPress={async () => {
									const settings = await getSettings();

									setSettings(typeof settings === "string" ? null : settings);

									ToastAndroid.show(
										"Successfully refreshed the settings",
										ToastAndroid.SHORT,
									);
								}}
								icon="refresh"
								color="transparent"
								iconColor="#2d3f70"
								containerStyle={{
									borderColor: "#222c47",
									borderWidth: 2
								}}
								buttonStyle={{
									padding: 4
								}}
								iconSize={30}
								rippleColor="#283557"
							/>
						</View>
					</View>
				</View>

				{saveSettings ? (
					<View style={styles.settingsContainer}>
						<KeyboardAwareScrollView style={styles.scrollView}>
							{settings && settings.tampered.length > 0 && (
								<View style={styles.tamperedSettingsWarning}>
									<Text style={styles.tamperedSettingTitle}>
										Environment Variable Settings
									</Text>

									<Text style={styles.tamperedSettingText}>
										<Text style={styles.tamperedSettingCount}>
											{settings.tampered.length}
										</Text>{" "}
										setting{settings.tampered.length > 1 ? "s have" : " has"}{" "}
										been set via environment variables, therefore any changes
										made to {settings.tampered.length > 1 ? "them" : "it"} on
										this page will not take effect unless the environment
										variable corresponding to the setting is removed. If you
										prefer using environment variables, you can ignore this
										message. Click{" "}
										<Pressable
											onPress={() => {
												setShowTamperedKeys(true);
											}}
										>
											<Text style={styles.tamperedSettingsLink}>here</Text>
										</Pressable>{" "}
										to view the list of overridden settings.
									</Text>
								</View>
							)}

							{zlSettings.map((setting) => renderSetting(setting))}
							<View />
						</KeyboardAwareScrollView>
					</View>
				) : (
					<View style={styles.settingsContainer}>
						<KeyboardAwareScrollView style={styles.scrollView}>
							{zlSettings.map((setting) => renderSetting(setting, true))}
						</KeyboardAwareScrollView>
					</View>
				)}
			</View>
		</View>
	);
}

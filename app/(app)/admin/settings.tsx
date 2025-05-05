import {
	getSettings,
	reloadSettings,
	updateSettings,
} from "@/functions/zipline/settings";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { settings as zlSettings } from "@/constants/adminSettings";
import { convertToBytes, convertToTime } from "@/functions/util";
import { useShareIntent } from "@/hooks/useShareIntent";
import { View, Text, ToastAndroid, ScrollView } from "react-native";
import type { APISettings, ExternalLink } from "@/types/zipline";
import ExternalUrl from "@/components/ExternalUrl";
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
import Popup from "@/components/Popup";
import ColorPicker from "@/components/ColorPicker";
import SkeletonTextInput from "@/components/skeleton/TextInput";
import SkeletonColorPicker from "@/components/skeleton/ColorPicker";
import Skeleton from "@/components/skeleton/Skeleton";

const urlRegex = /^http:\/\/(.*)?|https:\/\/(.*)?$/;

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
	const [saving, setSaving] = useState<boolean>(false);

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
				filesRandomWordsNumAdjectives: settings.filesRandomWordsNumAdjectives,
				filesRandomWordsSeparator: settings.filesRandomWordsSeparator,

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
		setSaving(true);

		if (!saveSettings) return setSaving(false);
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

		const reloadSuccess = await reloadSettings();

		if (typeof reloadSuccess === "string") {
			setSaveError([`Error while reloading: ${reloadSuccess}`]);
			setSaving(false);
		}

		const newSettings = await getSettings();

		setSettings(typeof newSettings === "string" ? null : newSettings);
		setSaving(false);

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

	useEffect(() => {
		console.log(saveSettings);
	}, [saveSettings]);

	function renderSetting(setting: Setting, skeleton = false) {
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
							description={setting.description}
							skeletonWidth={setting.skeletonWidth}
							disableAnimation
						/>
					);

				return (
					<TextInput
						key={setting.setting}
						title={setting.name}
						description={setting.description}
						password={setting.passwordInput}
						disabled={saving}
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
						disabled={skeleton || saving}
						onSelect={(selectedItem) => {
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
						disabled={skeleton || saving}
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
								color={skeleton || saving ? "#373d79" : "#323ea8"}
								iconColor={skeleton || saving ? "gray" : "white"}
								disabled={skeleton || saving}
								onPress={() => {
									setCreateNewUrl(true);
								}}
								width={30}
								height={30}
								padding={0}
								icon="add"
								iconSize={30}
								margin={{
									right: 10,
								}}
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
									<>
										{(
											JSON.parse(
												saveSettings?.websiteExternalLinks || "[]",
											) as Array<ExternalLink>
										).map((url, index) => (
											<ExternalUrl
												externalUrl={url}
												key={`${url.url}-${index}`}
												id={index}
												disabled={saving}
												onChange={(type, id) => {
													switch (type) {
														case "delete": {
															const newUrls: Array<ExternalLink> = JSON.parse(
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
															const urls: Array<ExternalLink> = JSON.parse(
																saveSettings?.websiteExternalLinks || "[]",
															);

															setEditUrlIndex(id);
															setEditUrlName(urls[id].name);
															setEditUrlURL(urls[id].url);
														}
													}
												}}
												onMove={(type, id) => {
													const urls: Array<ExternalLink> = JSON.parse(
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
						margin={{
							top: 10,
						}}
					/>
				);
			}
		}
	}

	useEffect(() => {
		console.log(saveError, !saveError || saveError.length <= 0);
	}, [saveError]);

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

				{/* <Popup
					onClose={() => {
						setSaveError(null)
					}}
					hidden={!saveError || saveError.length <= 0}
				>
					<View style={styles.popupContent}>
						<Text style={styles.headerText}>Failed Files</Text>

						<ScrollView style={styles.popupScrollView}>
							{saveError?.map((error) => (
								<Text key={error} style={styles.errorText}>{error}</Text>
							))}
						</ScrollView>

						<Text
							style={styles.popupSubHeaderText}
						>
							Press outside to close this popup
						</Text>
					</View>
				</Popup> */}

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
							text="Save"
							icon="save"
							margin={{
								top: 10,
							}}
							onPress={async () => {
								setEditUrlError(null);

								if (!editUrlName)
									return setEditUrlError("Please insert a name");
								if (!editUrlURL) return setEditUrlError("Please insert a URL");
								if (!urlRegex.test(editUrlURL))
									return setEditUrlError("Please insert a valid URL");

								const newUrls: Array<ExternalLink> = JSON.parse(
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
							text="Save"
							icon="save"
							margin={{
								top: 10,
							}}
							onPress={async () => {
								setNewUrlError(null);

								if (!newUrlName) return setNewUrlError("Please insert a name");
								if (!newUrlURL) return setNewUrlError("Please insert a URL");
								if (!urlRegex.test(newUrlURL))
									return setNewUrlError("Please insert a valid URL");

								const newUrls: Array<ExternalLink> = JSON.parse(
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
								borderColor="#222c47"
								borderWidth={2}
								iconSize={30}
								padding={4}
								rippleColor="#283557"
							/>
						</View>
					</View>

					{/* {saveError && (
						<View>
							{saveError.map((error) => (
								<Text style={styles.errorText} key={error}>
									{error}
								</Text>
							))}
						</View>
					)} */}
				</View>

				{saveSettings ? (
					<View style={styles.settingsContainer}>
						<KeyboardAwareScrollView style={styles.scrollView}>
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

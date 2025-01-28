import { useAuth } from "@/hooks/useAuth"
import { useShareIntent } from "@/hooks/useShareIntent"
import type { APISettings } from "@/types/zipline"
import { useState, useEffect } from "react"
import { getSettings } from "@/functions/zipline/settings"
import { View, ScrollView, Text, Pressable } from "react-native"
import bytes from "bytes"
import ms from "enhanced-ms"
import { styles } from "@/styles/admin/settings"
import { Switch } from "@react-native-material/core"
import { TextInput } from "react-native"
import Select from "@/components/Select"
import { formats } from "@/constants/settings"
import { KeyboardAwareScrollView } from "react-native-keyboard-controller"

export default function ServerSettings() {
    useAuth(true)
    useShareIntent()
    
    const [settings, setSettings] = useState<APISettings | null>(null)
    
    useEffect(() => {
        (async () => {
            const settings = await getSettings()
            
            setSettings(typeof settings === "string" ? null : settings)
        })()
    })
    
    const [coreReturnHttpsUrls, setCoreReturnHttpsUrls] = useState<boolean | null>(null);
    const [coreDefaultDomain, setCoreDefaultDomain] = useState<string | null>(null);
    const [coreTempDirectory, setCoreTempDirectory] = useState<string | null>(null);
    const [chunksEnabled, setChunksEnabled] = useState<boolean | null>(null);
    const [chunksMax, setChunksMax] = useState<string | null>(null);
    const [chunksSize, setChunksSize] = useState<string | null>(null);
    const [tasksDeleteInterval, setTasksDeleteInterval] = useState<string | null>(null);
    const [tasksClearInvitesInterval, setTasksClearInvitesInterval] = useState<string | null>(null);
    const [tasksMaxViewsInterval, setTasksMaxViewsInterval] = useState<string | null>(null);
    const [tasksThumbnailsInterval, setTasksThumbnailsInterval] = useState<string | null>(null);
    const [tasksMetricsInterval, setTasksMetricsInterval] = useState<string | null>(null);
    const [filesRoute, setFilesRoute] = useState<string | null>(null);
    const [filesLength, setFilesLength] = useState<number | null>(null);
    const [filesDefaultFormat, setFilesDefaultFormat] = useState<"random" | "uuid" | "date" | "name" | "gfycat" | null>(null);
    const [filesDisabledExtensions, setFilesDisabledExtensions] = useState<Array<string> | null>(null);
    const [filesMaxFileSize, setFilesMaxFileSize] = useState<string | null>(null);
    const [filesDefaultExpiration, setFilesDefaultExpiration] = useState<string | null>(null);
    const [filesAssumeMimetypes, setFilesAssumeMimetypes] = useState<boolean | null>(null);
    const [filesDefaultDateFormat, setFilesDefaultDateFormat] = useState<string | null>(null);
    const [filesRemoveGpsMetadata, setFilesRemoveGpsMetadata] = useState<boolean | null>(null);
    const [urlsRoute, setUrlsRoute] = useState<string | null>(null);
    const [urlsLength, setUrlsLength] = useState<number | null>(null);
    const [featuresImageCompression, setFeaturesImageCompression] = useState<boolean | null>(null);
    const [featuresRobotsTxt, setFeaturesRobotsTxt] = useState<boolean | null>(null);
    const [featuresHealthcheck, setFeaturesHealthcheck] = useState<boolean | null>(null);
    const [featuresUserRegistration, setFeaturesUserRegistration] = useState<boolean | null>(null);
    const [featuresOauthRegistration, setFeaturesOauthRegistration] = useState<boolean | null>(null);
    const [featuresDeleteOnMaxViews, setFeaturesDeleteOnMaxViews] = useState<boolean | null>(null);
    const [featuresThumbnailsEnabled, setFeaturesThumbnailsEnabled] = useState<boolean | null>(null);
    const [featuresThumbnailsNumberThreads, setFeaturesThumbnailsNumberThreads] = useState<number | null>(null);
    const [featuresMetricsEnabled, setFeaturesMetricsEnabled] = useState<boolean | null>(null);
    const [featuresMetricsAdminOnly, setFeaturesMetricsAdminOnly] = useState<boolean | null>(null);
    const [featuresMetricsShowUserSpecific, setFeaturesMetricsShowUserSpecific] = useState<boolean | null>(null);
    const [invitesEnabled, setInvitesEnabled] = useState<boolean | null>(null);
    const [invitesLength, setInvitesLength] = useState<number | null>(null);
    const [websiteTitle, setWebsiteTitle] = useState<string | null>(null);
    const [websiteTitleLogo, setWebsiteTitleLogo] = useState<string | null>(null);
    const [websiteExternalLinks, setWebsiteExternalLinks] = useState<string | null>(null);
    const [websiteLoginBackground, setWebsiteLoginBackground] = useState<string | null>(null);
    const [websiteLoginBackgroundBlur, setWebsiteLoginBackgroundBlur] = useState<boolean | null>(null);
    const [websiteDefaultAvatar, setWebsiteDefaultAvatar] = useState<string | null>(null);
    const [websiteTos, setWebsiteTos] = useState<string | null>(null);
    const [websiteThemeDefault, setWebsiteThemeDefault] = useState<string | null>(null);
    const [websiteThemeDark, setWebsiteThemeDark] = useState<string | null>(null);
    const [websiteThemeLight, setWebsiteThemeLight] = useState<string | null>(null);
    const [oauthBypassLocalLogin, setOauthBypassLocalLogin] = useState<boolean | null>(null);
    const [oauthLoginOnly, setOauthLoginOnly] = useState<boolean | null>(null);
    const [oauthDiscordClientId, setOauthDiscordClientId] = useState<string | null>(null);
    const [oauthDiscordClientSecret, setOauthDiscordClientSecret] = useState<string | null>(null);
    const [oauthDiscordRedirectUri, setOauthDiscordRedirectUri] = useState<string | null>(null);
    const [oauthGoogleClientId, setOauthGoogleClientId] = useState<string | null>(null);
    const [oauthGoogleClientSecret, setOauthGoogleClientSecret] = useState<string | null>(null);
    const [oauthGoogleRedirectUri, setOauthGoogleRedirectUri] = useState<string | null>(null);
    const [oauthGithubClientId, setOauthGithubClientId] = useState<string | null>(null);
    const [oauthGithubClientSecret, setOauthGithubClientSecret] = useState<string | null>(null);
    const [oauthGithubRedirectUri, setOauthGithubRedirectUri] = useState<string | null>(null);
    const [oauthOidcClientId, setOauthOidcClientId] = useState<string | null>(null);
    const [oauthOidcClientSecret, setOauthOidcClientSecret] = useState<string | null>(null);
    const [oauthOidcAuthorizeUrl, setOauthOidcAuthorizeUrl] = useState<string | null>(null);
    const [oauthOidcTokenUrl, setOauthOidcTokenUrl] = useState<string | null>(null);
    const [oauthOidcUserinfoUrl, setOauthOidcUserinfoUrl] = useState<string | null>(null);
    const [oauthOidcRedirectUri, setOauthOidcRedirectUri] = useState<string | null>(null);
    const [mfaTotpEnabled, setMfaTotpEnabled] = useState<boolean | null>(null);
    const [mfaTotpIssuer, setMfaTotpIssuer] = useState<string | null>(null);
    const [mfaPasskeys, setMfaPasskeys] = useState<boolean | null>(null);
    const [ratelimitEnabled, setRatelimitEnabled] = useState<boolean | null>(null);
    const [ratelimitMax, setRatelimitMax] = useState<number | null>(null);
    const [ratelimitWindow, setRatelimitWindow] = useState<number | null>(null);
    const [ratelimitAdminBypass, setRatelimitAdminBypass] = useState<boolean | null>(null);
    const [ratelimitAllowList, setRatelimitAllowList] = useState<Array<string> | null>(null);
    const [httpWebhookOnUpload, setHttpWebhookOnUpload] = useState<string | null>(null);
    const [httpWebhookOnShorten, setHttpWebhookOnShorten] = useState<string | null>(null);
    const [discordWebhookUrl, setDiscordWebhookUrl] = useState<string | null>(null);
    const [discordUsername, setDiscordUsername] = useState<string | null>(null);
    const [discordAvatarUrl, setDiscordAvatarUrl] = useState<string | null>(null);
    const [discordOnUploadWebhookUrl, setDiscordOnUploadWebhookUrl] = useState<string | null>(null);
    const [discordOnUploadUsername, setDiscordOnUploadUsername] = useState<string | null>(null);
    const [discordOnUploadAvatarUrl, setDiscordOnUploadAvatarUrl] = useState<string | null>(null);
    const [discordOnUploadContent, setDiscordOnUploadContent] = useState<string | null>(null);
    // const [discordOnUploadEmbed, setDiscordOnUploadEmbed] = useState<UploadEmbed | null>(null);
    const [discordOnShortenWebhookUrl, setDiscordOnShortenWebhookUrl] = useState<string | null>(null);
    const [discordOnShortenUsername, setDiscordOnShortenUsername] = useState<string | null>(null);
    const [discordOnShortenAvatarUrl, setDiscordOnShortenAvatarUrl] = useState<string | null>(null);
    const [discordOnShortenContent, setDiscordOnShortenContent] = useState<string | null>(null);
    // const [discordOnShortenEmbed, setDiscordOnShortenEmbed] = useState<ShortenEmbed | null>(null);
    const [pwaEnabled, setPwaEnabled] = useState<boolean | null>(null);
    const [pwaTitle, setPwaTitle] = useState<string | null>(null);
    const [pwaShortName, setPwaShortName] = useState<string | null>(null);
    const [pwaDescription, setPwaDescription] = useState<string | null>(null);
    const [pwaThemeColor, setPwaThemeColor] = useState<string | null>(null);
    const [pwaBackgroundColor, setPwaBackgroundColor] = useState<string | null>(null);
    
    useEffect(() => {
        if (settings) {
			setCoreReturnHttpsUrls(settings.coreReturnHttpsUrls);
            setCoreDefaultDomain(settings.coreDefaultDomain);
            setCoreTempDirectory(settings.coreTempDirectory);
            setChunksEnabled(settings.chunksEnabled);
            setChunksMax(typeof settings.chunksMax === "number" ? bytes(settings.chunksMax) : settings.chunksMax);
            setChunksSize(typeof settings.chunksSize === "number" ? bytes(settings.chunksSize) : settings.chunksSize);
            setTasksDeleteInterval(typeof settings.tasksDeleteInterval === "number" ? ms(settings.tasksDeleteInterval) : settings.tasksDeleteInterval);
            setTasksClearInvitesInterval(typeof settings.tasksClearInvitesInterval === "number" ? ms(settings.tasksClearInvitesInterval) : settings.tasksClearInvitesInterval);
            setTasksMaxViewsInterval(typeof settings.tasksMaxViewsInterval === "number" ? ms(settings.tasksMaxViewsInterval) : settings.tasksMaxViewsInterval);
            setTasksThumbnailsInterval(typeof settings.tasksThumbnailsInterval === "number" ? ms(settings.tasksThumbnailsInterval) : settings.tasksThumbnailsInterval);
            setTasksMetricsInterval(typeof settings.tasksMetricsInterval === "number" ? ms(settings.tasksMetricsInterval) : settings.tasksMetricsInterval);
            setFilesRoute(settings.filesRoute);
            setFilesLength(settings.filesLength);
            setFilesDefaultFormat(settings.filesDefaultFormat);
            setFilesDisabledExtensions(settings.filesDisabledExtensions);
            setFilesMaxFileSize(typeof settings.filesMaxFileSize === "number" ? bytes(settings.filesMaxFileSize) : settings.filesMaxFileSize);
            setFilesDefaultExpiration(settings.filesDefaultExpiration);
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
            setFeaturesThumbnailsNumberThreads(settings.featuresThumbnailsNumberThreads);
            setFeaturesMetricsEnabled(settings.featuresMetricsEnabled);
            setFeaturesMetricsAdminOnly(settings.featuresMetricsAdminOnly);
            setFeaturesMetricsShowUserSpecific(settings.featuresMetricsShowUserSpecific);
            setInvitesEnabled(settings.invitesEnabled);
            setInvitesLength(settings.invitesLength);
            setWebsiteTitle(settings.websiteTitle);
            setWebsiteTitleLogo(settings.websiteTitleLogo);
            setWebsiteExternalLinks(JSON.stringify(settings.websiteExternalLinks, null, 4));
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
            // setDiscordOnUploadEmbed(settings.discordOnUploadEmbed);
            setDiscordOnShortenWebhookUrl(settings.discordOnShortenWebhookUrl);
            setDiscordOnShortenUsername(settings.discordOnShortenUsername);
            setDiscordOnShortenAvatarUrl(settings.discordOnShortenAvatarUrl);
            setDiscordOnShortenContent(settings.discordOnShortenContent);
            // setDiscordOnShortenEmbed(settings.discordOnShortenEmbed);
            setPwaEnabled(settings.pwaEnabled);
            setPwaTitle(settings.pwaTitle);
            setPwaShortName(settings.pwaShortName);
            setPwaDescription(settings.pwaDescription);
            setPwaThemeColor(settings.pwaThemeColor);
            setPwaBackgroundColor(settings.pwaBackgroundColor);
        }
    }, [settings]);
    
    return (
        <View style={styles.mainContainer}>
            <View style={styles.mainContainer}>
                {settings && (
					<View style={styles.settingsContainer}>
						<Text style={styles.headerText}>Server Settings</Text>
						<KeyboardAwareScrollView style={styles.scrollView}>
							{/* core */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Core</Text>
								
								<View style={styles.switchContainer}>
									<Switch
										value={coreReturnHttpsUrls || false}
										onValueChange={() => setCoreReturnHttpsUrls((prev) => !prev)}
										thumbColor={coreReturnHttpsUrls ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Return HTTPS URLs
									</Text>
								</View>
								
								<Text
									style={styles.inputHeader}
								>
									Default Domain:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setCoreDefaultDomain(content)}
									value={coreDefaultDomain || ""}
									placeholderTextColor="#222c47"
									placeholder="example.com"
								/>
								
								<Text
									style={styles.inputHeader}
								>
									Temporary Directory:
								</Text>
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
							
							{/* chunks */}
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
									<Text
										style={styles.switchText}
									>
										Enable Chunks
									</Text>
								</View>
								
								<Text
									style={styles.inputHeader}
								>
									Max Chunk Size:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setChunksMax(content)}
									value={chunksMax || ""}
									placeholderTextColor="#222c47"
									placeholder="95mb"
								/>
								
								<Text
									style={styles.inputHeader}
								>
									Chunk Size:
								</Text>
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
							
							{/* tasks */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Tasks</Text>
								
								<Text
									style={styles.inputHeader}
								>
									Delete Files Interval:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setTasksDeleteInterval(content)}
									value={tasksDeleteInterval || ""}
									placeholderTextColor="#222c47"
									placeholder="30m"
								/>
								
								<Text
									style={styles.inputHeader}
								>
									Clear Invites Interval:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setTasksClearInvitesInterval(content)}
									value={tasksClearInvitesInterval || ""}
									placeholderTextColor="#222c47"
									placeholder="30m"
								/>
								
								<Text
									style={styles.inputHeader}
								>
									Max Views Interval:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setTasksMaxViewsInterval(content)}
									value={tasksMaxViewsInterval || ""}
									placeholderTextColor="#222c47"
									placeholder="30m"
								/>
								
								<Text
									style={styles.inputHeader}
								>
									Thumbnail Interval:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setTasksThumbnailsInterval(content)}
									value={tasksThumbnailsInterval || ""}
									placeholderTextColor="#222c47"
									placeholder="5m"
								/>
								
								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* mfa */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Multi-Factor Authentication</Text>
								
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
									<Text
										style={styles.switchText}
									>
										Passkeys
									</Text>
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
									<Text
										style={styles.switchText}
									>
										Enable TOTP
									</Text>
								</View>
								
								<Text
									style={styles.inputHeader}
								>
									Issuer:
								</Text>
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
							
							{/* features */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Features</Text>
								
								<View style={styles.switchContainer}>
									<Switch
										value={featuresImageCompression || false}
										onValueChange={() => setFeaturesImageCompression((prev) => !prev)}
										thumbColor={featuresImageCompression ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Image Compression
									</Text>
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
									<Text
										style={styles.switchText}
									>
										/robots.txt
									</Text>
								</View>
								
								<View style={styles.switchContainer}>
									<Switch
										value={featuresHealthcheck || false}
										onValueChange={() => setFeaturesHealthcheck((prev) => !prev)}
										thumbColor={featuresHealthcheck ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Healthcheck
									</Text>
								</View>
								
								<View style={styles.switchContainer}>
									<Switch
										value={featuresUserRegistration || false}
										onValueChange={() => setFeaturesUserRegistration((prev) => !prev)}
										thumbColor={featuresUserRegistration ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										User Registration
									</Text>
								</View>
								
								<View style={styles.switchContainer}>
									<Switch
										value={featuresOauthRegistration || false}
										onValueChange={() => setFeaturesOauthRegistration((prev) => !prev)}
										thumbColor={featuresOauthRegistration ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										OAuth Registration
									</Text>
								</View>
								
								<View style={styles.switchContainer}>
									<Switch
										value={featuresDeleteOnMaxViews || false}
										onValueChange={() => setFeaturesDeleteOnMaxViews((prev) => !prev)}
										thumbColor={featuresDeleteOnMaxViews ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Delete on Max Views
									</Text>
								</View>
								
								<View style={styles.switchContainer}>
									<Switch
										value={featuresMetricsEnabled || false}
										onValueChange={() => setFeaturesMetricsEnabled((prev) => !prev)}
										thumbColor={featuresMetricsEnabled ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Enable Metrics
									</Text>
								</View>
								
								<View style={styles.switchContainer}>
									<Switch
										value={featuresMetricsAdminOnly || false}
										onValueChange={() => setFeaturesMetricsAdminOnly((prev) => !prev)}
										thumbColor={featuresMetricsAdminOnly ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Admin Only Metrics
									</Text>
								</View>
								
								<View style={styles.switchContainer}>
									<Switch
										value={featuresMetricsShowUserSpecific || false}
										onValueChange={() => setFeaturesMetricsShowUserSpecific((prev) => !prev)}
										thumbColor={featuresMetricsShowUserSpecific ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Show User Specific Metrics
									</Text>
								</View>
								
								<Text
									style={styles.inputHeader}
								>
									Thumnails Number Threads:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFeaturesThumbnailsNumberThreads(Math.abs(Number.parseInt(content)))}
									value={featuresThumbnailsNumberThreads ? String(featuresThumbnailsNumberThreads) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="4"
								/>
								
								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>
							
							{/* files */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>Files</Text>
								
								<Text
									style={styles.inputHeader}
								>
									Route:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFilesRoute(content)}
									value={filesRoute || ""}
									placeholderTextColor="#222c47"
									placeholder="/u"
								/>
								
								<Text
									style={styles.inputHeader}
								>
									Length:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFilesLength(Math.abs(Number.parseInt(content)))}
									value={filesLength ? String(filesLength) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="6"
								/>
								
								<View style={styles.switchContainer}>
									<Switch
										value={filesAssumeMimetypes || false}
										onValueChange={() => setFilesAssumeMimetypes((prev) => !prev)}
										thumbColor={filesAssumeMimetypes ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Assume Mimetypes
									</Text>
								</View>

								<View style={styles.switchContainer}>
									<Switch
										value={filesRemoveGpsMetadata || false}
										onValueChange={() => setFilesRemoveGpsMetadata((prev) => !prev)}
										thumbColor={filesRemoveGpsMetadata ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Remove GPS Metadata
									</Text>
								</View>

								<Text
									style={styles.inputHeader}
								>
									Default Format:
								</Text>
								<Select
									data={formats}
									onSelect={(selectedFormat) => setFilesDefaultFormat(selectedFormat.value as typeof filesDefaultFormat)}
									placeholder="Select format..."
									defaultValue={formats.find(format => format.value === "random")}
								/>

								<Text
									style={styles.inputHeader}
								>
									Disabled Extensions:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFilesDisabledExtensions(content.split(", "))}
									value={filesDisabledExtensions?.join(", ") || ""}
									placeholderTextColor="#222c47"
									placeholder="exe, bat, sh"
								/>

								<Text
									style={styles.inputHeader}
								>
									Max File Size:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFilesMaxFileSize(content)}
									value={filesMaxFileSize || ""}
									placeholderTextColor="#222c47"
									placeholder="100mb"
								/>

								<Text
									style={styles.inputHeader}
								>
									Default Expiration:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setFilesDefaultExpiration(content)}
									value={filesDefaultExpiration || ""}
									placeholderTextColor="#222c47"
									placeholder="30d"
									
								/>

								<Text
									style={styles.inputHeader}
								>
									Default Date Format:
								</Text>
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

							{/* url shortener */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>URL Shortener</Text>
								
								<Text
									style={styles.inputHeader}
								>
									Route:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setUrlsRoute(content)}
									value={urlsRoute || ""}
									placeholderTextColor="#222c47"
									placeholder="/go"
								/>
								
								<Text
									style={styles.inputHeader}
								>
									Length:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setUrlsLength(Math.abs(Number.parseInt(content)))}
									value={urlsLength ? String(urlsLength) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="6"
								/>

								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* invites */}
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
									<Text
										style={styles.switchText}
									>
										Enable Invites
									</Text>
								</View>
								
								<Text
									style={styles.inputHeader}
								>
									Length:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setInvitesLength(Math.abs(Number.parseInt(content)))}
									value={invitesLength ? String(invitesLength) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="6"
								/>
								
								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* ratelimit */}
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
									<Text
										style={styles.switchText}
									>
										Enable Ratelimit
									</Text>
								</View>
								
								<View style={styles.switchContainer}>
									<Switch
										value={ratelimitAdminBypass || false}
										onValueChange={() => setRatelimitAdminBypass((prev) => !prev)}
										thumbColor={ratelimitAdminBypass ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Admin Bypass
									</Text>
								</View>
								
								<Text
									style={styles.inputHeader}
								>
									Max Requests:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setRatelimitMax(Math.abs(Number.parseInt(content)))}
									value={ratelimitMax ? String(ratelimitMax) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="10"
								/>

								<Text
									style={styles.inputHeader}
								>
									Window:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setRatelimitWindow(Math.abs(Number.parseInt(content)))}
									value={ratelimitWindow ? String(ratelimitWindow) || "" : ""}
									placeholderTextColor="#222c47"
									placeholder="60"
								/>

								<Text
									style={styles.inputHeader}
								>
									Allow List:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setRatelimitAllowList(content.split(", "))}
									value={ratelimitAllowList?.join(", ") || ""}
									placeholderTextColor="#222c47"
									placeholder="1.1.1.1, 8.8.8.8"
								/>
								
								<Pressable style={styles.settingSaveButton}>
									<Text style={styles.settingSaveButtonText}>Save</Text>
								</Pressable>
							</View>

							{/* website */}
							<View style={styles.settingGroup}>
								<Text
									style={styles.inputHeader}
								>
									Title:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteTitle(content)}
									value={websiteTitle || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Text
									style={styles.inputHeader}
								>
									Title Logo:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteTitleLogo(content)}
									value={websiteTitleLogo || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/logo.png"
								/>

								<Text
									style={styles.inputHeader}
								>
									External Links:
								</Text>
								<TextInput
									style={{
										...styles.textInput,
										...styles.multilneTextInput
									}}
									multiline
									onChangeText={(content) => setWebsiteExternalLinks(content)}
									value={websiteExternalLinks || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/logo.png"
								/>

								<Text
									style={styles.inputHeader}
								>
									Login Background:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteLoginBackground(content)}
									value={websiteLoginBackground || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/background.png"
								/>

								<View style={styles.switchContainer}>
									<Switch
										value={websiteLoginBackgroundBlur || false}
										onValueChange={() => setWebsiteLoginBackgroundBlur((prev) => !prev)}
										thumbColor={websiteLoginBackgroundBlur ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Login Background Blur
									</Text>
								</View>

								<Text
									style={styles.inputHeader}
								>
									Default Avatar:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteDefaultAvatar(content)}
									value={websiteDefaultAvatar || ""}
									placeholderTextColor="#222c47"
									placeholder="/zipline/avatar.png"
								/>

								<Text
									style={styles.inputHeader}
								>
									Terms of Service:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteTos(content)}
									value={websiteTos || ""}
									placeholderTextColor="#222c47"
									placeholder="/zipline/TOS.md"
								/>

								<Text
									style={styles.inputHeader}
								>
									Default Theme:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteThemeDefault(content)}
									value={websiteThemeDefault || ""}
									placeholderTextColor="#222c47"
									placeholder="system"
								/>

								<Text
									style={styles.inputHeader}
								>
									Dark Theme:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setWebsiteThemeDark(content)}
									value={websiteThemeDark || ""}
									placeholderTextColor="#222c47"
									placeholder="builtin:dark_gray"
								/>

								<Text
									style={styles.inputHeader}
								>
									Light Theme:
								</Text>
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

                            {/* oauth */}
							<View style={styles.settingGroup}>
							    <Text style={styles.headerText}>OAuth</Text>
							    
							    <View style={styles.switchContainer}>
									<Switch
										value={oauthBypassLocalLogin || false}
										onValueChange={() => setOauthBypassLocalLogin((prev) => !prev)}
										thumbColor={oauthBypassLocalLogin ? "#2e3e6b" : "#222c47"}
										trackColor={{
											true: "#21273b",
											false: "#181c28",
										}}
									/>
									<Text
										style={styles.switchText}
									>
										Bypass Local Login
									</Text>
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
									<Text
										style={styles.switchText}
									>
										Login Only
									</Text>
								</View>
								
								<View style={styles.settingGroup}>
								    <Text style={{
								        ...styles.oauthSubSettingText,
								        ...styles.oauthSubSettingTextColored
								    }}>Discord</Text>
								
								    <Text
    									style={styles.inputHeader}
    								>
    									Discord Client ID:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthDiscordClientId(content)}
    									value={oauthDiscordClientId || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									Discord Client Secret:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthDiscordClientSecret(content)}
    									value={oauthDiscordClientSecret || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									Discord Redirect URI:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthDiscordRedirectUri(content)}
    									value={oauthDiscordRedirectUri || ""}
    								/>
								</View>
								
								<View style={styles.settingGroup}>
								    <Text style={{
								        ...styles.oauthSubSettingText,
								        ...styles.oauthSubSettingTextColored
								    }}>Google</Text>
								
								    <Text
    									style={styles.inputHeader}
    								>
    									Google Client ID:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthGoogleClientId(content)}
    									value={oauthGoogleClientId || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									Google Client Secret:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthGoogleClientSecret(content)}
    									value={oauthGoogleClientSecret || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									Google Redirect URI:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthGoogleRedirectUri(content)}
    									value={oauthGoogleRedirectUri || ""}
    								/>
								</View>
								
								<View style={styles.settingGroup}>
								    <Text style={{
								        ...styles.oauthSubSettingText,
								        ...styles.oauthSubSettingTextColored
								    }}>GitHub</Text>
								
								    <Text
    									style={styles.inputHeader}
    								>
    									GitHub Client ID:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthGithubClientId(content)}
    									value={oauthGithubClientId || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									GitHub Client Secret:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthGithubClientSecret(content)}
    									value={oauthGithubClientSecret || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									GitHub Redirect URI:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthGithubRedirectUri(content)}
    									value={oauthGithubRedirectUri || ""}
    								/>
								</View>
								
								<View style={styles.settingGroup}>
								    <Text style={styles.oauthSubSettingText}>OpenID Connect</Text>
								
								    <Text
    									style={styles.inputHeader}
    								>
    									OIDC Client ID:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthOidcClientId(content)}
    									value={oauthOidcClientId || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									OIDC Client Secret:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthOidcClientSecret(content)}
    									value={oauthOidcClientSecret || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									OIDC Authorize URL:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthOidcAuthorizeUrl(content)}
    									value={oauthOidcAuthorizeUrl || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									OIDC Token URL:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthOidcTokenUrl(content)}
    									value={oauthOidcTokenUrl || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									OIDC Userinfo URL:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthOidcUserinfoUrl(content)}
    									value={oauthOidcUserinfoUrl || ""}
    								/>
    								
    								<Text
    									style={styles.inputHeader}
    								>
    									OIDC Redirect URL:
    								</Text>
    								<TextInput
    									style={styles.textInput}
    									onChangeText={(content) => setOauthOidcRedirectUri(content)}
    									value={oauthOidcRedirectUri || ""}
    								/>
								</View>
							</View>

							{/* pwa */}
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
									<Text
										style={styles.switchText}
									>
										PWA Enabled
									</Text>
								</View>
								
								<Text
									style={styles.inputHeader}
								>
									Title:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setPwaTitle(content)}
									value={pwaTitle || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Text
									style={styles.inputHeader}
								>
									Short Name:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setPwaShortName(content)}
									value={pwaShortName || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Text
									style={styles.inputHeader}
								>
									Description:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setPwaDescription(content)}
									value={pwaDescription || ""}
									placeholderTextColor="#222c47"
									placeholder="Zipline"
								/>

								<Text
									style={styles.inputHeader}
								>
									Theme Color:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setPwaThemeColor(content)}
									value={pwaThemeColor || ""}
									placeholderTextColor="#222c47"
									placeholder="#000000"
								/>

								<Text
									style={styles.inputHeader}
								>
									Background Color:
								</Text>
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

							{/* pwa */}
							<View style={styles.settingGroup}>
								<Text style={styles.headerText}>HTTP Webhooks</Text>
								
								<Text
									style={styles.inputHeader}
								>
									On Upload:
								</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => setHttpWebhookOnUpload(content)}
									value={httpWebhookOnUpload || ""}
									placeholderTextColor="#222c47"
									placeholder="https://example.com/upload"
								/>

								<Text
									style={styles.inputHeader}
								>
									On Shorten:
								</Text>
								<TextInput
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

							{/* discord webhook */}
						</KeyboardAwareScrollView>
					</View>
                )}
            </View>
        </View>
    )
}
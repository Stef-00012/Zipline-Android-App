import { useAuth } from "@/hooks/useAuth"
import { useShareIntent } from "@/hooks/useShareIntent"
import type { APISettings } from "@/types/zipline"
import { useState, useEffect } from "react"
import { getSettings } from "@/functions/zipline/settings"
import { View, ScrollView } from "react-native"

export default function ServerSettings() {
    useAuth(true)
    useShareIntent()
    
    const [settings, setSettings] = useState(APISettings | null)
    
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
    const [websiteExternalLinks, setWebsiteExternalLinks] = useState<Array<ExternalLink> | null>(null);
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
    const [discordOnUploadEmbed, setDiscordOnUploadEmbed] = useState<UploadEmbed | null>(null);
    const [discordOnShortenWebhookUrl, setDiscordOnShortenWebhookUrl] = useState<string | null>(null);
    const [discordOnShortenUsername, setDiscordOnShortenUsername] = useState<string | null>(null);
    const [discordOnShortenAvatarUrl, setDiscordOnShortenAvatarUrl] = useState<string | null>(null);
    const [discordOnShortenContent, setDiscordOnShortenContent] = useState<string | null>(null);
    const [discordOnShortenEmbed, setDiscordOnShortenEmbed] = useState<ShortenEmbed | null>(null);
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
            setChunksMax(settings.chunksMax);
            setChunksSize(settings.chunksSize);
            setTasksDeleteInterval(settings.tasksDeleteInterval);
            setTasksClearInvitesInterval(settings.tasksClearInvitesInterval);
            setTasksMaxViewsInterval(settings.tasksMaxViewsInterval);
            setTasksThumbnailsInterval(settings.tasksThumbnailsInterval);
            setTasksMetricsInterval(settings.tasksMetricsInterval);
            setFilesRoute(settings.filesRoute);
            setFilesLength(settings.filesLength);
            setFilesDefaultFormat(settings.filesDefaultFormat);
            setFilesDisabledExtensions(settings.filesDisabledExtensions);
            setFilesMaxFileSize(settings.filesMaxFileSize);
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
            setWebsiteExternalLinks(settings.websiteExternalLinks);
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
    
    return (
        <View style={styles.mainContainer}>
            <View style={styles.mainContainer}>
                {settings && (
                    <Text style={styles.headerText}>Server Settings</Text>
                    
                    <ScrollView>
                        <View style={styles.settingGroup}>
                            <Text style={styles.headerText}>Core</Text>
                            
                            <View style={styles.switchContainer}>
            					<Switch
            						value={coreReturnHttpsUrls}
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
            					placeholderTextColor="example.com"
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
            					placeholderTextColor="/tmp/zipline"
            				/>
            				
            				<Pressable style={styles.settingSaveButton}>
                                <Text style={styles.settingSaveButtonText}>Save</Text>
                            </Pressable>
                        </View>
                        
                        <View style={styles.settingGroup}>
                            <Text style={styles.headerText}>Chunks</Text>
                            
                            <View style={styles.switchContainer}>
            					<Switch
            						value={chunksEnabled}
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
            					value={setChunksMax || ""}
            					placeholderTextColor="95mb"
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
            					placeholderTextColor="25mb"
            				/>
            				
            				<Pressable style={styles.settingSaveButton}>
                                <Text style={styles.settingSaveButtonText}>Save</Text>
                            </Pressable>
                        </View>
                        
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
            					placeholderTextColor="30m"
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
            					placeholderTextColor="30m"
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
            					placeholderTextColor="30m"
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
            					placeholderTextColor="5m"
            				/>
            				
            				<Pressable style={styles.settingSaveButton}>
                                <Text style={styles.settingSaveButtonText}>Save</Text>
                            </Pressable>
                        </View>
                        
                        <View style={styles.settingGroup}>
                            <Text style={styles.headerText}>Features</Text>
                            
                            <View style={styles.switchContainer}>
            					<Switch
            						value={featuresImageCompression}
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
            						value={featuresRobotsTxt}
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
            						value={featuresHealthcheck}
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
            						value={featuresUserRegistration}
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
            						value={featuresOauthRegistration}
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
            						value={featuresDeleteOnMaxViews}
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
            						value={featuresMetricsEnabled}
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
            						value={featuresMetricsAdminOnly}
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
            						value={featuresMetricsShowUserSpecific}
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
            					onChangeText={(content) => setFeaturesThumbnailsNumberThreads(content)}
            					value={featuresThumbnailsNumberThreads || ""}
            					placeholderTextColor="4"
            				/>
            				
            				<Pressable style={styles.settingSaveButton}>
                                <Text style={styles.settingSaveButtonText}>Save</Text>
                            </Pressable>
                        </View>
                        
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
            					placeholderTextColor="30m"
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
            					placeholderTextColor="30m"
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
            					placeholderTextColor="30m"
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
            					placeholderTextColor="5m"
            				/>
            				
            				<Pressable style={styles.settingSaveButton}>
                                <Text style={styles.settingSaveButtonText}>Save</Text>
                            </Pressable>
                        </View>
                        
                        <View style={styles.settingGroup}>
                            <Text style={styles.headerText}>Multi-Factor Authentication</Text>
                            
                            <View style={styles.switchContainer}>
            					<Switch
            						value={mfaPasskeys}
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
            						value={mfaTotpEnabled}
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
            					value={setMfaTotpIssuer || ""}
            					placeholderTextColor="Zipline"
            				/>
            				
            				<Pressable style={styles.settingSaveButton}>
                                <Text style={styles.settingSaveButtonText}>Save</Text>
                            </Pressable>
                        </View>
                    </ScrollView>
                )}
            </View>
        </View>
    )
}
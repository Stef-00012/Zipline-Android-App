import { Pressable, ScrollView, Text, View, ToastAndroid, TextInput } from "react-native";
import type { APISettings, APIURLs, DashURL } from "@/types/zipline";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { isAuthenticated } from "@/functions/zipline/auth";
import { getSettings } from "@/functions/zipline/settings";
import { Row, Table } from "react-native-table-component";
import { useShareIntentContext } from "expo-share-intent";
import { createURL, type CreateURLParams, deleteURL, editURL, type EditURLOptions, getURLs } from "@/functions/zipline/urls";
import { timeDifference } from "@/functions/util";
import { styles } from "@/styles/urls/urls";
import { useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import {
	type ExternalPathString,
	Link,
	useFocusEffect,
	useRouter,
} from "expo-router";
import Popup from "@/components/Popup";

export default function Urls() {
	const router = useRouter();
	const { hasShareIntent } = useShareIntentContext();

	// biome-ignore lint/correctness/useExhaustiveDependencies: .
	useEffect(() => {
		if (hasShareIntent) {
			router.replace({
				pathname: "/shareintent",
			});
		}
	}, [hasShareIntent]);

	useFocusEffect(() => {
		(async () => {
			const authenticated = await isAuthenticated();

			if (!authenticated) return router.replace("/login");
		})();
	});

	const [urls, setUrls] = useState<APIURLs | null>(null);
	const [settings, setSettings] = useState<APISettings | null>(null);

	const [createNewUrl, setCreateNewUrl] = useState<boolean>(false);

	const [newUrl, setNewUrl] = useState<string | null>(null);
	const [newUrlVanity, setNewUrlVanity] = useState<string | null>(null);
	const [newUrlMaxViews, setNewUrlMaxViews] = useState<string | null>(null);
	const [newUrlPassword, setNewUrlPassword] = useState<string | null>(null);

	const [newUrlError, setNewUrlError] = useState<string>();

	const [urlToEdit, setUrlToEdit] = useState<APIURLs[0] | null>(null);

	const [editUrlDestination, setEditUrlDestination] = useState<string | null>(null);
	const [editUrlVanity, setEditUrlVanity] = useState<string | null>(null);
	const [editUrlMaxViews, setEditUrlMaxViews] = useState<string | null>(null);
	const [editUrlPassword, setEditUrlPassword] = useState<string | null>(null);

	const [editUrlOriginalVanity, setEditUrlOriginalVanity] = useState<string | null>(null);
	const [editUrlError, setEditUrlError] = useState<string>();

	const dashUrl = db.get("url") as DashURL | null;

	const urlRegex = /^http:\/\/(.*)?|https:\/\/(.*)?$/;

	useEffect(() => {
		(async () => {
			const urls = await getURLs();
			const settings = await getSettings();

			setUrls(urls);
			setSettings(settings);
		})();
	}, []);

	useEffect(() => {
		if (urlToEdit) {
			setEditUrlDestination(urlToEdit.destination);
			setEditUrlVanity(urlToEdit.vanity);
			setEditUrlOriginalVanity(urlToEdit.vanity);
			setEditUrlMaxViews(urlToEdit.maxViews?.toString() || null);
		}
	}, [urlToEdit])

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup hidden={!createNewUrl} onClose={() => {
					setCreateNewUrl(false)
					setNewUrl(null)
					setNewUrlVanity(null)
					setNewUrlMaxViews(null)
					setNewUrlPassword(null)
					setNewUrlError(undefined)
				}}>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Shorten URL</Text>
						{newUrlError && <Text style={styles.errorText}>{newUrlError}</Text>}

						<Text style={styles.popupHeaderText}>URL:</Text>
						<TextInput
							style={styles.textInput}
							onChangeText={(content) => {
								setNewUrl((content as DashURL) || null);
							}}
							value={newUrl || ""}
							keyboardType="url"
							placeholder="https://google.com"
							placeholderTextColor="#222c47"
						/>
		
						<Text style={styles.popupHeaderText}>Vanity:</Text>
						<TextInput
							style={styles.textInput}
							onChangeText={(content) => {
								setNewUrlVanity(content || null);
							}}
							value={newUrlVanity || ""}
							placeholder="google"
							placeholderTextColor="#222c47"
						/>
		
						<Text style={styles.popupHeaderText}>Max Views:</Text>
						<TextInput
							style={styles.textInput}
							keyboardType="numeric"
							onChangeText={(content) => {
								setNewUrlMaxViews(content || null);
							}}
							value={newUrlMaxViews || ""}
							placeholder="0"
							placeholderTextColor="#222c47"
						/>
		
						<Text style={styles.popupHeaderText}>Password:</Text>
						<TextInput
							style={styles.textInput}
							onChangeText={(content) => {
								setNewUrlPassword(content || null);
							}}
							value={newUrlPassword || ""}
							secureTextEntry={true}
							keyboardType="visible-password"
							placeholder="myPassword"
							placeholderTextColor="#222c47"
						/>
		
						<Pressable
							style={styles.button}
							onPress={async () => {
								setNewUrlError(undefined);
		
								if (!newUrl) return setNewUrlError("Please insert a URL");
								if (!urlRegex.test(newUrl))
									return setNewUrlError("Please insert a valid URL");
		
								const urlData: CreateURLParams = {
									destination: newUrl,
								};
		
								if (newUrlVanity) urlData.vanity = newUrlVanity;
								if (newUrlMaxViews) urlData.maxViews = Number.parseInt(newUrlMaxViews);
								if (newUrlPassword) urlData.password = newUrlPassword;
		
								const shortenedUrlData = await createURL(urlData);
		
								if (!shortenedUrlData)
									return setNewUrlError("An error occurred while shortening the URL");
		
								const saved = await Clipboard.setStringAsync(shortenedUrlData.url);
		
								setNewUrl(null);
								setNewUrlVanity(null);
								setNewUrlMaxViews(null);
								setNewUrlPassword(null);

								const newUrls = await getURLs()

								setUrls(newUrls)

								setCreateNewUrl(false);
		
								if (saved)
									return ToastAndroid.show(
										"Shortened URL copied to clipboard",
										ToastAndroid.SHORT,
									);

								ToastAndroid.show(
									"Failed to copy the URL to the clipboard",
									ToastAndroid.SHORT,
								);
							}}
						>
							<Text style={styles.buttonText}>Shorten</Text>
						</Pressable>

						<Text
							style={styles.popupSubHeaderText}
						>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup hidden={!urlToEdit} onClose={() => {
					setUrlToEdit(null)
					setEditUrlDestination(null)
					setEditUrlVanity(null)
					setEditUrlMaxViews(null)
					setEditUrlPassword(null)
					setEditUrlError(undefined)
				}}>
					<View style={styles.popupContent}>
						{urlToEdit && (
							<View>
								<Text style={styles.mainHeaderText}>Edit URL "{urlToEdit.vanity || urlToEdit.code}"</Text>
								{editUrlError && <Text style={styles.errorText}>{editUrlError}</Text>}

								<Text style={styles.popupHeaderText}>URL:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => {
										setEditUrlDestination((content as DashURL) || null);
									}}
									value={editUrlDestination || ""}
									keyboardType="url"
									placeholder="https://google.com"
									placeholderTextColor="#222c47"
								/>
				
								<Text style={styles.popupHeaderText}>Vanity:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => {
										setEditUrlVanity(content || null);
									}}
									value={editUrlVanity || ""}
									placeholder="google"
									placeholderTextColor="#222c47"
								/>
				
								<Text style={styles.popupHeaderText}>Max Views:</Text>
								<TextInput
									style={styles.textInput}
									keyboardType="numeric"
									onChangeText={(content) => {
										setEditUrlMaxViews(content || null);
									}}
									value={editUrlMaxViews || ""}
									placeholder="0"
									placeholderTextColor="#222c47"
								/>
				
								<Text style={styles.popupHeaderText}>Password:</Text>
								<TextInput
									style={styles.textInput}
									onChangeText={(content) => {
										setEditUrlPassword(content || null);
									}}
									value={editUrlPassword || ""}
									secureTextEntry={true}
									keyboardType="visible-password"
									placeholder="myPassword"
									placeholderTextColor="#222c47"
								/>
				
								<Pressable
									style={styles.button}
									onPress={async () => {
										setEditUrlError(undefined);
				
										if (!editUrlDestination) return setEditUrlError("Please insert a URL");
										if (!urlRegex.test(editUrlDestination))
											return setEditUrlError("Please insert a valid URL");
				
										const urlData: EditURLOptions = {
											destination: editUrlDestination,
										};
				
										if (editUrlVanity && editUrlVanity !== editUrlOriginalVanity) urlData.vanity = editUrlVanity;
										if (editUrlMaxViews) urlData.maxViews = Number.parseInt(editUrlMaxViews);
										if (editUrlPassword) urlData.password = editUrlPassword;

										const editedUrlData = await editURL(urlToEdit.id, urlData);

										if (!editedUrlData)
											return setEditUrlError("An error occurred while shortening the URL");

										setEditUrlDestination(null);
										setEditUrlVanity(null);
										setEditUrlMaxViews(null);
										setEditUrlPassword(null);

										const newUrls = await getURLs()

										setUrls(newUrls)

										setUrlToEdit(null);
									}}
								>
									<Text style={styles.buttonText}>Edit</Text>
								</Pressable>

								<Text
									style={styles.popupSubHeaderText}
								>
									Press outside to close this popup
								</Text>
							</View>
						)}
					</View>
				</Popup>

				{urls && settings && dashUrl ? (
					<View style={{ flex: 1 }}>
						<View style={styles.header}>
							<Text style={styles.headerText}>URLs</Text>
							<View style={styles.headerButtons}>
								<Pressable
									style={styles.headerButton}
									onPress={() => {
										setCreateNewUrl(true);
									}}
								>
									<MaterialIcons
										name="add-link"
										size={30}
										color={styles.headerButton.color}
									/>
								</Pressable>
							</View>
						</View>

						<View style={{ ...styles.urlsContainer, flex: 1 }}>
							<ScrollView
								showsHorizontalScrollIndicator={false}
								horizontal={true}
							>
								<View>
									<Table>
										<Row
											data={[
												"Code",
												"Vanity",
												"URL",
												"Views",
												"Max Views",
												"Created",
												"Actions",
											]}
											widthArr={[80, 100, 200, 100, 100, 130, 130]}
											style={styles.tableHeader}
											textStyle={styles.rowText}
										/>
									</Table>
									<ScrollView
										showsVerticalScrollIndicator={false}
										style={styles.tableVerticalScroll}
									>
										<Table>
											{urls.map((url, index) => {
												const code = (
													<Link
														key={url.id}
														href={
															`${dashUrl}${settings.urlsRoute === "/" ? "" : settings.urlsRoute}/${url.code}` as ExternalPathString
														}
														style={{
															...styles.rowText,
															...styles.link,
														}}
													>
														{url.code}
													</Link>
												);

												const vanity = (
													<Link
														key={url.id}
														href={
															`${dashUrl}${settings.urlsRoute === "/" ? "" : settings.urlsRoute}/${url.vanity}` as ExternalPathString
														}
														style={{
															...styles.rowText,
															...styles.link,
														}}
													>
														{url.vanity}
													</Link>
												);

												const noVanity = (
													<Text style={styles.rowText}>None</Text>
												);

												const destination = (
													<Link
														key={url.id}
														href={url.destination as ExternalPathString}
														style={{
															...styles.rowText,
															...styles.link,
														}}
													>
														{url.destination}
													</Link>
												);

												const views = (
													<Text style={styles.rowText}>{url.views}</Text>
												);

												const maxViews = (
													<Text style={styles.rowText}>
														{url.maxViews || "Unlimited"}
													</Text>
												);

												const created = (
													<Text style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(url.createdAt),
														)}
													</Text>
												);

												const actions = (
													<View style={styles.actionsContainer}>
														<Pressable
															style={styles.actionButton}
															onPress={async () => {
																const urlDest = url.vanity
																	? `${dashUrl}${settings.urlsRoute === "/" ? "" : settings.urlsRoute}/${url.vanity}`
																	: `${dashUrl}${settings.urlsRoute === "/" ? "" : settings.urlsRoute}/${url.code}`;

																const saved =
																	await Clipboard.setStringAsync(urlDest);

																if (saved)
																	return ToastAndroid.show(
																		"URL copied to clipboard",
																		ToastAndroid.SHORT,
																	);

																return ToastAndroid.show(
																	"Failed to paste to the clipboard",
																	ToastAndroid.SHORT,
																);
															}}
														>
															<MaterialIcons
																name="content-copy"
																size={20}
																color={"white"}
															/>
														</Pressable>

														<Pressable
															style={styles.actionButton}
															onPress={() => {
																setUrlToEdit(url)
															}}
														>
															<MaterialIcons
																name="edit"
																size={20}
																color={"white"}
															/>
														</Pressable>

														<Pressable
															style={{
																...styles.actionButton,
																...styles.actionButtonDanger,
															}}
															onPress={async () => {
																const urlId = url.id;

																const success = await deleteURL(urlId);

																if (!success) return ToastAndroid.show(
                                                                    `Failed to delete the url "${url.vanity || url.code}"`,
                                                                    ToastAndroid.SHORT
                                                                )

																const newUrls = urls.filter(uri => url.id !== uri.id)

																setUrls(newUrls)

                                                                ToastAndroid.show(
                                                                    `Deleted the url "${url.vanity || url.code}"`,
                                                                    ToastAndroid.SHORT
                                                                )
															}}
														>
															<MaterialIcons
																name="delete"
																size={20}
																color={"white"}
															/>
														</Pressable>
													</View>
												);

												let rowStyle = styles.row;

												if (index === 0)
													rowStyle = {
														...styles.row,
														...styles.firstRow,
													};

												if (index === urls.length - 1)
													rowStyle = {
														...styles.row,
														...styles.lastRow,
													};

												return (
													<Row
														key={url.id}
														data={[
															code,
															url.vanity ? vanity : noVanity,
															destination,
															views,
															maxViews,
															created,
															actions,
														]}
														widthArr={[80, 100, 200, 100, 100, 130, 130]}
														style={rowStyle}
														textStyle={styles.rowText}
													/>
												);
											})}
										</Table>
									</ScrollView>
								</View>
							</ScrollView>
						</View>
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

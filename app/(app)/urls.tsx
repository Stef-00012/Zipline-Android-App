import { ScrollView, Text, View, ToastAndroid } from "react-native";
import type { APISettings, APIURLs, DashURL } from "@/types/zipline";
import { getSettings } from "@/functions/zipline/settings";
import { Row, Table } from "react-native-table-component";
import {
	createURL,
	type CreateURLParams,
	deleteURL,
	editURL,
	type EditURLOptions,
	getURLs,
} from "@/functions/zipline/urls";
import { timeDifference } from "@/functions/util";
import { styles } from "@/styles/urls";
import { useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import { type ExternalPathString, Link } from "expo-router";
import Popup from "@/components/Popup";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import TextInput from "@/components/TextInput";
import Switch from "@/components/Switch";
import Button from "@/components/Button";

export default function Urls() {
	useAuth();
	useShareIntent();

	const [urls, setUrls] = useState<APIURLs | null>(null);
	const [settings, setSettings] = useState<APISettings | null>(null);

	const [createNewUrl, setCreateNewUrl] = useState<boolean>(false);

	const [newUrl, setNewUrl] = useState<string | null>(null);
	const [newUrlVanity, setNewUrlVanity] = useState<string | null>(null);
	const [newUrlMaxViews, setNewUrlMaxViews] = useState<string | null>(null);
	const [newUrlPassword, setNewUrlPassword] = useState<string | null>(null);
	const [newUrlEnabled, setNewUrlEnabled] = useState<boolean>(true);

	const [newUrlError, setNewUrlError] = useState<string>();

	const [urlToEdit, setUrlToEdit] = useState<APIURLs[0] | null>(null);

	const [editUrlDestination, setEditUrlDestination] = useState<string | null>(
		null,
	);
	const [editUrlVanity, setEditUrlVanity] = useState<string | null>(null);
	const [editUrlMaxViews, setEditUrlMaxViews] = useState<string | null>(null);
	const [editUrlPassword, setEditUrlPassword] = useState<string | null>(null);
	const [editUrlEnabled, setEditUrlEnabled] = useState<boolean>(true);

	const [editUrlOriginalVanity, setEditUrlOriginalVanity] = useState<
		string | null
	>(null);
	const [editUrlError, setEditUrlError] = useState<string>();

	const dashUrl = db.get("url") as DashURL | null;

	const urlRegex = /^http:\/\/(.*)?|https:\/\/(.*)?$/;

	useEffect(() => {
		(async () => {
			const urls = await getURLs();
			const settings = await getSettings();

			setUrls(typeof urls === "string" ? null : urls);
			setSettings(typeof settings === "string" ? null : settings);
		})();
	}, []);

	useEffect(() => {
		if (urlToEdit) {
			setEditUrlDestination(urlToEdit.destination);
			setEditUrlVanity(urlToEdit.vanity);
			setEditUrlOriginalVanity(urlToEdit.vanity);
			setEditUrlMaxViews(urlToEdit.maxViews?.toString() || null);
			setEditUrlEnabled(urlToEdit.enabled ?? true);
		}
	}, [urlToEdit]);

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup
					hidden={!createNewUrl}
					onClose={() => {
						setCreateNewUrl(false);
						setNewUrl(null);
						setNewUrlVanity(null);
						setNewUrlMaxViews(null);
						setNewUrlPassword(null);
						setNewUrlEnabled(true);
						setNewUrlError(undefined);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Shorten URL</Text>
						{newUrlError && <Text style={styles.errorText}>{newUrlError}</Text>}

						<TextInput
							title="URL:"
							onValueChange={(content) => {
								setNewUrl((content as DashURL) || null);
							}}
							value={newUrl || ""}
							keyboardType="url"
							placeholder="https://google.com"
						/>

						<TextInput
							title="Vanity:"
							onValueChange={(content) => {
								setNewUrlVanity(content || null);
							}}
							value={newUrlVanity || ""}
							placeholder="google"
						/>

						<TextInput
							title="Max Views:"
							keyboardType="numeric"
							onValueChange={(content) => {
								setNewUrlMaxViews(content || null);
							}}
							value={newUrlMaxViews || ""}
							placeholder="0"
						/>

						<TextInput
							title="Password:"
							onValueChange={(content) => {
								setNewUrlPassword(content || null);
							}}
							value={newUrlPassword || ""}
							password
							placeholder="myPassword"
						/>

						<Switch
							title="Enabled"
							value={newUrlEnabled}
							onValueChange={() => setNewUrlEnabled((prev) => !prev)}
						/>

						<Button
							color="#323ea8"
							text="Shorten"
							margin={{
								top: 5,
							}}
							onPress={async () => {
								setNewUrlError(undefined);

								if (!newUrl) return setNewUrlError("Please insert a URL");
								if (!urlRegex.test(newUrl))
									return setNewUrlError("Please insert a valid URL");

								const urlData: CreateURLParams = {
									destination: newUrl,
									enabled: newUrlEnabled ?? true,
								};

								if (newUrlVanity) urlData.vanity = newUrlVanity;
								if (newUrlMaxViews)
									urlData.maxViews = Number.parseInt(newUrlMaxViews);
								if (newUrlPassword) urlData.password = newUrlPassword;

								const shortenedUrlData = await createURL(urlData);

								if (typeof shortenedUrlData === "string")
									return setNewUrlError(shortenedUrlData);

								const saved = await Clipboard.setStringAsync(
									shortenedUrlData.url,
								);

								setNewUrl(null);
								setNewUrlVanity(null);
								setNewUrlMaxViews(null);
								setNewUrlPassword(null);
								setNewUrlEnabled(true);

								const newUrls = await getURLs();

								setUrls(typeof newUrls === "string" ? null : newUrls);

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
						/>

						<Text style={styles.popupSubHeaderText}>
							Press outside to close this popup
						</Text>
					</View>
				</Popup>

				<Popup
					hidden={!urlToEdit}
					onClose={() => {
						setUrlToEdit(null);
						setEditUrlDestination(null);
						setEditUrlVanity(null);
						setEditUrlMaxViews(null);
						setEditUrlPassword(null);
						setEditUrlEnabled(true);
						setEditUrlError(undefined);
					}}
				>
					<View style={styles.popupContent}>
						{urlToEdit && (
							<View>
								<Text style={styles.mainHeaderText}>
									Edit URL "{urlToEdit.vanity || urlToEdit.code}"
								</Text>
								{editUrlError && (
									<Text style={styles.errorText}>{editUrlError}</Text>
								)}

								<TextInput
									title="URL:"
									onValueChange={(content) => {
										setEditUrlDestination((content as DashURL) || null);
									}}
									value={editUrlDestination || ""}
									keyboardType="url"
									placeholder="https://google.com"
								/>

								<TextInput
									title="Vanity:"
									onValueChange={(content) => {
										setEditUrlVanity(content || null);
									}}
									value={editUrlVanity || ""}
									placeholder="google"
								/>

								<TextInput
									title="Max Views:"
									keyboardType="numeric"
									onValueChange={(content) => {
										setEditUrlMaxViews(content || null);
									}}
									value={editUrlMaxViews || ""}
									placeholder="0"
								/>

								<TextInput
									title="Password:"
									onValueChange={(content) => {
										setEditUrlPassword(content || null);
									}}
									value={editUrlPassword || ""}
									password
									placeholder="myPassword"
								/>

								<Switch
									title="Enabled"
									value={editUrlEnabled}
									onValueChange={() => setEditUrlEnabled((prev) => !prev)}
								/>

								<Button
									color="#323ea8"
									text="Edit"
									margin={{
										top: 5,
									}}
									onPress={async () => {
										setEditUrlError(undefined);

										if (!editUrlDestination)
											return setEditUrlError("Please insert a URL");
										if (!urlRegex.test(editUrlDestination))
											return setEditUrlError("Please insert a valid URL");

										const urlData: EditURLOptions = {
											destination: editUrlDestination,
											enabled: editUrlEnabled ?? true,
										};

										if (
											editUrlVanity &&
											editUrlVanity !== editUrlOriginalVanity
										)
											urlData.vanity = editUrlVanity;
										if (editUrlMaxViews)
											urlData.maxViews = Number.parseInt(editUrlMaxViews);
										if (editUrlPassword) urlData.password = editUrlPassword;

										const editedUrlData = await editURL(urlToEdit.id, urlData);

										if (typeof editedUrlData === "string")
											return setEditUrlError(editedUrlData);

										setEditUrlDestination(null);
										setEditUrlVanity(null);
										setEditUrlMaxViews(null);
										setEditUrlPassword(null);
										setEditUrlEnabled(true);

										const newUrls = await getURLs();

										setUrls(typeof newUrls === "string" ? null : newUrls);

										setUrlToEdit(null);
									}}
								/>

								<Text style={styles.popupSubHeaderText}>
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
								<Button
									onPress={() => {
										setCreateNewUrl(true);
									}}
									icon="add-link"
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

						<View style={{ ...styles.urlsContainer, flex: 1 }}>
							<ScrollView showsHorizontalScrollIndicator={false} horizontal>
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
												"Enabled",
												"Actions",
											]}
											widthArr={[80, 100, 200, 100, 100, 130, 50, 130]}
											style={styles.tableHeader}
											textStyle={{
												...styles.rowText,
												...styles.headerRow,
											}}
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

												const enabled = (
													<Text style={styles.rowText}>
														{url.enabled ? "Yes" : "No"}
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
														<Button
															icon="content-copy"
															color="#323ea8"
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
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>

														<Button
															icon="edit"
															color="#323ea8"
															onPress={() => {
																setUrlToEdit(url);
															}}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>

														<Button
															icon="delete"
															color="#CF4238"
															onPress={async () => {
																const urlId = url.id;

																const success = await deleteURL(urlId);

																if (typeof success === "string")
																	return ToastAndroid.show(
																		`Failed to delete the url "${url.vanity || url.code}"`,
																		ToastAndroid.SHORT,
																	);

																const newUrls = urls.filter(
																	(uri) => url.id !== uri.id,
																);

																setUrls(newUrls);

																ToastAndroid.show(
																	`Deleted the url "${url.vanity || url.code}"`,
																	ToastAndroid.SHORT,
																);
															}}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>
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
															enabled,
															actions,
														]}
														widthArr={[80, 100, 200, 100, 100, 130, 50, 130]}
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

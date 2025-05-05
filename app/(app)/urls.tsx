import type { APISettings, APIURLs, DashURL } from "@/types/zipline";
import { Text, View, ToastAndroid, ScrollView } from "react-native";
import { type ExternalPathString, Link } from "expo-router";
import { getSettings } from "@/functions/zipline/settings";
import { useShareIntent } from "@/hooks/useShareIntent";
import LargeURLView from "@/components/LargeURLView";
import { timeDifference } from "@/functions/util";
import { searchKeyNames } from "@/constants/urls";
import TextInput from "@/components/TextInput";
import { useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import Switch from "@/components/Switch";
import Button from "@/components/Button";
import Popup from "@/components/Popup";
import { styles } from "@/styles/urls";
import Table from "@/components/Table";
import {
	type CreateURLParams,
	type EditURLOptions,
	createURL,
	deleteURL,
	editURL,
	getURLs,
} from "@/functions/zipline/urls";
import SkeletonTable from "@/components/skeleton/Table";
import Skeleton from "@/components/skeleton/Skeleton";

export type URLActions =
	| "copyShortLink"
	| "copyDestination"
	| "edit"
	| "delete";

const urlRegex = /^http:\/\/(.*)?|https:\/\/(.*)?$/;

export default function Urls() {
	useAuth();
	useShareIntent();

	const urlsCompactView = db.get("urlsCompactView");

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

	const [sortKey, setSortKey] = useState<{
		id:
			| "code"
			| "vanity"
			| "destination"
			| "views"
			| "maxViews"
			| "createdAt"
			| "enabled"
			| "id";
		sortOrder: "asc" | "desc";
	}>({
		id: "createdAt",
		sortOrder: "asc",
	});

	const [showSearch, setShowSearch] = useState<boolean>(false);
	const [searchTerm, setSearchTerm] = useState<string>("");
	const [searchPlaceholder, setSearchPlaceholder] = useState<string>("");
	const [searchKey, setSearchKey] = useState<
		"code" | "vanity" | "destination" | "views" | "maxViews" | "id"
	>();

	const [compactModeEnabled, setCompactModeEnabled] = useState<boolean>(
		urlsCompactView === "true",
	);

	const dashUrl = db.get("url") as DashURL | null;

	useEffect(() => {
		(async () => {
			const settings = await getSettings();

			setSettings(typeof settings === "string" ? null : settings);
		})();
	}, []);

	// biome-ignore lint/correctness/useExhaustiveDependencies: should only trigger when search term changes
	useEffect(() => {
		fetchURls();
	}, [searchTerm]);

	async function fetchURls(force = false) {
		if (!force && searchKey && searchKey !== "vanity" && searchKey !== "destination") return;
		if (force && searchKey && searchKey !== "code" && searchKey !== "vanity" && searchKey !== "destination") setSearchKey(undefined)
		
		const urls = await getURLs({
			searchField: searchKey as "code" | "vanity" | "destination",
			searchQuery: searchTerm,
		});

		setUrls(typeof urls === "string" ? null : urls);
	}

	// biome-ignore lint/correctness/useExhaustiveDependencies: search term should be resetted when search key changes
	useEffect(() => {
		setSearchPlaceholder("");
	}, [searchKey]);

	useEffect(() => {
		if (urlToEdit) {
			setEditUrlDestination(urlToEdit.destination);
			setEditUrlVanity(urlToEdit.vanity);
			setEditUrlOriginalVanity(urlToEdit.vanity);
			setEditUrlMaxViews(urlToEdit.maxViews?.toString() || null);
			setEditUrlEnabled(urlToEdit.enabled ?? true);
		}
	}, [urlToEdit]);

	async function onAction(type: URLActions, url: APIURLs[0]) {
		switch (type) {
			case "copyShortLink": {
				const urlDest = url.vanity
					? `${dashUrl}${settings?.urlsRoute === "/" ? "" : settings?.urlsRoute || "/go"}/${url.vanity}`
					: `${dashUrl}${settings?.urlsRoute === "/" ? "" : settings?.urlsRoute || "/go"}/${url.code}`;

				const saved = await Clipboard.setStringAsync(urlDest);

				if (saved)
					return ToastAndroid.show(
						"URL copied to clipboard",
						ToastAndroid.SHORT,
					);

				return ToastAndroid.show(
					"Failed to paste to the clipboard",
					ToastAndroid.SHORT,
				);
			}

			case "edit": {
				return setUrlToEdit(url);
			}

			case "delete": {
				const urlId = url.id;

				const success = await deleteURL(urlId);

				if (typeof success === "string")
					return ToastAndroid.show(
						`Failed to delete the url "${url.vanity || url.code}"`,
						ToastAndroid.SHORT,
					);

				await fetchURls();

				return ToastAndroid.show(
					`Deleted the url "${url.vanity || url.code}"`,
					ToastAndroid.SHORT,
				);
			}

			case "copyDestination": {
				const urlDest = url.destination;

				const saved = await Clipboard.setStringAsync(urlDest);

				if (saved)
					return ToastAndroid.show(
						"URL Destination copied to clipboard",
						ToastAndroid.SHORT,
					);

				return ToastAndroid.show(
					"Failed to paste to the clipboard",
					ToastAndroid.SHORT,
				);
			}
		}
	}

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

								await fetchURls(true)

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

				<View style={styles.header}>
					<Text style={styles.headerText}>URLs</Text>
					<View style={styles.headerButtons}>
						<Button
							onPress={() => {
								setCreateNewUrl(true);
							}}
							icon="add-link"
							color="transparent"
							iconColor={urls && dashUrl ? "#2d3f70" : "#2d3f7055"}
							borderColor="#222c47"
							borderWidth={2}
							iconSize={30}
							padding={4}
							rippleColor="#283557"
							disabled={!urls || !dashUrl}
							margin={{
								left: 2,
								right: 2,
							}}
						/>

						<Button
							onPress={() => {
								db.set(
									"urlsCompactView",
									compactModeEnabled ? "false" : "true",
								);

								setCompactModeEnabled((prev) => !prev);
							}}
							icon={compactModeEnabled ? "view-module" : "view-agenda"}
							color="transparent"
							iconColor={urls && dashUrl ? "#2d3f70" : "#2d3f7055"}
							borderColor="#222c47"
							borderWidth={2}
							iconSize={30}
							padding={4}
							rippleColor="#283557"
							disabled={!urls || !dashUrl}
							margin={{
								left: 2,
								right: 2,
							}}
						/>
					</View>
				</View>

				{showSearch && (
					<View style={styles.mainSearchContainer}>
						{showSearch && searchKey && (
							<>
								<View style={styles.searchContainer}>
									<Text style={styles.searchHeader}>
										Search by {searchKeyNames[searchKey]}
									</Text>

									<Button
										onPress={() => setShowSearch(false)}
										icon="close"
										color="#191b27"
										width={30}
										height={30}
										padding={5}
									/>
								</View>

								<TextInput
									placeholder="Search..."
									defaultValue={searchPlaceholder}
									onValueChange={(text) => setSearchPlaceholder(text)}
									onSubmitEditing={(event) => {
										const searchText = event.nativeEvent.text;

										setSearchTerm(searchText);
										setShowSearch(false);
									}}
									returnKeyType="search"
								/>
							</>
						)}
					</View>
				)}

				<View style={{ flex: 1 }}>
					<View style={styles.urlsContainer}>
						{urls && dashUrl ? (
							<>
								{compactModeEnabled ? (
									<Table
										headerRow={[
											{
												row: "Code",
												id: "code",
												sortable: true,
												searchable: true,
											},
											{
												row: "Vanity",
												id: "vanity",
												sortable: true,
												searchable: true,
											},
											{
												row: "Destination",
												id: "destination",
												sortable: true,
												searchable: true,
											},
											{
												row: "Views",
												id: "views",
												sortable: true,
												searchable: true
											},
											{
												row: "Max Views",
												id: "maxViews",
												sortable: true,
												searchable: true
											},
											{
												row: "Created",
												id: "createdAt",
												sortable: true,
											},
											{
												row: "Enabled",
												id: "enabled",
												sortable: true,
											},
											{
												row: "ID",
												id: "id",
												sortable: true,
												searchable: true
											},
											{
												row: "Actions",
											},
										]}
										sortKey={sortKey}
										onSortOrderChange={(key, order) => {
											setSortKey({
												id: key as typeof sortKey.id,
												sortOrder: order,
											});
										}}
										onSearch={(key) => {
											setShowSearch(true);
											setSearchKey(key as typeof searchKey);
										}}
										rowWidth={[100, 120, 300, 110, 140, 130, 100, 220, 130]}
										rows={urls
											.filter((invite) => {
												if (!searchKey) return invite;
												
												let filterKey = invite[searchKey];

												if (searchKey === "maxViews" && !filterKey) filterKey = "0"

												return String(filterKey)
													.toLowerCase()
													.includes(searchTerm.toLowerCase());
											})
											.sort((a, b) => {
												const compareKeyA =
													sortKey.id === "createdAt"
														? new Date(a[sortKey.id])
														: a[sortKey.id];

												const compareKeyB =
													sortKey.id === "createdAt"
														? new Date(b[sortKey.id])
														: b[sortKey.id];

												let result = 0;

												if (
													typeof compareKeyA === "string" &&
													typeof compareKeyB === "string"
												)
													result = compareKeyA.localeCompare(compareKeyB);
												else if (
													compareKeyA instanceof Date &&
													compareKeyB instanceof Date
												)
													result =
														compareKeyA.getTime() - compareKeyB.getTime();
												else result = Number(compareKeyA) - Number(compareKeyB);

												return sortKey.sortOrder === "desc" ? -result : result;
											})
											.map((url) => {
												const code = (
													<Link
														key={url.id}
														href={
															`${dashUrl}${settings?.urlsRoute === "/" ? "" : settings?.urlsRoute || "/go"}/${url.code}` as ExternalPathString
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
													<>
														{url.vanity ? (
															<Link
																key={url.id}
																href={
																	`${dashUrl}${settings?.urlsRoute === "/" ? "" : settings?.urlsRoute || "/go"}/${url.vanity}` as ExternalPathString
																}
																style={{
																	...styles.rowText,
																	...styles.link,
																}}
															>
																{url.vanity}
															</Link>
														) : (
															<Text key={url.id} style={styles.rowText}>
																None
															</Text>
														)}
													</>
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
													<Text key={url.id} style={styles.rowText}>
														{url.views}
													</Text>
												);

												const maxViews = (
													<Text key={url.id} style={styles.rowText}>
														{url.maxViews || "Unlimited"}
													</Text>
												);

												const enabled = (
													<Text key={url.id} style={styles.rowText}>
														{url.enabled ? "Yes" : "No"}
													</Text>
												);

												const created = (
													<Text key={url.id} style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(url.createdAt),
														)}
													</Text>
												);

												const id = (
													<Text key={url.id} style={styles.rowText}>
														{url.id}
													</Text>
												);

												const actions = (
													<View key={url.id} style={styles.actionsContainer}>
														<Button
															icon="content-copy"
															color="#323ea8"
															onPress={async () => {
																onAction("copyShortLink", url);
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
																onAction("edit", url);
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
																onAction("delete", url);
															}}
															iconSize={20}
															width={32}
															height={32}
															padding={6}
														/>
													</View>
												);

												return [
													code,
													vanity,
													destination,
													views,
													maxViews,
													created,
													enabled,
													id,
													actions,
												];
											})}
									/>
								) : (
									<ScrollView>
										{urls.map((url) => (
											<LargeURLView
												key={url.id}
												url={url}
												urlsRoute={settings?.urlsRoute || "/go"}
												dashUrl={dashUrl}
												onAction={onAction}
											/>
										))}
									</ScrollView>
								)}
							</>
						) : (
							<>
								{compactModeEnabled ? (
									<SkeletonTable
										headerRow={[
											"Code",
											"Vanity",
											"Destination",
											"Views",
											"Max Views",
											"Created",
											"Enabled",
											"ID",
											"Actions",
										]}
										rowWidth={[100, 120, 300, 110, 140, 130, 100, 220, 130]}
										rows={[...Array(12).keys()].map(() => {
											return [60, 50,  200,  30,  30,  70,  40, 180, 90];
										})}
										rowHeight={55}
										disableAnimations
									/>
								) : (
									<ScrollView showsVerticalScrollIndicator={false}>
										{[...Array(4).keys()].map(index => (
											<View key={index} style={{
												marginVertical: 5,
												marginHorizontal: 5
											}}>
												<Skeleton width="100%" height={200} />
											</View>
										))}
									</ScrollView>
								)}
							</>
						)}
					</View>
				</View>
			</View>
		</View>
	);
}

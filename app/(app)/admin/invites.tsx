import Button from "@/components/Button";
import LargeInviteView from "@/components/LargeInviteView";
import Popup from "@/components/Popup";
import Select from "@/components/Select";
import Skeleton from "@/components/skeleton/Skeleton";
import SkeletonTable from "@/components/skeleton/Table";
import Table from "@/components/Table";
import TextInput from "@/components/TextInput";
import { dates, searchKeyNames } from "@/constants/invites";
import * as db from "@/functions/database";
import { timeDifference } from "@/functions/util";
import {
	createInvite,
	deleteInvite,
	getInvites,
} from "@/functions/zipline/invites";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import { styles } from "@/styles/admin/invites";
import type { APIInvites, DashURL } from "@/types/zipline";
import * as Clipboard from "expo-clipboard";
import { useEffect, useState } from "react";
import { ScrollView, Text, ToastAndroid, View } from "react-native";

export type InviteActions = "copy" | "delete";

export default function Invites() {
	useAuth("ADMIN");
	useShareIntent();

	const invitesCompactView = db.get("invitesCompactView");

	const [showSearch, setShowSearch] = useState<boolean>(false);
	const [searchTerm, setSearchTerm] = useState<string>("");
	const [searchPlaceholder, setSearchPlaceholder] = useState<string>("");
	const [searchKey, setSearchKey] = useState<
		"code" | "inviter" | "maxUses" | "uses" | "id"
	>("code");

	const [invites, setInvites] = useState<APIInvites | null>(null);

	const [createNewInvite, setCreateNewInvite] = useState<boolean>(false);

	const [newInviteExpires, setNewInviteExpires] = useState<string>("never");
	const [newInviteMaxUses, setNewInviteMaxUses] = useState<number>();

	const [newInviteError, setNewInviteError] = useState<string | null>(null);

	const [sortKey, setSortKey] = useState<{
		id:
			| "code"
			| "inviter"
			| "createdAt"
			| "updatedAt"
			| "expiresAt"
			| "maxUses"
			| "uses"
			| "id";
		sortOrder: "asc" | "desc";
	}>({
		id: "createdAt",
		sortOrder: "asc",
	});

	const [compactModeEnabled, setCompactModeEnabled] = useState<boolean>(
		invitesCompactView === "true",
	);

	const dashUrl = db.get("url") as DashURL | null;

	useEffect(() => {
		(async () => {
			const invites = await getInvites();

			setInvites(typeof invites === "string" ? null : invites);
		})();
	}, []);

	// biome-ignore lint/correctness/useExhaustiveDependencies: search term should be resetted when search key changes
	useEffect(() => {
		setSearchPlaceholder("");
	}, [searchKey]);

	async function onAction(type: InviteActions, invite: APIInvites[0]) {
		switch (type) {
			case "copy": {
				const urlDest = `${dashUrl}/invite/${invite.code}`;

				const saved = await Clipboard.setStringAsync(urlDest);

				if (saved)
					return ToastAndroid.show(
						"Invite URL copied to clipboard",
						ToastAndroid.SHORT,
					);

				return ToastAndroid.show(
					"Failed to paste to the clipboard",
					ToastAndroid.SHORT,
				);
			}

			case "delete": {
				const inviteId = invite.id;

				const success = await deleteInvite(inviteId);

				if (typeof success === "string")
					return ToastAndroid.show(
						`Failed to delete the invite "${invite.code}"`,
						ToastAndroid.SHORT,
					);

				const newInvites = await getInvites();

				setInvites(typeof newInvites === "string" ? null : newInvites);

				ToastAndroid.show(
					`Deleted the invite "${invite.code}"`,
					ToastAndroid.SHORT,
				);
			}
		}
	}

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				<Popup
					hidden={!createNewInvite}
					onClose={() => {
						setCreateNewInvite(false);
						setNewInviteExpires("never");
						setNewInviteMaxUses(undefined);
					}}
				>
					<View style={styles.popupContent}>
						<Text style={styles.mainHeaderText}>Create Invite</Text>
						{newInviteError && (
							<Text style={styles.errorText}>{newInviteError}</Text>
						)}

						<Text style={styles.popupHeaderText}>Expires At:</Text>
						<Text style={styles.selectDescription}>
							Select an expiration for this invite, or choose "never" if you want the invite to never expire.
						</Text>
						<Select
							placeholder="Select Date..."
							data={dates}
							onSelect={(selectedDate) => {
								if (selectedDate.length <= 0) return;

								setNewInviteExpires(selectedDate[0].value);
							}}
							defaultValue={dates.find((date) => date.value === "never")}
						/>

						<TextInput
							title="Max Uses:"
							description="Set a maximum number of uses for this invite, or leave blank for unlimited uses."
							onValueChange={(content) => {
								setNewInviteMaxUses(
									Math.abs(Number.parseInt(content, 10)) || undefined,
								);
							}}
							value={newInviteMaxUses ? String(newInviteMaxUses) : ""}
							keyboardType="numeric"
							placeholder="5"
						/>

						<Button
							onPress={async () => {
								setNewInviteError(null);

								const createdInvite = await createInvite(
									newInviteExpires,
									newInviteMaxUses,
								);

								if (typeof createdInvite === "string")
									return setNewInviteError(createdInvite);

								const urlDest = `${dashUrl}/invite/${createdInvite.code}`;

								const saved = await Clipboard.setStringAsync(urlDest);

								setNewInviteExpires("never");
								setNewInviteMaxUses(undefined);

								const newInvites = await getInvites();

								setInvites(typeof newInvites === "string" ? null : newInvites);

								setCreateNewInvite(false);

								if (saved)
									return ToastAndroid.show(
										"Invite URL copied to clipboard",
										ToastAndroid.SHORT,
									);

								ToastAndroid.show(
									"Failed to copy the invite URL to the clipboard",
									ToastAndroid.SHORT,
								);
							}}
							text="Create"
							color="#323ea8"
							containerStyle={{
								marginTop: 15
							}}
						/>
					</View>
				</Popup>

				<View style={styles.header}>
					<Text style={styles.headerText}>Invites</Text>
					<View style={styles.headerButtons}>
						<Button
							onPress={() => {
								setCreateNewInvite(true);
							}}
							icon="add"
							color="transparent"
							iconColor={invites && dashUrl ? "#2d3f70" : "#2d3f7055"}
							containerStyle={{
								borderColor: "#222c47",
								borderWidth: 2,
								marginHorizontal: 2
							}}
							buttonStyle={{
								padding: 4
							}}
							iconSize={30}
							rippleColor="#283557"
							disabled={!invites || !dashUrl}
						/>

						<Button
							onPress={() => {
								db.set(
									"invitesCompactView",
									compactModeEnabled ? "false" : "true",
								);

								setCompactModeEnabled((prev) => !prev);
							}}
							icon={compactModeEnabled ? "view_module" : "view_agenda"}
							color="transparent"
							iconColor={invites && dashUrl ? "#2d3f70" : "#2d3f7055"}
							containerStyle={{
								borderColor: "#222c47",
								borderWidth: 2,
								marginHorizontal: 2
							}}
							buttonStyle={{
								padding: 4
							}}
							iconSize={30}
							rippleColor="#283557"
							disabled={!invites || !dashUrl}
						/>
					</View>
				</View>

				{showSearch && (
					<View style={styles.mainSearchContainer}>
						<View style={styles.searchContainer}>
							<Text style={styles.searchHeader}>
								Search by {searchKeyNames[searchKey]}
							</Text>

							<Button
								onPress={() => setShowSearch(false)}
								icon="close"
								color="#191b27"
								containerStyle={{
									width: 30,
									height: 30,
								}}
								buttonStyle={{
									padding: 5
								}}
								iconStyle={{
									marginBottom: 7
								}}
							/>
						</View>

						<TextInput
							placeholder="Search..."
							defaultValue={searchPlaceholder}
							keyboardType={
								searchKey === "maxUses" || searchKey === "uses"
									? "numeric"
									: "default"
							}
							onValueChange={(text) => setSearchPlaceholder(text)}
							onSubmitEditing={(event) => {
								const searchText = event.nativeEvent.text;

								setSearchTerm(searchText);
								setShowSearch(false);
							}}
							returnKeyType="search"
						/>
					</View>
				)}

				<View style={{ flex: 1 }}>
					<View style={{ ...styles.invitesContainer, flex: 1 }}>
						{invites && dashUrl ? (
							// biome-ignore lint/complexity/noUselessFragments: The fragment is required
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
												row: "Created By",
												id: "inviter",
												sortable: true,
												searchable: true,
											},
											{
												row: "Created",
												id: "createdAt",
												sortable: true,
											},
											{
												row: "Last Updated",
												id: "updatedAt",
												sortable: true,
											},
											{
												row: "Expires",
												id: "expiresAt",
												sortable: true,
											},
											{
												row: "Max Uses",
												id: "maxUses",
												sortable: true,
												searchable: true,
											},
											{
												row: "Uses",
												id: "uses",
												sortable: true,
												searchable: true,
											},
											{
												row: "ID",
												id: "id",
												sortable: true,
												searchable: true,
											},
											{
												row: "Actions",
											},
										]}
										sortKey={sortKey}
										onSearch={(key) => {
											setShowSearch(true);
											setSearchKey(key as typeof searchKey);
										}}
										onSortOrderChange={(key, order) => {
											setSortKey({
												id: key as typeof sortKey.id,
												sortOrder: order,
											});
										}}
										rowWidth={[100, 140, 130, 140, 130, 130, 100, 220, 90]}
										rows={invites
											.filter((folder) => {
												let filterKey =
													searchKey === "inviter"
														? folder[searchKey].username
														: folder[searchKey];

												if (searchKey === "maxUses" && !filterKey)
													filterKey = "0";

												return String(filterKey)
													.toLowerCase()
													.includes(searchTerm.toLowerCase());
											})
											.sort((a, b) => {
												const compareKeyA =
													sortKey.id === "createdAt" ||
													sortKey.id === "updatedAt"
														? new Date(a[sortKey.id])
														: sortKey.id === "inviter"
															? a[sortKey.id].username
															: sortKey.id === "expiresAt"
																? a[sortKey.id]
																	? new Date(a[sortKey.id] as string)
																	: null
																: a[sortKey.id];

												const compareKeyB =
													sortKey.id === "createdAt" ||
													sortKey.id === "updatedAt"
														? new Date(b[sortKey.id])
														: sortKey.id === "inviter"
															? b[sortKey.id].username
															: sortKey.id === "expiresAt"
																? b[sortKey.id]
																	? new Date(b[sortKey.id] as string)
																	: null
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
											.map((invite) => {
												const code = (
													<Text key={invite.id} style={styles.rowText}>
														{invite.code}
													</Text>
												);

												const createdBy = (
													<Text key={invite.id} style={styles.rowText}>
														{invite.inviter.username}
													</Text>
												);

												const created = (
													<Text key={invite.id} style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(invite.createdAt),
														)}
													</Text>
												);

												const lastUpdated = (
													<Text key={invite.id} style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(invite.updatedAt),
														)}
													</Text>
												);

												const expires = invite.expiresAt ? (
													<Text key={invite.id} style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(invite.expiresAt),
														)}
													</Text>
												) : (
													<Text key={invite.id} style={styles.rowText}>
														Never
													</Text>
												);

												const uses = (
													<Text key={invite.id} style={styles.rowText}>
														{invite.uses}
													</Text>
												);

												const maxUses = (
													<Text key={invite.id} style={styles.rowText}>
														{invite.maxUses || "Unlimited"}
													</Text>
												);

												const id = (
													<Text key={invite.id} style={styles.rowText}>
														{invite.id}
													</Text>
												);

												const actions = (
													<View key={invite.id} style={styles.actionsContainer}>
														<Button
															icon="content_copy"
															color="#323ea8"
															onPress={async () => {
																onAction("copy", invite);
															}}
															iconSize={20}
															containerStyle={{
																width: 32,
																height: 32,
															}}
															iconStyle={{
																marginBottom: 7
															}}
														/>

														<Button
															icon="delete"
															color="#CF4238"
															onPress={async () => {
																onAction("delete", invite);
															}}
															iconSize={20}
															containerStyle={{
																width: 32,
																height: 32,
															}}
															iconStyle={{
																marginBottom: 7
															}}
														/>
													</View>
												);

												return [
													code,
													createdBy,
													created,
													lastUpdated,
													expires,
													maxUses,
													uses,
													id,
													actions,
												];
											})}
									/>
								) : (
									<ScrollView>
										{invites.map((invite) => (
											<LargeInviteView
												key={invite.id}
												invite={invite}
												dashUrl={dashUrl}
												onAction={onAction}
											/>
										))}
									</ScrollView>
								)}
							</>
						) : (
							// biome-ignore lint/complexity/noUselessFragments: The fragment is required
							<>
								{compactModeEnabled ? (
									<SkeletonTable
										headerRow={[
											"Code",
											"Created By",
											"Created",
											"Last Updated",
											"Expires",
											"Max Uses",
											"Uses",
											"ID",
											"Actions",
										]}
										rowWidth={[100, 140, 130, 140, 130, 130, 100, 220, 90]}
										rows={[...Array(12).keys()].map(() => {
											return [60, 50, 70, 70, 70, 40, 40, 180, 60];
										})}
										rowHeight={55}
										disableAnimations
									/>
								) : (
									<ScrollView showsVerticalScrollIndicator={false}>
										{[...Array(4).keys()].map((index) => (
											<View
												key={index}
												style={{
													marginVertical: 5,
													marginHorizontal: 5,
												}}
											>
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

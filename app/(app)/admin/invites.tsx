import { Text, View, ToastAndroid, ScrollView } from "react-native";
import type { APIInvites, DashURL } from "@/types/zipline";
import LargeInviteView from "@/components/LargeInviteView";
import { useShareIntent } from "@/hooks/useShareIntent";
import { timeDifference } from "@/functions/util";
import { styles } from "@/styles/admin/invites";
import TextInput from "@/components/TextInput";
import { useEffect, useState } from "react";
import { dates } from "@/constants/invites";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import { useAuth } from "@/hooks/useAuth";
import Select from "@/components/Select";
import Button from "@/components/Button";
import Popup from "@/components/Popup";
import Table from "@/components/Table";
import {
	createInvite,
	deleteInvite,
	getInvites,
} from "@/functions/zipline/invites";

export type InviteActions = "copy" | "delete";

export default function Invites() {
	useAuth("ADMIN");
	useShareIntent();

	const invitesCompactView = db.get("invitesCompactView");

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
			| "uses";
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
							onValueChange={(content) => {
								setNewInviteMaxUses(
									Math.abs(Number.parseInt(content)) || undefined,
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
							margin={{
								top: 15,
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
							// iconColor="#2d3f70"
							iconColor={invites && dashUrl ? "#2d3f70" : "#2d3f7055"}
							borderColor="#222c47"
							borderWidth={2}
							iconSize={30}
							padding={4}
							rippleColor="#283557"
							disabled={!invites || !dashUrl}
							margin={{
								left: 2,
								right: 2,
							}}
						/>

						<Button
							onPress={() => {
								db.set(
									"invitesCompactView",
									compactModeEnabled ? "false" : "true",
								);

								setCompactModeEnabled((prev) => !prev);
							}}
							icon={compactModeEnabled ? "view-module" : "view-agenda"}
							color="transparent"
							iconColor={invites && dashUrl ? "#2d3f70" : "#2d3f7055"}
							borderColor="#222c47"
							borderWidth={2}
							iconSize={30}
							padding={4}
							rippleColor="#283557"
							disabled={!invites || !dashUrl}
							margin={{
								left: 2,
								right: 2,
							}}
						/>
					</View>
				</View>

				<View style={{ flex: 1 }}>
					<View style={{ ...styles.invitesContainer, flex: 1 }}>
						{invites && dashUrl ? (
							<>
								{compactModeEnabled ? (
									<Table
										headerRow={[
											{
												row: "Code",
												id: "code",
												sortable: true,
											},
											{
												row: "Created By",
												id: "inviter",
												sortable: true,
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
											},
											{
												row: "Uses",
												id: "uses",
												sortable: true,
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
										rowWidth={[90, 120, 130, 140, 130, 120, 100, 90]}
										rows={invites
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
											.map((invite, index) => {
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

												const actions = (
													<View key={invite.id} style={styles.actionsContainer}>
														<Button
															icon="content-copy"
															color="#323ea8"
															onPress={async () => {
																onAction("copy", invite);
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
																onAction("delete", invite);
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

												if (index === invites.length - 1)
													rowStyle = {
														...styles.row,
														...styles.lastRow,
													};

												return [
													code,
													createdBy,
													created,
													lastUpdated,
													expires,
													maxUses,
													uses,
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
							<View style={styles.loadingContainer}>
								<Text style={styles.loadingText}>Loading...</Text>
							</View>
						)}
					</View>
				</View>
			</View>
		</View>
	);
}

import { ScrollView, Text, View, ToastAndroid } from "react-native";
import type {
	APIInvites,
	APISettings,
	DashURL,
} from "@/types/zipline";
import { getSettings } from "@/functions/zipline/settings";
import { Row, Table } from "react-native-table-component";
import { timeDifference } from "@/functions/util";
import { styles } from "@/styles/admin/invites";
import { useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import {
	createInvite,
	deleteInvite,
	getInvites,
} from "@/functions/zipline/invites";
import Popup from "@/components/Popup";
import Select from "@/components/Select";
import { dates } from "@/constants/invites";
import { useAuth } from "@/hooks/useAuth";
import { useShareIntent } from "@/hooks/useShareIntent";
import TextInput from "@/components/TextInput";
import Button from "@/components/Button";

export default function Invites() {
	useAuth("ADMIN");
	useShareIntent();

	const [invites, setInvites] = useState<APIInvites | null>(null);
	const [settings, setSettings] = useState<APISettings | null>(null);

	const [createNewInvite, setCreateNewInvite] = useState<boolean>(false);

	const [newInviteExpires, setNewInviteExpires] = useState<string>("never");
	const [newInviteMaxUses, setNewInviteMaxUses] = useState<number>();

	const [newInviteError, setNewInviteError] = useState<string | null>(null);

	const dashUrl = db.get("url") as DashURL | null;

	useEffect(() => {
		(async () => {
			const invites = await getInvites();
			const settings = await getSettings();

			setInvites(typeof invites === "string" ? null : invites);
			setSettings(typeof settings === "string" ? null : settings);
		})();
	}, []);

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

				{invites && settings && dashUrl ? (
					<View style={{ flex: 1 }}>
						<View style={styles.header}>
							<Text style={styles.headerText}>Invites</Text>
							<View style={styles.headerButtons}>
								<Button
									onPress={() => {
										setCreateNewInvite(true);
									}}
									icon="add"
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

						<View style={{ ...styles.invitesContainer, flex: 1 }}>
							<ScrollView showsHorizontalScrollIndicator={false} horizontal>
								<View>
									<Table>
										<Row
											data={[
												"Code",
												"Created By",
												"Created",
												"Last Updated",
												"Expires",
												"Max Uses",
												"Uses",
												"Actions",
											]}
											widthArr={[80, 100, 130, 130, 130, 100, 100, 90]}
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
											{invites.map((invite, index) => {
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
													<Text style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(invite.createdAt),
														)}
													</Text>
												);

												const lastUpdated = (
													<Text style={styles.rowText}>
														{timeDifference(
															new Date(),
															new Date(invite.updatedAt),
														)}
													</Text>
												);

												const expires = invite.expiresAt ? (
													<Text style={styles.rowText}>
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
													<Text style={styles.rowText}>{invite.uses}</Text>
												);

												const maxUses = (
													<Text style={styles.rowText}>
														{invite.maxUses || "Unlimited"}
													</Text>
												);

												const actions = (
													<View style={styles.actionsContainer}>
														<Button
															icon="content-copy"
															color="#323ea8"
															onPress={async () => {
																const urlDest = `${dashUrl}/invite/${invite.code}`;

																const saved =
																	await Clipboard.setStringAsync(urlDest);

																if (saved)
																	return ToastAndroid.show(
																		"Invite URL copied to clipboard",
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
															icon="delete"
															color="#CF4238"
															onPress={async () => {
																const inviteId = invite.id;

																const success = await deleteInvite(inviteId);

																if (typeof success === "string")
																	return ToastAndroid.show(
																		`Failed to delete the invite "${invite.code}"`,
																		ToastAndroid.SHORT,
																	);

																const newInvites = invites.filter(
																	(inv) => inv.id !== invite.id,
																);

																setInvites(newInvites);

																ToastAndroid.show(
																	`Deleted the invite "${invite.code}"`,
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

												if (index === invites.length - 1)
													rowStyle = {
														...styles.row,
														...styles.lastRow,
													};

												return (
													<Row
														key={invite.id}
														data={[
															code,
															createdBy,
															created,
															lastUpdated,
															expires,
															maxUses,
															uses,
															actions,
														]}
														widthArr={[80, 100, 130, 130, 130, 100, 100, 90]}
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

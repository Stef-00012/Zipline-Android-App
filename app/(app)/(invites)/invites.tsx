import { Pressable, ScrollView, Text, View, ToastAndroid } from "react-native";
import type { APIInvites, APISettings, APIURLs, DashURL } from "@/types/zipline";
import MaterialIcons from "@expo/vector-icons/MaterialIcons";
import { isAuthenticated } from "@/functions/zipline/auth";
import { getSettings } from "@/functions/zipline/settings";
import { Row, Table } from "react-native-table-component";
import { useShareIntentContext } from "expo-share-intent";
import { timeDifference } from "@/functions/util";
import { styles } from "@/styles/invites/invites";
import { useEffect, useState } from "react";
import * as Clipboard from "expo-clipboard";
import * as db from "@/functions/database";
import {
	type ExternalPathString,
	Link,
	useFocusEffect,
	useRouter,
} from "expo-router";
import { deleteInvite, getInvites } from "@/functions/zipline/invites";

export default function Invites() {
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

	const [invites, setInvites] = useState<APIInvites | null>(null);
	const [settings, setSettings] = useState<APISettings | null>(null);

	const dashUrl = db.get("url") as DashURL | null;

	useEffect(() => {
		(async () => {
			const invites = await getInvites();
			const settings = await getSettings();

			setInvites(invites);
			setSettings(settings);
		})();
	}, []);

	return (
		<View style={styles.mainContainer}>
			<View style={styles.mainContainer}>
				{invites && settings && dashUrl ? (
					<View style={{ flex: 1 }}>
						<View style={styles.header}>
							<Text style={styles.headerText}>Invites</Text>
							<View style={styles.headerButtons}>
								<Pressable
									style={styles.headerButton}
									onPress={() => {
										console.debug("Create Invite Pressed")
									}}
								>
									<MaterialIcons
										name="add"
										size={30}
										color={styles.headerButton.color}
									/>
								</Pressable>
							</View>
						</View>

						<View style={{ ...styles.invitesContainer, flex: 1 }}>
							<ScrollView
								showsHorizontalScrollIndicator={false}
								horizontal={true}
							>
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
											textStyle={styles.rowText}
										/>
									</Table>
									<ScrollView
										showsVerticalScrollIndicator={false}
										style={styles.tableVerticalScroll}
									>
										<Table>
											{invites.map((invite, index) => {
												const code = (
													<Text
														key={invite.id}
														style={styles.rowText}
													>
														{invite.code}
													</Text>
												);

												const createdBy = (
													<Text
														key={invite.id}
														style={styles.rowText}
													>
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
                                                    <Text
														key={invite.id}
														style={styles.rowText}
													>
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
														<Pressable
															style={styles.actionButton}
															onPress={async () => {
																const urlDest = `${dashUrl}/invite/${invite.code}`

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
														>
															<MaterialIcons
																name="content-copy"
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
																const inviteId = invite.id;

																const success = await deleteInvite(inviteId);

																if (!success) return ToastAndroid.show(
                                                                    `Failed to delete the invite "${invite.code}"`,
                                                                    ToastAndroid.SHORT
                                                                )

																const newInvites = invites.filter(inv => inv.id !== invite.id)

																setInvites(newInvites)

                                                                ToastAndroid.show(
                                                                    `Deleted the invite "${invite.code}"`,
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
                                                            actions
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

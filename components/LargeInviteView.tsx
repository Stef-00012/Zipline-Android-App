import type { InviteActions } from "@/app/(app)/admin/invites";
import { styles } from "@/styles/components/largeInviteView";
import type { APIInvite, DashURL } from "@/types/zipline";
import { timeDifference } from "@/functions/util";
import Dropdown from "@/components/Dropdown";
import { Text, View } from "react-native";
import { Link } from "expo-router";

interface Props {
	invite: APIInvite;
	dashUrl: DashURL;
	onAction: (type: InviteActions, invite: APIInvite) => Promise<void> | void;
}

export default function LargeInviteView({ invite, dashUrl, onAction }: Props) {
	return (
		<View style={styles.mainContainer}>
			<View style={styles.titleContainer}>
				<Link
					href={`${dashUrl}/invite/${invite.id}`}
					style={{
						...styles.inviteCode,
						...styles.link,
					}}
				>
					{invite.code}
				</Link>

				<Dropdown
					icon="more-horiz"
					data={[
						{
							name: "Copy URL",
							id: `${invite.id}-copyUrl`,
							icon: "folder-open",
							onPress: () => {
								onAction("copy", invite);
							},
						},
						{
							name: "Delete",
							id: `${invite.id}-delete`,
							color: "#e65f59",
							iconColor: "#e65f59",
							icon: "delete",
							onPress: () => {
								onAction("delete", invite);
							},
						},
					]}
				/>
			</View>

			<View style={styles.divider} />

			<View style={styles.keysContainer}>
				<Text style={styles.key}>
					<Text style={styles.keyName}>Created By</Text>:{" "}
					<Text>{invite.inviter.username}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Created</Text>:{" "}
					<Text>{timeDifference(new Date(), new Date(invite.createdAt))}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Updated</Text>:{" "}
					<Text>{timeDifference(new Date(), new Date(invite.updatedAt))}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Expires</Text>:{" "}
					<Text>
						{invite.expiresAt
							? timeDifference(new Date(), new Date(invite.expiresAt))
							: "Never"}
					</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Max Uses</Text>:{" "}
					<Text>{invite.maxUses}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Uses</Text>: <Text>{invite.uses}</Text>
				</Text>
			</View>
		</View>
	);
}

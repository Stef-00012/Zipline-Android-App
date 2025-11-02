import type { UserActions } from "@/app/(app)/admin/users";
import Button from "@/components/Button";
import Dropdown from "@/components/Dropdown";
import { timeDifference } from "@/functions/util";
import { styles } from "@/styles/components/largeUserView";
import type { APIUserNoIncl, DashURL } from "@/types/zipline";
import { Image } from "expo-image";
import { Text, View } from "react-native";
import MaterialSymbols from "./MaterialSymbols";

interface Props {
	user: APIUserNoIncl;
	dashUrl: DashURL;
	onAction: (type: UserActions, user: APIUserNoIncl) => Promise<void> | void;
}

export default function LargeUserView({
	user,
	dashUrl: _dashUrl,
	onAction,
}: Props) {
	return (
		<View style={styles.mainContainer}>
			<View style={styles.titleContainer}>
				<View style={styles.titleLeftContainer}>
					{user.avatar ? (
						<Image
							key={user.id}
							source={{ uri: user.avatar }}
							style={styles.userAvatar}
							alt={`${user.username}'s avatar`}
						/>
					) : (
						<View key={user.id} style={styles.userAvatar}>
							<MaterialSymbols name="person" size={30} color={"white"} />
						</View>
					)}

					<View style={styles.userInfoContainer}>
						<Text style={styles.userUsername}>{user.username}</Text>
						<Text style={styles.userId}>{user.id}</Text>
					</View>
				</View>

				<View style={styles.titleRightContainer}>
					<Button
						onPress={() => onAction("viewFiles", user)}
						color="transparent"
						icon="folder_open"
						rippleColor="#464953"
						containerStyle={{
							borderColor: "#222c47",
							borderWidth: 2,
							borderRadius: 7,
							width: 30,
							height: 30,
							marginRight: 4
						}}
						buttonStyle={{
							padding: 5
						}}
						iconStyle={{
							marginBottom: 12,
							marginLeft: -1
						}}
						iconSize={20}
						iconColor="#575DB5"
					/>

					<Dropdown
						icon="more_horiz"
						data={[
							{
								name: "Edit",
								id: `${user.id}-edit`,
								icon: "edit",
								onPress: () => {
									onAction("edit", user);
								},
							},
							{
								name: "Delete",
								id: `${user.id}-delete`,
								color: "#e65f59",
								iconColor: "#e65f59",
								icon: "delete",
								onPress: () => {
									onAction("delete", user);
								},
							},
						]}
					/>
				</View>
			</View>

			<View style={styles.divider} />

			<View style={styles.keysContainer}>
				<Text style={styles.key}>
					<Text style={styles.keyName}>Role</Text>:{" "}
					<Text>
						{user.role.charAt(0).toUpperCase() +
							user.role.slice(1).toLowerCase()}
					</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Created</Text>:{" "}
					<Text>{timeDifference(new Date(), new Date(user.createdAt))}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Updated</Text>:{" "}
					<Text>{timeDifference(new Date(), new Date(user.updatedAt))}</Text>
				</Text>
			</View>
		</View>
	);
}

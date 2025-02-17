import { styles } from "@/styles/components/largeFolderView";
import type { APIFolder, DashURL } from "@/types/zipline";
import type { FolderActions } from "@/app/(app)/folders";
import { timeDifference } from "@/functions/util";
import Dropdown from "@/components/Dropdown";
import { Text, View } from "react-native";
import { Link } from "expo-router";

interface Props {
	folder: APIFolder;
	dashUrl: DashURL;
	onAction: (type: FolderActions, folder: APIFolder) => Promise<void> | void;
}

export default function LargeFolderView({ folder, dashUrl, onAction }: Props) {
	return (
		<View style={styles.mainContainer}>
			<View style={styles.titleContainer}>
				{folder.public ? (
					<Link
						href={`${dashUrl}/folder/${folder.id}`}
						style={{
							...styles.folderName,
							...styles.link,
						}}
					>
						{folder.name}
					</Link>
				) : (
					<Text style={styles.folderName}>{folder.name}</Text>
				)}

				<Dropdown
					icon="more-horiz"
					data={[
						{
							name: "View Files",
							id: `${folder.id}-viewFiles`,
							icon: "folder-open",
							onPress: () => {
								onAction("viewFiles", folder);
							},
						},
						{
							name: folder.public ? "Make Private" : "Make Public",
							id: `${folder.id}-changeVisibility`,
							icon: folder.public ? "lock-open" : "lock",
							onPress: async () => {
								onAction("visibility", folder);
							},
						},
						{
							name: "Edit Name",
							id: `${folder.id}-editName`,
							icon: "edit",
							onPress: () => {
								onAction("edit", folder);
							},
						},
						{
							name: "Copy URL",
							id: `${folder.id}-copyUrl`,
							icon: "content-copy",
							onPress: () => {
								onAction("copyUrl", folder);
							},
						},
						{
							name: "Delete",
							id: `${folder.id}-delete`,
							color: "#e65f59",
							iconColor: "#e65f59",
							icon: "delete",
							onPress: () => {
								onAction("delete", folder);
							},
						},
					]}
				/>
			</View>

			<View style={styles.divider} />

			<View style={styles.keysContainer}>
				<Text style={styles.key}>
					<Text style={styles.keyName}>Created</Text>:{" "}
					<Text>{timeDifference(new Date(), new Date(folder.createdAt))}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Updated</Text>:{" "}
					<Text>{timeDifference(new Date(), new Date(folder.updatedAt))}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Public</Text>:{" "}
					<Text>{folder.public ? "Yes" : "No"}</Text>
				</Text>

				<Text style={styles.key}>
					<Text style={styles.keyName}>Files</Text>:{" "}
					<Text>{folder.files.length}</Text>
				</Text>
				
				<Text style={styles.key}>
					<Text style={styles.keyName}>ID</Text>:{" "}
					<Text>{folder.id}</Text>
				</Text>
			</View>
		</View>
	);
}

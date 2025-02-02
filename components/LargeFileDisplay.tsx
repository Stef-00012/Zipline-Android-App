import { styles } from "@/styles/components/largeFileDisplay";
import type { APIFile, DashURL } from "@/types/zipline";
import { Pressable, Text, View } from "react-native";
import FileDisplay from "./FileDisplay";
import * as db from "@/functions/database";
import { KeyboardAwareScrollView } from "react-native-keyboard-controller";
import { MaterialIcons } from "@expo/vector-icons";
import bytes from "bytes";
import Select from "./Select";

interface Props {
	file: APIFile;
	hidden: boolean;
	onClose: () => void;
}

// WIP
export default function LargeFileDisplay({ file, hidden, onClose }: Props) {
	const dashUrl = db.get("url") as DashURL | null;

	return (
		<Pressable
			style={{
				...styles.popupContainerOverlay,
				...((hidden || !file) && { display: "none" }),
			}}
			onPress={(e) => {
				if (e.target === e.currentTarget) onClose();
			}}
		>
			<View style={styles.popupContainer}>
				<Text style={styles.fileHeader}>{file.name}</Text>

				<KeyboardAwareScrollView>
					<FileDisplay
						passwordProtected={!!file.password}
						uri={`${dashUrl}/raw/${file.name}`}
						originalName={file.originalName}
						mimetype={file.type}
						name={file.name}
						maxHeight={500}
						width={350}
						file={file}
						autoHeight
					/>

					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="description" size={40} color="white" />
						<View>
							<Text style={styles.fileInfoHeader}>Type</Text>
							<Text style={styles.fileInfoText}>{file.type}</Text>
						</View>
					</View>

					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="sd-storage" size={40} color="white" />
						<View>
							<Text style={styles.fileInfoHeader}>Size</Text>
							<Text style={styles.fileInfoText}>{bytes(file.size)}</Text>
						</View>
					</View>

					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="file-upload" size={40} color="white" />
						<View>
							<Text style={styles.fileInfoHeader}>Created At</Text>
							<Text style={styles.fileInfoText}>{new Date(file.createdAt).toLocaleString()}</Text>
						</View>
					</View>

					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="autorenew" size={40} color="white" />
						<View>
							<Text style={styles.fileInfoHeader}>Updated At</Text>
							<Text style={styles.fileInfoText}>{new Date(file.updatedAt).toLocaleString()}</Text>
						</View>
					</View>
					
					<View style={styles.fileInfoContainer}>
						<MaterialIcons name="visibility" size={40} color="white" />
						<View>
							<Text style={styles.fileInfoHeader}>View</Text>
							<Text style={styles.fileInfoText}>{file.views}</Text>
						</View>
					</View>

					<Select placeholder="aa" multiple data={[
						{ label: "Favorite", value: "favorite" },
						{ label: "Delete", value: "delete" },
						{ label: "Delete1", value: "delete1" },
						{ label: "Delete2", value: "delete2" },
						{ label: "Delete3", value: "delete3" },
					]} onSelect={console.log}  />
				</KeyboardAwareScrollView>
			</View>
		</Pressable>
	);
}

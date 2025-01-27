import { styles } from "@/styles/components/largeFileDisplay";
import type { APIFile, DashURL } from "@/types/zipline";
import { Pressable, Text, View } from "react-native";
import FileDisplay from "./FileDisplay";
import * as db from "@/functions/database";

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
				<Text>{file.name}</Text>

				<FileDisplay
					uri={`${dashUrl}/raw/${file.name}`}
					name={file.name}
					mimetype={file.type}
					autoHeight
					passwordProtected={!!file.password}
					originalName={file.originalName}
					width={400}
                    file={file}
				/>
			</View>
		</Pressable>
	);
}

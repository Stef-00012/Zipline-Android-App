import { styles } from "@/styles/components/externalUrl";
import type { ExternalLink } from "@/types/zipline";
import { type ExternalPathString, Link } from "expo-router";
import { Text, View } from "react-native";
import Button from "./Button";

type ChangeType = "delete" | "edit";
type MoveType = "up" | "down";

interface Props {
	externalUrl: ExternalLink;
	onChange: (type: ChangeType, id: Props["id"]) => void | Promise<void>;
	onMove: (type: MoveType, id: Props["id"]) => void | Promise<void>;
	id: number;
	disabled?: boolean;
}

export default function ExternalUrl({
	externalUrl,
	onChange,
	id,
	onMove,
	disabled,
}: Props) {
	return (
		<View style={styles.mainContainer}>
			<View style={{ width: "88%" }}>
				<Text style={styles.propertyText}>
					<Text style={styles.propertyTitle}>Name</Text>:{" "}
					<Text>{externalUrl.name}</Text>
				</Text>

				<Text style={styles.propertyText}>
					<Text style={styles.propertyTitle}>URL</Text>:{" "}
					<Link
						href={externalUrl.url as ExternalPathString}
						style={styles.link}
					>
						{externalUrl.url}
					</Link>
				</Text>

				<View style={{ flexDirection: "row" }}>
					<Button
						onPress={() => {
							onChange("edit", id);
						}}
						disabled={disabled}
						color="#323ea8"
						containerStyle={{
							width: "45%",
							marginTop: 7,
							marginBottom: 5,
							marginHorizontal: "2.5%",
						}}
						text="Edit"
						icon="edit"
					/>

					<Button
						onPress={() => {
							onChange("delete", id);
						}}
						disabled={disabled}
						color="#CF4238"
						containerStyle={{
							width: "45%",
							marginTop: 7,
							marginBottom: 5,
							marginHorizontal: "2.5%",
						}}
						text="Delete"
						icon="delete"
					/>
				</View>
			</View>

			<View
				style={{
					justifyContent: "center",
				}}
			>
				<Button
					onPress={() => {
						onMove("up", id);
					}}
					disabled={disabled}
					color="#323ea8"
					containerStyle={{
						width: 25,
						height: 25,
						marginVertical: 10,
						marginLeft: 5
					}}
					buttonStyle={{
						padding: 4
					}}
					iconStyle={{
						marginBottom: 12
					}}
					icon="north"
					iconSize={16}
				/>

				<Button
					onPress={() => {
						onMove("down", id);
					}}
					disabled={disabled}
					color="#323ea8"
					containerStyle={{
						width: 25,
						height: 25,
						marginVertical: 10,
						marginLeft: 5
					}}
					buttonStyle={{
						padding: 4
					}}
					iconStyle={{
						marginBottom: 12
					}}
					icon="south"
					iconSize={16}
				/>
			</View>
		</View>
	);
}

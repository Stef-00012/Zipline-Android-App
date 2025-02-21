import { type ExternalPathString, Link } from "expo-router";
import type { ExternalLink } from "@/types/zipline";
import { Text, View } from "react-native";
import Button from "./Button";
import { styles } from "@/styles/components/externalUrl";

type ChangeType = "delete" | "edit";
type MoveType = "up" | "down";

interface Props {
	externalUrl: ExternalLink;
	onChange: (type: ChangeType, id: Props["id"]) => void | Promise<void>;
    onMove: (type: MoveType, id: Props["id"]) => void | Promise<void>
	id: number;
}

export default function ExternalUrl({ externalUrl, onChange, id, onMove }: Props) {
	return (
		<View style={styles.mainContainer}>
			<View style={{width: "88%"}}>
                <Text style={styles.propertyText}>
                    <Text style={styles.propertyTitle}>Name</Text>:{" "}
                    <Text>{externalUrl.name}</Text>
                </Text>

                <Text style={styles.propertyText}>
                    <Text style={styles.propertyTitle}>URL</Text>:{" "}
                    <Link href={externalUrl.url as ExternalPathString} style={styles.link}>
                        {externalUrl.url}
                    </Link>
                </Text>

                <View style={{ flexDirection: "row" }}>
                    <Button
                        onPress={() => {
                            onChange("edit", id);
                        }}
                        color="#323ea8"
                        width="45%"
                        margin={{
                            top: 7,
                            bottom: 5,
                            left: "2.5%",
                            right: "2.5%",
                        }}
                        text="Edit"
                        icon="edit"
                    />

                    <Button
                        onPress={() => {
                            onChange("delete", id);
                        }}
                        color="#CF4238"
                        width="45%"
                        margin={{
                            top: 7,
                            bottom: 5,
                            left: "2.5%",
                            right: "2.5%",
                        }}
                        text="Delete"
                        icon="delete"
                    />
                </View>
            </View>

            <View style={{
                justifyContent: "center",
            }}>
                <Button
                    onPress={() => {
                        onMove("up", id)
                    }}
                    color="#323ea8"
                    width={25}
                    height={25}
                    margin={{
                        top: 10,
                        bottom: 10,
                        left: 5
                    }}
                    icon="north"
                    padding={4}
                    iconSize={16}
                />

                <Button
                    onPress={() => {
                        onMove("down", id)
                    }}
                    color="#323ea8"
                    width={25}
                    height={25}
                    margin={{
                        top: 10,
                        bottom: 10,
                        left: 5
                    }}
                    icon="south"
                    padding={4}
                    iconSize={16}
                />
            </View>
		</View>
	);
}

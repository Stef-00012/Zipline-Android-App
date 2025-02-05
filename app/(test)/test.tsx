import Button from "@/components/Button";
import { View } from "react-native";

export default function Test() {
	return (
		<View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
			<Button icon="save" text="test" textColor={"black"} iconColor={"black"} borderColor={"#000000"} borderWidth={4} color="transparent" onPress={() => {
                console.log("press")
            }} />
		</View>
	);
}

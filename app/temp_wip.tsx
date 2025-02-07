import { Pressable, Text, View } from "react-native";
import { useRouter } from "expo-router";

export default function Temp() {
    const router = useRouter();

    return (
        <View style={{
            flex: 1,
            display: "flex",
            backgroundColor: "#0c101c",
            justifyContent: "center",
            alignItems: "center",
        }}>
            <View>
                <Text style={{
                    color: "#cdd6f4",
                    fontSize: 30,
                    fontWeight: "bold",
                    margin: "auto",
                    justifyContent: "center",
                    alignItems: "center",
                    textAlign: "center"
                }}>This page is still being developed</Text>

                <Pressable
                    style={{
                        backgroundColor: "#323ea8",
                        padding: 10,
                        borderRadius: 10,
                        marginTop: 15
                    }}
                    onPress={() => {
                        router.replace({
                            pathname: "/",
                        });
                    }}
                >
                    <Text style={{
                        textAlign: "center",
                        fontWeight: "bold",
                        color: "white",
                        fontSize: 16,
                    }}>Head to the Dashboard</Text>
                </Pressable>
            </View>
        </View>
    );
}
import { MaterialIcons } from "@expo/vector-icons";
import { TouchableOpacity, View } from "react-native";

interface Props {
    value: boolean;
    onValueChange: () => void
}

export default function CheckBox({ value, onValueChange }: Props) {
    return (
        <TouchableOpacity onPress={onValueChange} style={{ marginRight: 10 }}>
            <View style={{
                borderRadius: 6,
                width: 20,
                height: 20,
                borderWidth: 2,
                borderColor: value ? "#323ea8" : "#222c47",
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: value ? '#323ea8' : 'transparent'
            }}>
                {value && <MaterialIcons
                    name="check"
                    size={16}
                    color='white'
                />}
            </View>
        </TouchableOpacity>
    );
}
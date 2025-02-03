import { MaterialIcons } from "@expo/vector-icons";
import { TouchableOpacity, View } from "react-native";
import { styles } from "@/styles/components/checkbox";

interface Props {
    value: boolean;
    onValueChange: () => void
}

export default function CheckBox({ value, onValueChange }: Props) {
    return (
        <TouchableOpacity onPress={onValueChange} style={styles.checkboxContainer}>
            <View style={{
                ...styles.checkbox,
                borderColor: value ? "#323ea8" : "#222c47",
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
interface Props {
    value: string;
    onValueChange: (newValue: string) => void | Promise<void>;
    disabled?: boolean;
    password?: boolean;
    type?: "default" | "number-pad" | "decimal-pad" | "numeric" | "email-address" | "phone-pad" | "url" // | "visible-password"
    placeholder?: string;
    copy?: boolean;
}

export default function TextInput({
    value,
    onValueChange = () => {},
    disabled = false,
    password = false,
    type = "default",
    placeholder,
    copy = false
}) {
    return (
        
    )
}
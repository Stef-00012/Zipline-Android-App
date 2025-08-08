import DateTimePicker from "react-native-ui-datepicker";
import { styles } from "@/styles/components/datePicker";
import Button from "@/components/Button";
import type { ReactNode } from "react";
import Popup from "@/components/Popup";
import type {
	DatePickerMultipleProps,
	DatePickerRangeProps,
	DatePickerSingleProps
} from "react-native-ui-datepicker/lib/typescript/datetime-picker";

type Props = (
	| DatePickerMultipleProps
	| DatePickerRangeProps
	| DatePickerSingleProps
) & {
	open: boolean;
	onClose: () => void | Promise<void>;
	children?: ReactNode;
};

export default function DatePicker(props: Props) {
	const { open, onClose, children, ...datePickerProps } = props;

	const defaultProps = {
		monthContainerStyle: styles.monthContainerStyle,
		yearContainerStyle: styles.yearContainerStyle,
		calendarTextStyle: styles.calendarTextStyle,
		weekDaysTextStyle: styles.weekDaysTextStyle,
		headerTextStyle: styles.headerTextStyle,
		selectedRangeBackgroundColor: "#171c39",
		todayTextStyle: styles.todayTextStyle,
		headerButtonColor: "lightgray",
		selectedItemColor: "#323ea8",
		displayFullDays: true,
		firstDayOfWeek: 1,
		locale: "en",
		...datePickerProps,
	};

	return (
		<Popup hidden={!open} onClose={onClose}>
			<DateTimePicker {...defaultProps} />

			{props.children}

			<Button text="Close" onPress={props.onClose} color="#171c39" />
		</Popup>
	);
}

import Button from "@/components/Button";
import Popup from "@/components/Popup";
import { styles } from "@/styles/components/datePicker";
import type { ReactNode } from "react";
import DateTimePicker from "react-native-ui-datepicker";

type DateTimePickerProps = Parameters<typeof DateTimePicker>[0];

type Props = DateTimePickerProps & {
	open: boolean;
	onClose: () => void | Promise<void>;
	children?: ReactNode;
};

export default function DatePicker(props: Props) {
	const { open, onClose, children: _children, ...datePickerProps } = props;

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
			<DateTimePicker
				{...defaultProps}
				styles={{
					month_selector_label: styles.monthSelectorLabel,
					year_selector_label: styles.yearSelectorLabel,
					weekday_label: styles.weekdayLabel,
					day_label: styles.dayLabel,
				}}
			/>

			{props.children}

			<Button text="Close" onPress={props.onClose} color="#171c39" />
		</Popup>
	);
}

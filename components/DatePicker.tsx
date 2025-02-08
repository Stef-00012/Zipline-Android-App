import { styles } from "@/styles/components/datePicker";
import Popup from "./Popup";
import DateTimePicker from 'react-native-ui-datepicker';
import type { DatePickeMultipleProps, DatePickerRangeProps, DatePickerSingleProps } from "react-native-ui-datepicker/lib/typescript/DateTimePicker";
import Button from "./Button";

type Props = (DatePickeMultipleProps | DatePickerRangeProps | DatePickerSingleProps) & {
    open: boolean;
    onClose: () => void | Promise<void>
}

export default function DatePicker(props: Props) {
    const defaultProps: Props = {
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
        ...props
    }

    return (
        <Popup hidden={!props.open} onClose={props.onClose}>
            <DateTimePicker {...defaultProps}/>

            <Button
                text="Close"
                onPress={props.onClose}
                color="#171c39"
            />
        </Popup>
    );
}
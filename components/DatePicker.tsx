import { styles } from "@/styles/components/datePicker";
import Popup from "./Popup";
import DateTimePicker from 'react-native-ui-datepicker';
import type { DatePickeMultipleProps, DatePickerRangeProps, DatePickerSingleProps } from "react-native-ui-datepicker/lib/typescript/DateTimePicker";
import Button from "./Button";
import type { ReactNode } from "react";

type Props = (DatePickeMultipleProps | DatePickerRangeProps | DatePickerSingleProps) & {
    open: boolean;
    onClose: () => void | Promise<void>;
    children?: ReactNode
}

export default function DatePicker(props: Props) {
    const {
        open,
        onClose,
        children,
        ...datePickerProps
    } = props;

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
        ...datePickerProps
    };

    return (
        <Popup hidden={!open} onClose={onClose}>
            <DateTimePicker {...defaultProps}/>

            {props.children}

            <Button
                text="Close"
                onPress={props.onClose}
                color="#171c39"
            />
        </Popup>
    );
}
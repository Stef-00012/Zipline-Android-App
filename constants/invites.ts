import type { SelectProps } from "@/components/Select";

export const millisecondsMinute = 60 * 1000;
export const millisecondsHour = 60 * millisecondsMinute;
export const millisecondsDay = 24 * millisecondsHour;

export const dates: SelectProps["data"] = [
	{
		label: "Never",
		value: "never",
		milliseconds: null,
	},
	{
		label: "30 minutes",
		value: "30m",
		milliseconds: 30 * millisecondsMinute,
	},
	{
		label: "1 hour",
		value: "1h",
		milliseconds: millisecondsHour,
	},
	{
		label: "12 hours",
		value: "12h",
		milliseconds: 12 * millisecondsHour,
	},
	{
		label: "1 day",
		value: "1d",
		milliseconds: millisecondsDay,
	},
	{
		label: "3 days",
		value: "3d",
		milliseconds: 3 * millisecondsDay,
	},
	{
		label: "5 days",
		value: "5d",
		milliseconds: 5 * millisecondsDay,
	},
	{
		label: "7 days",
		value: "7d",
		milliseconds: 7 * millisecondsDay,
	},
];

import type { SelectProps } from "@/components/Select";

export const millisecondsMinute = 60 * 1000;
export const millisecondsHour = 60 * millisecondsMinute;
export const millisecondsDay = 24 * millisecondsHour;
export const millisecondsWeek = 7 * millisecondsDay;
export const millisecondsMonth = 30 * millisecondsDay;
export const millisecondsYear = 365 * millisecondsDay;

export const dates: SelectProps["data"] = [
	{
		label: "Never",
		value: "never",
		milliseconds: null,
	},
	{
		label: "5 minutes",
		value: "5m",
		milliseconds: 5 * millisecondsMinute,
	},
	{
		label: "10 minutes",
		value: "10m",
		milliseconds: 10 * millisecondsMinute,
	},
	{
		label: "15 minutes",
		value: "15m",
		milliseconds: 15 * millisecondsMinute,
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
		label: "2 hours",
		value: "2h",
		milliseconds: 2 * millisecondsHour,
	},
	{
		label: "3 hours",
		value: "3h",
		milliseconds: 3 * millisecondsHour,
	},
	{
		label: "4 hours",
		value: "4h",
		milliseconds: 4 * millisecondsHour,
	},
	{
		label: "5 hours",
		value: "5h",
		milliseconds: 5 * millisecondsHour,
	},
	{
		label: "6 hours",
		value: "6h",
		milliseconds: 6 * millisecondsHour,
	},
	{
		label: "8 hours",
		value: "8h",
		milliseconds: 8 * millisecondsHour,
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
		label: "1 week",
		value: "1w",
		milliseconds: millisecondsWeek,
	},
	{
		label: "1.5 weeks",
		value: "1.5w",
		milliseconds: 1.5 * millisecondsWeek,
	},
	{
		label: "2 weeks",
		value: "2w",
		milliseconds: 2 * millisecondsWeek,
	},
	{
		label: "3 weeks",
		value: "3w",
		milliseconds: 3 * millisecondsWeek,
	},
	{
		label: "1 month",
		value: "1M",
		milliseconds: millisecondsMonth,
	},
	{
		label: "1.5 months",
		value: "1.5M",
		milliseconds: 1.5 * millisecondsMonth,
	},
	{
		label: "2 months",
		value: "2M",
		milliseconds: 2 * millisecondsMonth,
	},
	{
		label: "3 months",
		value: "3M",
		milliseconds: 3 * millisecondsMonth,
	},
	{
		label: "4 months",
		value: "4M",
		milliseconds: 4 * millisecondsMonth,
	},
	{
		label: "6 months",
		value: "6M",
		milliseconds: 6 * millisecondsMonth,
	},
	{
		label: "1 year",
		value: "1y",
		milliseconds: millisecondsYear,
	},
];

export const formats: SelectProps["data"] = [
	{
		label: "Random",
		value: "random",
	},
	{
		label: "Date",
		value: "date",
	},
	{
		label: "UUID",
		value: "uuid",
	},
	{
		label: "Use File Name",
		value: "name",
	},
	{
		label: "Gfycat-style Name",
		value: "gfycat",
	},
];
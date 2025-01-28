import type { SelectProps } from "@/components/Select";

export const formats: SelectProps["data"] = [
	{ label: "Random", value: "random" },
	{ label: "Date", value: "date" },
	{ label: "UUID", value: "uuid" },
	{ label: "Use File Name", value: "name" },
	{ label: "Gfycat-style Name", value: "gfycat" },
];
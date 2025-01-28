import type { SelectProps } from "@/components/Select";
import type { APISettings, ShortenEmbed, UploadEmbed } from "@/types/zipline";

export const defaultUploadEmbed: UploadEmbed = {
	url: false,
	color: null,
	title: null,
	footer: null,
	thumbnail: false,
	timestamp: false,
	description: null,
	imageOrVideo: false,
};

export const defaultShortenEmbed: ShortenEmbed = {
	url: false,
	color: null,
	title: null,
	footer: null,
	thumbnail: false,
	timestamp: false,
	description: null,
	imageOrVideo: false,
};

export const settingNames = {
	chunksMax: "Max Chunks Size",
	chunksSize: "Chunks Size",
};

export const formats: SelectProps["data"] = [
	{ label: "Random", value: "random" },
	{ label: "Date", value: "date" },
	{ label: "UUID", value: "uuid" },
	{ label: "Use File Name", value: "name" },
	{ label: "Gfycat-style Name", value: "gfycat" },
];

import type MaterialSymbols from "@/components/MaterialSymbols";

interface SidebarOptionButton {
	icon: keyof typeof MaterialSymbols.glyphMap;
	invitesRoute: boolean;
	adminOnly: boolean;
	superAdminOnly: boolean;
	type: "button";
	route: string;
	name: string;
	subMenus: [];
}

interface SidebarOptionSelect {
	icon: keyof typeof MaterialSymbols.glyphMap;
	subMenus: SidebarOption[];
	invitesRoute: boolean;
	adminOnly: boolean;
	superAdminOnly: boolean;
	type: "select";
	name: string;
	route: null;
}

export type SidebarOption = SidebarOptionButton | SidebarOptionSelect;

export const sidebarOptions: SidebarOption[] = [
	{
		route: "/",
		name: "Home",
		icon: "home",
		adminOnly: false,
		superAdminOnly: false,
		invitesRoute: false,
		subMenus: [],
		type: "button",
	},
	{
		route: "/metrics",
		name: "Metrics",
		icon: "bar_chart",
		adminOnly: false,
		superAdminOnly: false,
		invitesRoute: false,
		subMenus: [],
		type: "button",
	},
	{
		route: "/files",
		name: "Files",
		icon: "insert_drive_file",
		adminOnly: false,
		superAdminOnly: false,
		invitesRoute: false,
		subMenus: [],
		type: "button",
	},
	{
		route: "/folders",
		name: "Folders",
		icon: "folder",
		adminOnly: false,
		superAdminOnly: false,
		invitesRoute: false,
		subMenus: [],
		type: "button",
	},
	{
		route: null,
		name: "Upload",
		icon: "cloud_upload",
		adminOnly: false,
		superAdminOnly: false,
		invitesRoute: false,
		type: "select",
		subMenus: [
			{
				route: "/upload/file",
				name: "File",
				icon: "upload_file",
				adminOnly: false,
				superAdminOnly: false,
				invitesRoute: false,
				subMenus: [],
				type: "button",
			},
			{
				route: "/upload/text",
				name: "Text",
				icon: "text_fields",
				adminOnly: false,
				superAdminOnly: false,
				invitesRoute: false,
				subMenus: [],
				type: "button",
			},
		],
	},
	{
		route: "/urls",
		name: "URLs",
		icon: "link",
		adminOnly: false,
		superAdminOnly: false,
		invitesRoute: false,
		subMenus: [],
		type: "button",
	},
	{
		route: null,
		name: "Administrator",
		icon: "admin_panel_settings",
		adminOnly: true,
		superAdminOnly: false,
		invitesRoute: false,
		type: "select",
		subMenus: [
			{
				route: "/admin/settings",
				name: "Settings",
				icon: "settings",
				adminOnly: true,
				superAdminOnly: true,
				invitesRoute: false,
				subMenus: [],
				type: "button",
			},
			{
				route: "/admin/users",
				name: "Users",
				icon: "people",
				adminOnly: true,
				superAdminOnly: false,
				invitesRoute: false,
				subMenus: [],
				type: "button",
			},
			{
				route: "/admin/invites",
				name: "Invites",
				icon: "mail_outline",
				adminOnly: true,
				superAdminOnly: false,
				invitesRoute: true,
				subMenus: [],
				type: "button",
			},
		],
	},
];

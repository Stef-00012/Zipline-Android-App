import type { IconProps } from "@react-native-material/core";
import type MaterialIcons from "@expo/vector-icons/MaterialIcons";

interface SidebarOptionButton {
    route: string;
    name: string;
    icon: keyof typeof MaterialIcons.glyphMap;
    adminOnly: boolean;
    invitesRoute: boolean;
    type: "button";
    subMenus: [];
}

interface SidebarOptionSelect {
    route: null;
    name: string;
    icon: keyof typeof MaterialIcons.glyphMap;
    adminOnly: boolean;
    invitesRoute: boolean;
    type: "select";
    subMenus: Array<SidebarOption>;
}

export type SidebarOption = SidebarOptionButton | SidebarOptionSelect;

export const sidebarOptions: Array<SidebarOption> = [
    {
        route: "/",
        name: "Home",
        icon: "home",
        adminOnly: false,
        invitesRoute: false,
        subMenus: [],
        type: "button"
    },
    {
        route: "/metrics",
        name: "Metrics",
        icon: "bar-chart",
        adminOnly: false,
        invitesRoute: false,
        subMenus: [],
        type: "button"
    },
    {
        route: "/files",
        name: "Files",
        icon: "insert-drive-file",
        adminOnly: false,
        invitesRoute: false,
        subMenus: [],
        type: "button"
    },
    {
        route: "/folders",
        name: "Folders",
        icon: "folder",
        adminOnly: false,
        invitesRoute: false,
        subMenus: [],
        type: "button"
    },
    {
        route: null,
        name: "Upload",
        icon: "cloud-upload",
        adminOnly: false,
        invitesRoute: false,
        type: "select",
        subMenus: [
            {
                route: "/upload/text",
                name: "Text",
                icon: "text-fields",
                adminOnly: false,
                invitesRoute: false,
                subMenus: [],
                type: "button"
            },
            {
                route: "/upload/file",
                name: "File",
                icon: "attach-file",
                adminOnly: false,
                invitesRoute: false,
                subMenus: [],
                type: "button"
            }
        ],
    },
    {
        route: "/urls",
        name: "URLs",
        icon: "link",
        adminOnly: false,
        invitesRoute: false,
        subMenus: [],
        type: "button"
    },
    {
        route: null,
        name: "Administrator",
        icon: "admin-panel-settings",
        adminOnly: true,
        invitesRoute: false,
        type: "select",
        subMenus: [
            {
                route: "/admin/settings",
                name: "Settings",
                icon: "settings",
                adminOnly: true,
                invitesRoute: false,
                subMenus: [],
                type: "button"
            },
            {
                route: "/admin/users",
                name: "Users",
                icon: "people",
                adminOnly: true,
                invitesRoute: false,
                subMenus: [],
                type: "button"
            },
            {
                route: "/admin/invites",
                name: "Invites",
                icon: "mail-outline",
                adminOnly: true,
                invitesRoute: true,
                subMenus: [],
                type: "button"
            }
        ],
    }
];
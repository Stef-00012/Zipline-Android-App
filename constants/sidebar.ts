interface SidebarOptionButton {
    route: string;
    name: string;
    icon: string;
    adminOnly: boolean;
    invitesRoute: boolean;
    subMenus: [];
    type: "button";
}

interface SidebarOptionSelect {
    route: null;
    name: string;
    icon: string;
    adminOnly: boolean;
    invitesRoute: boolean;
    subMenus: Array<SidebarOption>;
    type: "select";
}

type SidebarOption = SidebarOptionButton | SidebarOptionSelect;

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
        type: "select"
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
        subMenus: [
            {
                route: "/administrator/settings",
                name: "Settings",
                icon: "settings",
                adminOnly: true,
                invitesRoute: false,
                subMenus: [],
                type: "button"
            },
            {
                route: "/administrator/users",
                name: "Users",
                icon: "people",
                adminOnly: true,
                invitesRoute: false,
                subMenus: [],
                type: "button"
            },
            {
                route: "/administrator/invites",
                name: "Invites",
                icon: "mail-outline",
                adminOnly: true,
                invitesRoute: true,
                subMenus: [],
                type: "button"
            }
        ],
        type: "select"
    }
];
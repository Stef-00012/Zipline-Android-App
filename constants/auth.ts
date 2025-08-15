import type { APIUser } from "@/types/zipline";

export const minimumVersion = "4.2.3";

export const roles: Record<APIUser["role"], number> = {
	USER: 0,
	ADMIN: 1,
	SUPERADMIN: 2,
};

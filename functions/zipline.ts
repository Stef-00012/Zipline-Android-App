import * as db from "@/functions/database";
import type { APIRecentFiles, APIStats, APIUser } from "@/types/zipline";
import axios from "axios";

export async function getUser() {
    const token = db.get("token")
    const url = db.get("url")

    if (!url || !token) return null;

    try {
        const res = await axios.get(`${url}/api/user`, {
            headers: {
                Authorization: token
            }
        })

        return res.data.user as APIUser;
    } catch(e) {
        return null;
    }
}

export async function getRecentFiles() {
    const token = db.get("token")
    const url = db.get("url")

    if (!url || !token) return null;

    try {
        const res = await axios.get(`${url}/api/user/recent`, {
            headers: {
                Authorization: token
            }
        })

        return res.data as APIRecentFiles;
    } catch(e) {
        return null;
    }
}

export async function getStats() {
    const token = db.get("token")
    const url = db.get("url")

    if (!url || !token) return null;

    try {
        const res = await axios.get(`${url}/api/user/stats`, {
            headers: {
                Authorization: token
            }
        })

        return res.data as APIStats;
    } catch(e) {
        return null;
    }
}

export async function getUserAvatar() {
    const token = db.get("token")
    const url = db.get("url")

    if (!url || !token) return null;

    try {
        const res = await axios.get(`${url}/api/user/avatar`, {
            headers: {
                Authorization: token
            }
        })

        return res.data as string;
    } catch(e) {
        return null;
    }
}
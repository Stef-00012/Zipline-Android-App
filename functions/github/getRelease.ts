import { repoName, username } from "@/constants/updates";
import type { GitHubRelease } from "@/types/githubApi";
import axios from "axios";

export async function getLatestRelease(prerelease?: boolean): Promise<GitHubRelease | null> {
    if (prerelease) {
        try {
            const res = await axios.get(
                `https://api.github.com/repos/${username}/${repoName}/releases`,
            );

            const releasesData = res.data as GitHubRelease[];

            const latestPrerelease = releasesData.find(release => release.prerelease);

            if (!latestPrerelease) return null;

            return latestPrerelease;
        } catch(_e) {
            return null;
        }
    }

    try {
        const res = await axios.get(
            `https://api.github.com/repos/${username}/${repoName}/releases/latest`,
        );

        const releaseData = res.data as GitHubRelease;

        return releaseData;
    } catch(_e) {
        return null;
    }
}
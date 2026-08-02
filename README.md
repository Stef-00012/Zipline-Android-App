# Zipline Android App

> [!IMPORTANT]
> iOS is not supported

This is an android app made to manage your [Zipline V4](https://github.com/diced/zipline) (self-hosted) server and also upload files or shorten URLs. You can also share files to the app to upload them.

# Features

- Upload Files
- Shorten URLs
- Manage Files
- Manage URLs
- Manage Folders
- View your Stats
- Manage your own User
- Manage Users (requires an admin account)
- Manage Server Settings (requires a admin or superadmin account)

> [!NOTE]
> If you add a widget but it appears empty or has null values, set the app battery usage to "Unrestricted" and re-add them.<br />
This should fix it.

# Download

The app is available on the following platforms:
- [Google Play Store](https://play.google.com/store/apps/details?id=com.stefdp.zipline)
- [Forgejo Releases](https://git.stefdp.com/Stef/Zipline-Android-App/releases/download/latest/app-fdroid-release-signed.apk)
- [Fdroid](https://f-droid.org/en/packages/com.stefdp.zipline)

<a href="https://play.google.com/store/apps/details?id=com.stefdp.zipline">
  <img 
    src="https://git.stefdp.com/Stef/Zipline-Android-App/raw/branch/native/assets/google-play.png" 
    alt="Get on Google Play" 
    height="100" 
  />
</a>
<a href="https://f-droid.org/en/packages/com.stefdp.zipline">
  <img 
    src="https://git.stefdp.com/Stef/Zipline-Android-App/raw/branch/native/assets/f-droid.png" 
    alt="Get it on F-Droid"
    height="100" 
  />
</a>
<a href="https://git.stefdp.com/Stef/Zipline-Android-App/releases/download/latest/app-fdroid-release-signed.apk">
  <img
    src="https://git.stefdp.com/Stef/Zipline-Android-App/raw/branch/native/assets/forgejo.png" 
    alt="Get on Forgejo" 
    height="100" 
  />
</a>

# Creating a development build

To create a development build just run `./gradlew assembleDebug` or use the Android Studio Emulator

This will create an APK in `app/build/outputs/apk/debug/app-debug.apk`

> Building an apk

just run `./gradlew assembleRelease`

This will create an APK in `app/build/outputs/apk/release/app-release(-unsigned).apk`

# Flavortown

If you're from flavortown and don't want to host your own [Zipline V4](https://github.com/diced/zipline) instance, you can use the following credentials:

Server URL: `https://i.stefdp.com`<br />
Username: `FT Demo`<br />
Password: `FT Demo` [^1]

> [!NOTE]
> This user has the following limits:
> - 2GB max file size (across all uploaded files)
> - 20 shortened URLs

### Are you a Flavortown reviewer?

If you are a flavortown reviewer, you can DM me on [Slack](https://hackclub.enterprise.slack.com/team/U0823APHVK7) and I'll give you a instance as a `SUPERADMIN` (highest role).<br />
You can choose to either use a pre-made admin account on an already existing testing instance, or i can setup a temporary new instance and you create your own `SUPERADMIN` account (since this account can only be created on the first login).
<br />
Or well... you can just host your own instance of [Zipline V4](https://github.com/diced/zipline), just make sure you put it on a `https` URL

# Optimization

This section is for the [flavortown](https://flavortown.hackclub.com) "[Optimization](https://flavortown.hackclub.com/sidequests/optimization)" sidequest

- Instead of rendering all items (such as images in the "Files" tab or "Home" tab (recent files)) or tables' contents at once, I used uses the Android's `LazyColumn`/`LazyRow` which only renders the elements on screens and un-renders them when they're out of the screen.[^2]
- I used R8 minimization, which removes all the unused code from the final APK and renames long functions such as `runDeleteTemporaryFilesJob()` to `a()`, thus reducing the final APK bundle. [^3]
- I used XML drawables files instead of PNGs/JPGs, resulting in a smaller final APK size since XML drawables can be resized like SVGs instead of having different versions of the same PNG image for the different screen densities. [^4]
- When available from the Zipline API, i used query params such as `sortBy` and `sortorder` to sort, `searchField` and `searchQuery` to search instead of filtering/sorting myself, thus saving memory, as it doesn't have to store in memory URLs or files that won't be displayed, improved performance as the filtering/searching is done on the server instead of the weaker mobile CPU. Examples:
  - [`getFiles`](https://git.stefdp.com/Stef/Zipline-Android-App/src/commit/8616b65d9b9eb25515ca22ff65ba87700340af4e/app/src/main/java/com/stefdp/zipline/network/ApiService.kt#L500-L505) uses `sortBy` and `sortOrder` to sort, `searchField` and `searchQuery` to search and `favorite` to filter for favorite files.
  - [`getUrls`](https://git.stefdp.com/Stef/Zipline-Android-App/src/commit/8616b65d9b9eb25515ca22ff65ba87700340af4e/app/src/main/java/com/stefdp/zipline/network/ApiService.kt#L359-L360) uses `searchField` and `searchQuery` (at the time of writing, the [API endpoint](https://github.com/diced/zipline/blob/bf7a4e92e3eeb76c1d72df78152db503948465e7/src/server/routes/api/user/urls/index.ts#L160-L163) doesn't seem to support sort query parameters).

[^1]: Please do not change the password
[^2]: Android's documentation for lazy list: https://developer.android.com/develop/ui/compose/lists#lazy
[^3]: Android's documentation for R8 optimization: https://developer.android.com/topic/performance/app-optimization/enable-app-optimization#overview
[^4]: Android's documentation for vector drawables: https://developer.android.com/develop/ui/views/graphics/vector-drawable-resources#key-points
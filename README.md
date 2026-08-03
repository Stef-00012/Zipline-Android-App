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
    style="margin-left: 5px; margin-right: 5px;"
  />
</a>
<a href="https://f-droid.org/en/packages/com.stefdp.zipline">
  <img 
    src="https://git.stefdp.com/Stef/Zipline-Android-App/raw/branch/native/assets/f-droid.png" 
    alt="Get it on F-Droid"
    style="margin-left: 5px; margin-right: 5px;"
  />
</a>
<a href="https://git.stefdp.com/Stef/Zipline-Android-App/releases/download/latest/app-fdroid-release-signed.apk">
  <img
    src="https://git.stefdp.com/Stef/Zipline-Android-App/raw/branch/native/assets/forgejo.png" 
    alt="Get on Forgejo"
    style="margin-left: 5px; margin-right: 5px;"
  />
</a>

# Creating a development build

To create a development build just run `./gradlew assembleDebug` or use the Android Studio Emulator

This will create an APK in `app/build/outputs/apk/debug/app-debug.apk`

> Building an apk

just run `./gradlew assembleRelease`

This will create an APK in `app/build/outputs/apk/release/app-release(-unsigned).apk`
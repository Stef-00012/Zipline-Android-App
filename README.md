# Zipline Android App

> [!IMPORTANT]
> iOS is not supported

This is an android app made to manage your [Zipline V4](https://github.com/diced/zipline/tree/v4) (self-hosted) server and also upload files or shorten URLs. You can also share files to the app to upload them.

# Download

To download the app's APK, just go to the [latest release](https://github.com/Stef-00012/zipline-android-app/releases/latest) and download the `app-release.apk` file.\
Or directly download it from [here](https://github.com/Stef-00012/zipline-android-app/releases/latest/download/app-release.apk).

# Creating a development build

> [!IMPORTANT]
> Requires an android device connected to the laptop and ADB

1. `bun install`
2. `bun run prebuild`
3. `bun run run:android`

(this will create a development apk and automatically install in your device)

> Building an apk

> [!NOTE]
> add `--local` flag if you don't have a Expo EAS account or want to build it locally on your PC\
> add `--output /path/to/file.apk` to save the apk in that path

1. `bun install`
2. `bun install --global eas-cli`
3. `eas build --clear-cache --platform android --profile preview`

This will create an apk but won't automatically install

# To-DO
- [ ] Try to allow chunked uploads
- [ ] Try to improve popups (kinda ugly that thy can't go over the header)
- [ ] Fix app freezing when selecting large files (caused by copying the file in a folder the app can access, specifically, `copyToCacheDirectory` in `app/(app)/(files)/upload/file.tsx` in `DocumentPicker.getDocumentAsync()`)
- [ ] try to make animations smoother
- [ ] Fix recent files section having no height when there is no recent file
- [ ] Remove `useAuth` hook and instead use the `AuthContext` context to avoid fetching user login data on every page change
- [ ] Move public API settings to `ZiplineContext` to avoid fetching them on every page change
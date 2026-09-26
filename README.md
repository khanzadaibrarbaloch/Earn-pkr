# PKR Earn Android wrapper

This Android project wraps the existing PKR Earn web app and provides the native `window.AdMobReward` bridge expected by the web app.

## AdMob
- App ID: `ca-app-pub-9075427382575085~8259418874`
- Rewarded unit: `ca-app-pub-9075427382575085/1354350740`

For development, replace the live rewarded unit with Google's official test rewarded unit before testing. Do not repeatedly test live ads.

## Build
Requires Android SDK 35 and JDK 17+.

`gradle assembleDebug`

The debug APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.

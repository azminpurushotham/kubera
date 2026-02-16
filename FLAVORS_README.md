# Product Flavors: Genaral & Cloud

## Overview
The app supports two product flavors that can be installed side-by-side:

| Flavor | Application ID | Use Case |
|--------|----------------|----------|
| **genaral** | `com.collection.management.genaral` | Offline only – Local Room database |
| **cloud** | `com.collection.management.cloud` | Firebase collection only |

## Building
- **Genaral flavor**: `./gradlew assembleGenaralDebug` or Build Variant: `genaralDebug`
- **Cloud flavor**: `./gradlew assembleCloudDebug` or Build Variant: `cloudDebug`

## Firebase Setup

### Fix "Failed to get service from broker" / `SecurityException: Unknown calling package name`

This error occurs when **SHA fingerprints are not registered** in Firebase. Add them for Google Sign-In to work:

1. **Firebase Console** → [Project Settings](https://console.firebase.google.com/project/_/settings/general)
2. Under **Your apps**, select the Android app: `com.collection.management.genaral`
3. Click **Add fingerprint** and add these:

   **Debug SHA-1** (from `./gradlew signingReport`):
   ```
   53:05:56:DD:CB:48:78:D5:C8:33:F0:FF:06:38:6A:26:F2:AB:F9:AF
   ```

   **Debug SHA-256**:
   ```
   36:3B:06:A4:12:49:16:A6:A3:6E:B0:F2:92:E0:1F:5F:EA:5D:4B:9C:65:1D:80:51:5A:77:E6:CB:8E:73:82:64
   ```

4. **Re-download** `google-services.json` (Project Settings → Your apps → Download) and replace it in `app/src/genaral/`
5. In **Authentication → Sign-in method**, enable **Google**
6. Rebuild and run: `./gradlew assembleGenaralDebug`

> For release builds, add your release keystore SHA-1 and SHA-256 too.

### App registration
1. Register **two** Android apps in Firebase Console (aligned with `google-services.json`):
   - Package: `com.collection.management.genaral`
   - Package: `com.collection.management.cloud`
2. Place `google-services.json` in:
   - `app/src/genaral/google-services.json`
   - `app/src/cloud/google-services.json`

## DI / Data Source
- **CloudDataModule** (in `app/src/cloud/`) provides Firebase-backed repositories
- **LocalDataModule** (in `app/src/genaral/`) provides Room-backed repositories

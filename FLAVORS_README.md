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
1. In Firebase Console, register **two** Android apps (aligned with `google-services.json`):
   - Package: `com.collection.management.genaral`
   - Package: `com.collection.management.cloud`
2. Place `google-services.json` in:
   - `app/src/genaral/google-services.json`
   - `app/src/cloud/google-services.json`

## DI / Data Source
- **CloudDataModule** (in `app/src/cloud/`) provides Firebase-backed repositories
- **LocalDataModule** (in `app/src/genaral/`) provides Room-backed repositories

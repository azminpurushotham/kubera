# Product Flavors: Local & Cloud

## Overview
The app supports two product flavors that can be installed side-by-side:

| Flavor | Application ID | Use Case |
|--------|----------------|----------|
| **local** | `com.collection.management.local` | Local Room database (future) / Offline-first |
| **cloud** | `com.collection.management.cloud` | Firebase/Firestore cloud backend |

## Building
- **Local flavor**: `./gradlew assembleLocalDebug` or Build Variant: `localDebug`
- **Cloud flavor**: `./gradlew assembleCloudDebug` or Build Variant: `cloudDebug`

## Firebase Setup
1. In Firebase Console, register **two** Android apps:
   - Package: `com.collection.management.local` (already configured)
   - Package: `com.collection.management.cloud`
2. Download the `google-services.json` for each app
3. Place them in:
   - `app/src/local/google-services.json`
   - `app/src/cloud/google-services.json`

The placeholder files use the same project config with updated package names. Replace with your Firebase Console downloads for production.

## DI / Data Source
- **CloudDataModule** (in `app/src/cloud/`) provides Firebase-backed repositories
- **LocalDataModule** (in `app/src/local/`) currently uses Firebase implementations
  - When Room database is implemented, switch to `Local*Repository` classes here

<!--
  Copyright (c) Meta Platforms, Inc. and affiliates.

  This source code is licensed under the MIT license found in the
  LICENSE file in the root directory of this source tree.
-->

# Horizon Platform SDK — Android Sample Apps

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Meta Quest](https://img.shields.io/badge/Meta_Quest-Developer-1877F2)](https://developers.meta.com/horizon/)

Standalone Android sample apps demonstrating each [Horizon Platform SDK](https://developers.meta.com/horizon/documentation/android-apps/ps-setup-kotlin/) API.

## What is this?

This repository contains **16 standalone Gradle projects**, one for each public Horizon Platform SDK API. Each sample is a minimal, self-contained Android app built with Kotlin and Jetpack Compose that shows how to integrate and call a single Platform SDK API on Meta Quest devices running Horizon OS.

These samples are designed for **third-party developers** building apps for the Meta Quest ecosystem. Each app follows the same architecture (MVVM with Compose) so you can focus on the API usage rather than boilerplate.

## Prerequisites

- **Android Studio** Ladybug or later (AGP 9.0+)
- **JDK 11** or later
- **Meta Quest device** with [Developer Mode](https://developers.meta.com/horizon/documentation/native/android/mobile-device-setup/) enabled
- **Application ID** — follow the [setup guide](https://developers.meta.com/horizon/documentation/android-apps/ps-setup-kotlin/) to create your app and obtain your Application ID

## Getting started

1. Clone this repository.
2. Open any sample directory in Android Studio (e.g., `users/`).
3. In `app/src/main/java/.../MainActivity.kt`, replace the `APPLICATION_ID` property with your own Application ID:
   ```kotlin
   private val APPLICATION_ID = "your-application-id-here"
   ```
4. Build and run on your Quest device:
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## Sample apps

| Sample | API | Description |
|--------|-----|-------------|
| `abusereport/` | Abuse Report | Listen for abuse report button press events |
| `achievements/` | Achievements | Unlock and query achievement definitions and progress |
| `application/` | Application | Query application metadata and version info |
| `applicationlifecycle/` | Application Lifecycle | Monitor app lifecycle state transitions |
| `assetfile/` | Asset File | Download and manage downloadable content (DLC) asset files |
| `consent/` | Consent | Check and request user consent status |
| `deviceapplicationintegrity/` | Device Application Integrity | Verify device and application integrity |
| `grouppresence/` | Group Presence | Set and manage group presence for multiplayer sessions |
| `iap/` | In-App Purchases | Browse products, make purchases, and verify ownership |
| `languagepack/` | Language Pack | Query and manage localized language packs |
| `leaderboards/` | Leaderboards | Submit scores and query leaderboard rankings |
| `notifications/` | Notifications | Send and manage in-app notifications |
| `rateandreview/` | Rate and Review | Request users to rate and review the app |
| `richpresence/` | Rich Presence | Set detailed presence info visible to friends |
| `useragecategory/` | User Age Category | Query the user's age category for content gating |
| `users/` | Users | Retrieve user profile information |

## Project structure

Each sample follows an identical layout:

```
<sample>/
├── app/
│   ├── build.gradle.kts              # App build config with SDK dependency
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   └── java/.../
│   │   │       ├── MainActivity.kt   # Entry point — SDK init + Compose UI
│   │   │       └── *ViewModel.kt     # Business logic + UI state
│   │   └── test/                     # Unit tests
│   └── testapps.keystore             # Shared signing key for dev builds
├── build.gradle.kts                  # Root build file
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml            # Version catalog
├── gradlew
└── gradlew.bat
```

## Tech stack

- **Language:** Kotlin 2.0
- **UI:** Jetpack Compose with Material 3
- **Architecture:** MVVM (ViewModel + sealed UiState)
- **Build:** Gradle with Version Catalogs
- **SDK:** Horizon Platform SDK (`com.meta.horizon.platform.sdk`)
- **Min SDK:** 34 (Android 14)
- **Target SDK:** 36

## Contributing

We welcome contributions! Please read our [Contributing Guide](CONTRIBUTING.md) for details on how to submit pull requests, report issues, and contribute to the project.

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior to opensource-conduct@meta.com.

## License

MIT — see [LICENSE](LICENSE) for details.

Copyright (c) 2025 Meta Platforms, Inc.

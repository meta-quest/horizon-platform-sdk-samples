<!--
  Copyright (c) Meta Platforms, Inc. and affiliates.

  This source code is licensed under the MIT license found in the
  LICENSE file in the root directory of this source tree.
-->

# Contributing to Horizon Platform SDK Sample Apps

## Welcome

Thanks for your interest in contributing to the Horizon Platform SDK sample apps.

This repository contains standalone Android sample apps demonstrating each Horizon Platform SDK API for third-party developers. Before contributing, please review our [Code of Conduct](CODE_OF_CONDUCT.md).

## How to Contribute

Contributions are accepted through GitHub pull requests.

1. Fork the repository.
2. Create a feature branch from `main`.
3. Make your changes in a focused commit series.
4. Open a pull request with a clear description of what changed and why.

## Sample App Structure

Each sample app lives in its own directory and should follow this pattern:

- `app/build.gradle.kts` -- app build config with SDK dependency
- `app/src/main/java/.../MainActivity.kt` -- entry point with SDK initialization and Compose UI
- `app/src/main/java/.../*ViewModel.kt` -- business logic and UI state management
- `app/src/test/` -- unit tests

Use `users/` as a template for structure, style, and level of detail.

## Writing Guidelines

When writing or updating sample apps:

- Keep code minimal and focused on demonstrating a single API
- Follow Kotlin and Jetpack Compose best practices
- Use MVVM architecture with sealed UiState classes
- Include unit tests for ViewModel logic
- Do not hardcode Application IDs — use the placeholder pattern with `IllegalStateException`

## Reporting Bugs

If you find an issue in a sample app, open an issue on GitHub.

Please include:

- The sample app name
- A clear description of the problem
- Steps to reproduce (if applicable)

## Contributor License Agreement (CLA)

All contributors must sign the Meta Contributor License Agreement (CLA) before pull requests can be merged.

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). Please report unacceptable behavior to opensource-conduct@meta.com.

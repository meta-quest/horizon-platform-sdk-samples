# Agent Instructions — Horizon Platform SDK Android Sample Apps

Collection of standalone Android sample apps, one per public Horizon Platform SDK API, showing how to integrate a single Platform SDK feature on Meta Quest devices running Horizon OS.

## Source-of-truth files (read these first, do not duplicate their contents in this file)

For setup, build steps, SDK versions, and project layout, read:

- `README.md` — official setup and instructions
- `<sample>/build.gradle.kts` + `<sample>/gradle/libs.versions.toml` — Android Gradle / SDK versions for each sample
- `<sample>/app/src/main/AndroidManifest.xml` — package id, permissions, target API
- `LICENSE` — license terms

## Quest / Horizon-specific notes

- Each top-level directory (`abusereport/`, `achievements/`, `users/`, etc.) is its own independent Gradle root with its own version catalog — treat them as separate projects rather than a multi-module build.
- Every sample requires a real Application ID from the Meta Quest developer dashboard; the placeholder `APPLICATION_ID` constant in `MainActivity.kt` must be replaced before the Platform SDK calls will work end-to-end.
- The PSDK artifacts use the `-kotlin` suffix (e.g. `core-kotlin`, `users-kotlin`) — do not silently swap to Java variants.
- Each sample ships a shared dev signing key at `app/testapps.keystore`; preserve it when refactoring build files.

# Meta Quest tooling

This is a Meta Quest / Horizon OS sample. The bespoke intro above is the source of truth for what this project is and how it's built — use it (and the files it points at) instead of restating facts from memory.

When the user asks anything about Quest device behavior, build / deploy / debug / capture flows, on-device performance, or Horizon OS APIs, reach for these tools instead of generic Android answers:

- **`hzdb`** — Quest-aware ADB wrapper (device list, install / launch / stop, logs, screenshots, Perfetto traces, on-device docs search). Already wired up as an MCP server via `.mcp.json`, `.vscode/mcp.json`, and `.cursor/mcp.json`. Also runnable directly: `npx -y @meta-quest/hzdb <subcommand>`.
- **Meta Quest Agentic Tools** — the full skill set, including Android-specific skills: <https://github.com/meta-quest/agentic-tools>. Install per your client (Claude Code: `/plugin install meta-vr@meta-quest`; Gemini CLI: `gemini extensions install https://github.com/meta-quest/agentic-tools`; Cursor / VS Code: install the **Meta Horizon** extension from the Marketplace).

A few behavior expectations:

- **Read this repo's files first.** Before answering anything project-specific, read `README.md` and whichever source-of-truth files the intro above points at. Don't restate their contents in chat — quote or link instead.
- **Use `hzdb` for device-side work.** Anything that touches an attached Quest (install, launch, logs, screenshot, capture, manifest inspection) goes through `hzdb`, not raw `adb`.
- **Check live Horizon OS docs before answering API questions.** `hzdb docs search "..."` queries the live docs; training data on Horizon OS APIs goes stale fast.
- **Don't fabricate SDK / engine versions.** If a version isn't visible in this repo's files, say so rather than guessing.

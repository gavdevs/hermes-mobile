# Compact layout implementation evidence

## Scope and baseline

- Branch observed: `lp3`; starting HEAD: `04520c63`.
- `git merge-base --is-ancestor upstream/main HEAD` returned 0 before editing. Remote refs were not fetched; freshness against remote upstream was not established.
- Implemented task groups 1–6. No push, PR, adb, installation, emulator driving, or screenshot capture.
- Source changes are under `app/src`; this is the only OpenSpec file changed. `tasks.md` checkboxes and `openspec/specs/` were left untouched.
- Visual acceptance belongs to the parent/owner. Compilation is not visual proof.

## Commits

| Group | Commit | Change |
| --- | --- | --- |
| 1 | `42b9a9a419` | Height-only compact local, root provider, boundary previews |
| 2 | `f50cf11f2e` | Compact History actions, summary and filter sheet |
| 3 | `fe3c735bbf` | Single-row placement of existing composer controls; overflow model/reasoning, context meter, text-only empty state |
| 4 | `15d008e34a` | Curated compact drawer and remembered collapsible groups |
| 5 | `38c018303b` | Safe-area login layout, compact scrollable form and pinned action |
| 6 | `aecc9e6ff6` | Fork-only AMOLED preset default and saved-preset round-trip coverage |
| Integration fix | `c4416db929` | Import the existing `reasoningSupport` extension used by the compact reasoning menu |

Group 3 also restores the original regular History header inline rather than using the extracted sheet controls on the regular path. The six numbered implementation commits remain separate; the compiler integration fix is an additional commit.

## Gates

Environment for every Gradle invocation:

```bash
export ANDROID_HOME=$HOME/.local/share/light-tern-toolchain/android-sdk
export JAVA_HOME=$HOME/.local/share/light-tern-toolchain/jdk17
export PATH=/tmp/hermes-lp3-tools:$PATH
```

The repository's `ktlintCheck` invokes an external `ktlint`. The first group-1 invocation failed with `ktlint: command not found`; ktlint 1.8.0 was downloaded into temporary tooling, without changing repository build configuration.

After each numbered implementation commit:

```bash
./gradlew ktlintCheck checkColorLiterals
```

- Groups 1–5: final post-commit invocation reported `BUILD SUCCESSFUL in 8s`, `2 actionable tasks: 2 executed`.
- Groups 2–5 initially reported formatting errors; those were corrected with source patches and amended into the respective current commit before the passing gate.
- Group 6: both gates passed in the combined final-build invocation, but Kotlin compilation initially failed on a missing `reasoningSupport` import. The integration commit fixes that compiler error.
- Color guard output: `checkColorLiterals: no hardcoded Color literals found.`

After the last source edit/integration commit:

```bash
./gradlew ktlintCheck checkColorLiterals testDebugUnitTest \
  --tests com.m57.hermescontrol.data.config.ServerStoreTest \
  --tests com.m57.hermescontrol.theme.ThemePaletteTest \
  assembleDebug
```

Observed output:

```text
> Task :app:ktlintCheck
> Task :app:checkColorLiterals
> Task :app:assembleDebug
> Task :app:testDebugUnitTest
BUILD SUCCESSFUL in 1m 44s
49 actionable tasks: 17 executed, 32 up-to-date
```

- `ServerStoreTest`: 11 tests, 0 skipped, 0 failures, 0 errors.
- `ThemePaletteTest`: 11 tests, 0 skipped, 0 failures, 0 errors.
- APK: `app/build/outputs/apk/debug/app-debug.apk`.
- Compiler warnings include existing deprecated Android/Material APIs; compilation succeeded.
- No view-model event/reducer/send logic was changed. No full unit suite, instrumentation suite, or preview renderer was run.

## Task status

Checked means implemented and statically gated, not visually accepted:

- [x] 1.1 Height-only helper/local; default false, threshold `< 520`.
- [ ] 1.2 Root provider implemented and boundary previews at 519dp/520dp compile; preview assertions have not been rendered/executed.
- [x] 1.3 Boundary source grep shows `LocalConfiguration.current.screenHeightDp < 520`; no device-model, width or build-flavour boundary was introduced.
- [x] 2.1 Compact actions and one clearable filter summary.
- [x] 2.2 Sheet uses the same Material chip/search/stats APIs, labels, tags and view-model callbacks as the existing header.
- [x] 2.3 Compact header switch; list, paging, grouping, selection and undo code retained.
- [ ] 2.4 Screenshot/interaction acceptance pending.
- [x] 3.1 Existing field/action controls placed in one compact row, with existing outer 12dp/8dp padding.
- [x] 3.2 Existing gesture owner, send availability, attachments, active recording and banners retained. Stop/listening may keep a secondary action while active.
- [x] 3.3 Model and reasoning entries available inside the existing overflow. The model picker itself has no effort controls, so effort is a native submenu with the same capability/pending rules.
- [x] 3.4 Context fraction/detail callback moved below the app bar on compact; visible meter 2dp, independent touch target 48dp.
- [x] 3.5 Compact empty state contains the existing headline/subtitle without decorative art.
- [ ] 3.6 Screenshot/keyboard/send-enablement/sheet-opening acceptance pending.
- [x] 4.1 Curated rows plus groups populated from `ScreenRegistry`, preserving profile-lock filtering.
- [x] 4.2 Compact switch; expansion state remembered at the navigation host, navigation still uses `NavigationController`.
- [ ] 4.3 Drawer fit, reachability and close/reopen acceptance pending. Existing drawer code has no cron/unread badge rendering to migrate.
- [x] 5.1 Navigation-bar insets for all configurations; compact form scrolls independently of the primary action.
- [ ] 5.2 Fresh-install screenshot acceptance and regular-size visual comparison pending.
- [x] 6.1 Separate fork-only default commit; no palettes, tokens or preset-registry entries changed. Explicit saved preset choices round-trip in the test.
- [ ] 6.2 First-launch visual acceptance pending; unconditional dark/pure-black launch requires the theme-mode decision below.

Baseline group 0 and visual wrap-up 7.2 were not performed: this brief explicitly assigns emulator verification to the parent. For 7.1, the listed lint/color and scoped unit gates passed; no full-suite claim. Roadmap changes (7.3) were not made because scope did not change and the brief forbids edits outside the allowed paths.

## Before/after measurements

No independently measured before/after screenshot pair is available from this execution.

| Surface / criterion | Before | After |
| --- | --- | --- |
| History rows occupy at least 60% of viewport | Not measured here | Parent measurement pending |
| Empty composer at most 72dp | Not measured here | Parent measurement pending |
| Keyboard open: at least three conversation lines | Not measured here | Parent measurement pending |
| Drawer default fits; each destination within two taps | Not measured here | Parent interaction/screenshot pending |
| Login primary action visible without scrolling | Not measured here | Parent screenshot pending |
| Regular-height rendering unchanged except authorized auth insets / fork default | No comparison captured | Parent comparison pending |
| First-run AMOLED/pure black | Not measured here | Mode-dependent; see question below |

The context meter's 2dp visual line reserves a 48dp non-overlapping hit area. This preserves the mandatory target size but consumes more vertical space than the visible line; the keyboard-open criterion must be measured rather than inferred.

## Open decision and limits

1. **Theme mode:** should fresh installs default to `DARK`, or retain `SYSTEM`? The documents specify a preset-id-only change, while AMOLED is dark-only and intentionally falls back to Default-light when system mode is light. This implementation changes only the preset default, leaves saved modes intact, and leaves 6.2 unchecked rather than silently forcing dark mode.
2. The all-configuration auth safe-area fix and the fork-only first-run preset are explicitly documented exceptions to regular-phone parity. Other regular paths retain their existing controls, text, callbacks and rendering branches, but pixel parity has not been measured.
3. Parent verification still needs normal/enlarged text, RTL, theme variants, TalkBack, keyboard-open geometry, clearing filters, selection/prune/undo, recording/dictation, drawer expansion, and model/context sheet opening.

## Review fixes — LP3 tasks 6.2 and 3.6

- Task 6.2 supersedes the theme-mode question above: the fork now defaults to
  `AMOLED` + `DARK` in `ServerStoreState`, the legacy migration's missing/invalid
  mode fallback, and `AuthManager`'s initial theme flow. Dynamic colors remain
  off by default. Saved modes are still decoded, not overwritten.
  `ServerStoreTest` now checks constructed/empty-JSON defaults and round-trips
  every preset/mode combination, including explicit `LIGHT` and `SYSTEM`.
  No palette, registry, or preset-switcher changes.
- Task 3.6 uses `WindowInsets.isImeVisible` only with `LocalCompactLayout`.
  `CompactChatAppBar.kt` adds a compact-only scaffold adapter because the shared
  scaffold has no bar-visibility slot and is outside the permitted edit scope.
  The compact scaffold omits the entire `TopAppBar` while typing; its top inset
  and padding therefore disappear too. Its content remains at one composition
  site across IME changes, preserving the focused composer. Closing the IME
  restores the bar. Regular chat delegates to `HermesScaffold` with its previous
  arguments. Drawer ownership, feedback snackbars, menu actions, theme tokens,
  and existing 48dp action targets are retained.
- The compact context meter's entire 48dp hit row also disappears while typing;
  no undersized clickable hairline replaces it. Static arithmetic from the
  review's estimates is `44px + 110px = 154px` for bar removal alone; removing
  the context row additionally reclaims 48dp when present. A >=220px or >=3-line
  claim still requires the parent's density/layout measurement; it is not
  established by compilation or these mixed px/dp estimates.
- Static inspection traced the fresh defaults through the theme dispatcher:
  `DARK` resolves AMOLED's dark palette rather than its Default-light fallback.
  Runtime screenshots, keyboard geometry, and visual acceptance remain with
  the parent. No emulator/adb or preview rendering; no task boxes changed.
- After the last source edit, using the SDK/JDK/ktlint environment above:

  ```bash
  ./gradlew ktlintCheck checkColorLiterals testDebugUnitTest \
    --tests com.m57.hermescontrol.data.config.ServerStoreTest \
    --tests com.m57.hermescontrol.theme.ThemePaletteTest assembleDebug
  ```

  Result: `BUILD SUCCESSFUL in 36s`; 49 actionable tasks (13 executed,
  36 up-to-date). Both lint/color gates executed; no hardcoded color literals.
  Each test class reported 11 tests, zero failures/errors/skips. Debug APK:
  `app/build/outputs/apk/debug/app-debug.apk`. Existing crypto API deprecation
  warnings remain. No full unit suite or instrumented tests were run.

## Finding 3 — bounded auth probe completion

- `AuthLoginViewModel.probe()` now bounds its wait to 25 seconds and publishes
  `probing = false` from `finally`. Timeout, cancellation, and unexpected failure
  use `auth_login_error_unreachable`; completed certificate failures retain their
  existing localized error/dialog. Reachable-dashboard result mapping is unchanged.
- Shared probe-client lookup now happens on the IO worker, not during ViewModel
  construction. The worker is activity-scope-owned, outside the timeout's children:
  cancelling it does not wait for blocked initialization/body reads. Uninterruptible
  work may remain on IO until it returns, but cannot hold the UI spinner or publish
  a late result. Status/SPA cancellation is rethrown rather than swallowed.
- Four regression tests use existing MockK/coroutines-test dependencies, including
  virtual-time expiry while client lookup is blocked, successful retry, initialization
  failure, scope cancellation, and SPA-fetch cancellation.
- Final source verification:
  `./gradlew ktlintCheck checkColorLiterals testDebugUnitTest --tests com.m57.hermescontrol.ui.authlogin.AuthLoginViewModelTest assembleDebug`
  passed (`BUILD SUCCESSFUL in 11s`); 31 tests, zero failures/errors/skips.
  APK: `app/build/outputs/apk/debug/app-debug.apk`.
- Only the ViewModel, its existing test file, and this evidence note changed.
  No emulator/adb, visual acceptance, push, or task-checkbox edits.

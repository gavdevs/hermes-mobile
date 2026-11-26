# Design

## Compact boundary

- One predicate: `LocalConfiguration.current.screenHeightDp < 520`. Published as
  `LocalCompactLayout: ProvidableCompositionLocal<Boolean>` (default false) provided once in the
  theme/root composable from the activity content. Never re-evaluated per screen; screens read the
  local only.
- 520dp threshold: LP3 = 472dp; smallest common phone ≈ 640dp; headroom covers DPI drift without
  catching tablets or foldable inner screens.
- Compact decision must not depend on width (411dp is a normal phone width) and never on device
  model or build flavour.

## History (`SessionsScreen`)

- Keep all existing state hoisted in the view model: section (conversations/automations/archived),
  source filter, project filter, group-by-project, search text, bulk selection.
- Compact header becomes: top app bar (title, search icon, filter icon with active-filter badge,
  overflow with refresh/prune/bulk/new) + one optional summary chip row (single chip describing
  the active non-default filter, tap reopens the sheet, trailing X clears).
- The bottom sheet reuses the exact chip and search composables from the current header so
  accessibility labels, semantics and strings do not fork. It is a `ModalBottomSheet` sized to
  content with the list of chip rows and the search field.
- Stats row (Total / Messages loaded / Prune) moves into the sheet header area; prune keeps its
  confirmation flow untouched.
- The pager/list behavior, paging, pinned headers, group headers, selection mode and undo snackbar
  are untouched.

## Chat composer (`ChatComposer` / `ComposerToolbar`)

- Compact composer = one rounded row: text field (flex) + attach icon + 48dp action button that
  shows mic when the field is empty and send when it has content (existing morph logic reused).
- Removed from the compact row: model/effort pill, context-usage chip, and any secondary action
  strip. Their entry points on compact:
  - model/effort → existing top-bar overflow menu (`ChatOverflowMenu`), which already opens the
    full model sheet; show current model name as part of that menu item's label.
  - context usage → 2dp hairline under the app bar (fill = fraction, no text); tapping opens the
    existing `ContextDetailSheet` with the current numbers.
- Empty state: keep headline + one-line subtitle; drop the large decorative bubble icon on
  compact.
- Keep attachment tray, replies, queued-send chips, approvals, clarify and dictation flows
  unchanged; they may occupy extra height while active, which is acceptable.
- Composer side padding compact: 12dp; vertical padding 8dp; total resting height target ≈ 56dp.

## Drawer

- Compact drawer shows: header (Hermes + status), then Chat, History, Cron, Settings as top-level
  rows, then collapsible group headers (`Automation`, `Configure`, `Develop`) that expand in place.
  Group contents reuse the existing `ScreenRegistry` destinations and icons.
- Collapsible groups persist expanded state only in-process (remembered by navigation state), no
  new storage.
- Badges (cron running, unread) unchanged.

## Auth login

- Wrap content in `Modifier.navigationBarsPadding()` (or include `WindowInsets.navigationBars` in
  the column's contentPadding) and make the column scrollable when content exceeds the viewport —
  both configurations, since the bug affects regular phones behind gesture nav too.
- Compact spacing: reduce vertical gaps between fields/buttons to the DESIGN.md small step; the
  primary action stays a 48dp-tall button pinned inside the safe area.

## Theme default (fork-only commit)

- Default preset id switches to the existing AMOLED preset (pure black background, existing
  palette). No new palette entries; no token changes. This lives in its own commit, never proposed
  upstream.

## Verification protocol

- Build: `ANDROID_HOME=$HOME/.local/share/light-tern-toolchain/android-sdk
  JAVA_HOME=$HOME/.local/share/light-tern-toolchain/jdk17 ./gradlew assembleDebug`
  (`local.properties` with `sdk.dir` is already present and git-ignored).
- Install and drive: `adb -s emulator-5584 install -r app/build/outputs/apk/debug/app-debug.apk`,
  app id `com.m57.hermescontrol.dev`. Disable the AVD's HOME app while auditing:
  `adb shell pm disable-user --user 0 com.mealforge.lightphone` (re-enable after).
- Login uses loopback token mode, not credentials: `adb reverse tcp:9121 tcp:9121`, app URL
  `http://127.0.0.1:9121`; the app auto-extracts `__HERMES_SESSION_TOKEN__` from the SPA.
- Screenshots: `adb exec-out screencap -p`, saved under
  `~/Projects/light/lp3-hermes-mobile-audit/after/<name>.png` (local only — never committed).
- Gates: ktlint + color guard (`./gradlew ktlintCheck` or the repo's check task), unit tests for
  any touched view-model logic, and the acceptance measurements from the spec delta.

## Rebase discipline

- Compact implementations live in new files (`CompactSessionsHeader.kt`,
  `CompactComposerRow.kt`, `CompactDrawer.kt`, `Compact.kt` for the composition-local).
- Existing files receive only single-line call-site switches (`if (LocalCompactLayout.current) …
  else …`) and, where necessary, parameter plumbing. Keep each commit under ~400 changed lines
  outside new files to keep rebases surgical.

# Tasks

Work bottom-up; one commit per numbered task group, ktlint clean at every commit. Do not touch
files or screens outside this list. `AGENTS.md` rules apply (design tokens, 48dp targets, strings
via resources, no hardcoded colors).

## 0. Baseline

- [ ] 0.1 Verify the branch is `lp3` rebased on current `upstream/main`; record `git rev-parse --short HEAD` in the final report.
- [ ] 0.2 Build and install the unmodified APK to confirm the tooling loop on this machine (see design.md verification protocol).
- [ ] 0.3 Capture "before" history/chat/drawer/login screenshots at 1080x1240 for the final report pair.

## 1. Compact boundary

- [ ] 1.1 `ui/common/Compact.kt`: `LocalCompactLayout` composition-local (default `false`, threshold `screenHeightDp < 520`), plus a `@Composable fun rememberCompactLayout(): Boolean` helper using `LocalConfiguration`.
- [ ] 1.2 Provide it once at the app root (activity content or theme root); assert via a unit test or preview that both values render.
- [ ] 1.3 No screen may branch on device model, width, or build type. Grep-check before commit.

## 2. History — list-first

- [ ] 2.1 New `ui/sessions/components/CompactSessionsHeader.kt`: top-bar title/search/filter actions with an active-filter badge; one summary chip when a filter is active.
- [ ] 2.2 New `ui/sessions/components/SessionsFilterSheet.kt`: ModalBottomSheet reusing the existing section/source/project chip composables, the search field and the stats row + prune entry.
- [ ] 2.3 In `SessionsScreen.kt`, switch the header block on `LocalCompactLayout`; keep pager, paging, pinned/group headers, bulk selection, undo snackbar untouched.
- [ ] 2.4 Acceptance (measured on emulator screenshots): with no filters active, session rows occupy ≥60% of the viewport height; all filters reachable in ≤2 taps; clearing the summary chip restores the default list; empty/loading/error states still render.

## 3. Chat — composer diet

- [ ] 3.1 New `ui/chat/components/CompactComposerRow.kt`: single-row composer (field + attach + 48dp mic/send morph), 12dp horizontal / 8dp vertical padding, resting height ≈56dp; reuse the existing morph and send-availability logic.
- [ ] 3.2 In `ChatComposer.kt`, switch the composer subtree on `LocalCompactLayout`; keep attachment tray, queue chips, clarify/approvals, dictation and error banners working (they may add height when active).
- [ ] 3.3 Move model/effort into `ChatOverflowMenu` on compact (menu item shows current model name); remove the composer pill on compact only.
- [ ] 3.4 Replace the compact context-usage chip with a 2dp hairline under the app bar bound to the same fraction; tap opens the existing `ContextDetailSheet`.
- [ ] 3.5 Compact empty state: headline + subtitle only (no large decorative icon).
- [ ] 3.6 Acceptance (screenshots): empty composer ≤72dp tall; with keyboard open ≥3 conversation lines visible above the composer; send still works (do not send into the live session — use a scratch session or verify send-button enablement only); model sheet opens from the overflow; context sheet opens from the hairline.

## 4. Drawer — short list

- [ ] 4.1 New compact drawer content composable: curated rows (Chat, History, Cron, Settings) + collapsible groups (`Automation`, `Configure`, `Develop`) reusing `ScreenRegistry` destinations, icons and badges.
- [ ] 4.2 Switch the drawer content on `LocalCompactLayout` in the navigation host; selection highlighting, back handling and deep links unchanged.
- [ ] 4.3 Acceptance (screenshots): default drawer fits without scrolling; every screen reachable in ≤2 taps; expanded group state survives drawer close/reopen within the session.

## 5. Auth login fit

- [ ] 5.1 Apply navigation-bar insets and viewport-aware scrolling to the auth-login column (all configurations); compact vertical spacing per DESIGN.md small step.
- [ ] 5.2 Acceptance (screenshots): fresh install, sign-in button fully visible without scrolling at 1080x1240; the same screen unchanged at regular phone size.

## 6. Fork-only theme default

- [ ] 6.1 Separate commit: default preset id → existing AMOLED preset. No palette/token changes; document the fork-only intent in the commit body.
- [ ] 6.2 Acceptance: fresh install launches dark with pure-black background; preset switcher still lists every preset.

## 7. Wrap-up

- [ ] 7.1 ktlint + color guard + unit tests green; report the exact commands and output.
- [ ] 7.2 "After" screenshot set at 1080x1240 and a before/after measurement table appended as `evidence.md` in the change folder.
- [ ] 7.3 Update `openspec/roadmap.md` if scope shifted; leave `openspec/specs` untouched until acceptance is confirmed by the owner.

# Proposal

## Why

On the Light Phone III (411x472dp usable), upstream's Material 3 Expressive chrome consumes most
of the vertical budget before any content: the History screen spends 84% of the viewport on filter
rows, stats and search (one truncated session row visible); the empty chat composer spans 120dp
(25% of the screen) and with the keyboard open no conversation line remains readable; the auth
login's primary button sits entirely behind the navigation bar on first run; the 30+ destination
drawer needs five wheel-length scrolls per navigation. Nothing is broken on regular phones; the
light phone shape simply has no vertical room for stacked chrome.

Evidence and screenshots: `~/Projects/light/lp3-hermes-mobile-audit/REPORT.md` (local only).

## What Changes

- Add one runtime compact form-factor flag (`screenHeightDp < 520`) published as a
  composition-local, computed once at the root. Regular phones take every existing code path
  unchanged.
- **History**: on compact, replace the always-visible chrome (section tabs, source chips, project
  chips, "group by project", search field, stats row) with a single Filters action in the top bar
  opening a bottom sheet that hosts them; show at most one summary chip when a non-default filter
  is active. The session list owns the screen.
- **Chat**: on compact, reduce the composer to one row — input field plus a single 48dp
  mic/send morph and an attach action. Move the model/effort entry into the existing top-bar
  overflow and the context usage meter to a tappable hairline under the app bar opening the
  existing ContextDetailSheet. Trim empty-state art and composer padding.
- **Drawer**: on compact, present a curated short list (Chat, History, Cron, Settings) followed by
  collapsible section groups; every destination stays reachable in at most two taps.
- **Auth login**: content respects navigation-bar insets on all configurations; the primary
  action is never occluded on a 472dp viewport.
- **Theme**: the fork defaults to the existing AMOLED preset (fork-only preference commit).

## Capabilities

### New Capabilities

- `compact-form-factor`: A single runtime compact-mode boundary for short viewports, plus the
  compact variants of History, Chat composer, drawer and auth login that respect the existing
  design tokens, keep 48dp touch targets, and leave regular phones untouched.

### Modified Capabilities

None. The deltas live behind the compact flag; non-compact rendering, behavior, strings and
accessibility semantics are unchanged.

## Impact

- New files under `ui/sessions/components/`, `ui/chat/components/` and `ui/navigation/` (or the
  drawer's home) plus one composition-local provider near the theme root.
- Small call-site switches in `SessionsScreen.kt`, `ChatScreen.kt`/`ChatComposer.kt`, the drawer
  content, and `AuthLoginScreen.kt`.
- No new dependencies. No API, protocol or storage changes. No changes to chat send semantics.
- Risk: upstream rebases that touch the same call sites. Mitigated by additive composables and
  call-site-level switches only.

## Non-goals

- No Light SDK / LightOS tool integration; the app remains a normal Android app.
- No changes outside M1 screens; cron/models/settings/logs wait for M2.
- No upstream pull request in M1; upstreamability is preserved, not exercised.

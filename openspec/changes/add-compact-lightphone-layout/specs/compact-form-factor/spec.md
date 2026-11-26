# Compact Form Factor Spec Delta

## Purpose

Make Hermes Mobile usable on a 411x472dp viewport (Light Phone III) by collapsing stacked chrome
into single entry points, while every regular phone keeps the existing Material 3 Expressive
layout unchanged.

## ADDED Requirements

### Requirement: Runtime compact boundary

The app SHALL decide compact layout from viewport height alone (`screenHeightDp < 520`) through one
composition-local read at the root; it MUST NOT key compact layout off device model, width, or
build flavour, and regular-height devices MUST render the existing layouts unchanged.

#### Scenario: Light Phone III geometry

- **WHEN** the app runs on a 411x472dp viewport
- **THEN** every M1 screen renders its compact variant
- **AND** no pixel of an existing regular layout is substituted at any height ≥520dp

#### Scenario: Regular phone

- **WHEN** the app runs on a viewport at or above 520dp tall
- **THEN** no M1 compact composable is composed and behavior, strings and accessibility semantics are identical to upstream

### Requirement: History list-first on compact

On compact screens the History screen SHALL present the session list as the primary surface: no
always-visible section tabs, source chips, project chips, group-by-project control, search field or
stats row. Such controls MUST remain reachable through one Filters action and at most one summary
chip reflecting an active non-default filter, and clearing that chip MUST restore the default list.

#### Scenario: Default History

- **WHEN** the History screen opens on compact with no filter applied
- **THEN** session cards occupy at least 60% of the viewport height
- **AND** exactly one filter affordance is visible in the top bar

#### Scenario: Active filter and clearing

- **WHEN** the user applies a source or project filter through the sheet
- **THEN** one summary chip describes it and tapping it reopens the sheet while its trailing X clears the filter and restores the unfiltered list

#### Scenario: Paging and selection preserved

- **WHEN** the user scrolls, pages, pins, groups, selects in bulk, prunes or undoes a delete
- **THEN** those flows behave exactly as on regular phones

### Requirement: Single-row composer on compact

On compact screens the chat composer SHALL rest at approximately 56dp tall with only the text
field, an attach action and one 48dp mic/send action; the model/effort entry SHALL live in the
top-bar overflow, and context usage SHALL surface as a tappable hairline under the app bar opening
the existing detail sheet. Send availability semantics MUST be identical to the regular composer.

#### Scenario: Empty and typing states

- **WHEN** the compact chat screen is at rest with no text
- **THEN** the composer is at most 72dp tall and shows the mic affordance
- **AND** with text present the affordance becomes send with the same enable/disable rules as the regular composer

#### Scenario: Keyboard open

- **WHEN** the IME is open on the compact chat screen
- **THEN** at least three conversation lines remain visible above the composer

#### Scenario: Model and context entries

- **WHEN** the user needs to change model/effort or inspect context usage
- **THEN** the model sheet opens from the top-bar overflow and the context detail opens from the hairline, with the same data as the regular layout

### Requirement: Short drawer on compact

On compact screens the navigation drawer SHALL default to a curated short list (Chat, History,
Cron, Settings) plus collapsible groups for the remaining destinations; every destination MUST
remain reachable in at most two taps and the default drawer MUST fit without scrolling.

#### Scenario: Navigation reach

- **WHEN** the drawer opens on compact
- **THEN** the four curated rows and all group headers are visible without scrolling, and expanding a group reveals its destinations in place

#### Scenario: Selection state

- **WHEN** a destination is opened from a group
- **THEN** the drawer highlights it exactly as the regular drawer does and closing/reopening preserves the expanded group within the session

### Requirement: Auth login fit

The auth-login screen SHALL keep its primary action inside the safe area on every configuration,
scrolling its content when the viewport is shorter than the form.

#### Scenario: Fresh install on compact

- **WHEN** the app starts signed out at 1080x1240 with the keyboard dismissed
- **THEN** the sign-in action is fully visible and tappable without scrolling or being occluded by the navigation bar

### Requirement: Fork default theme

The fork SHALL launch with the existing AMOLED preset selected on first run; every preset MUST
remain switchable and no palette/token values may change.

#### Scenario: First launch

- **WHEN** the app starts with fresh settings
- **THEN** the UI renders on the AMOLED preset with a pure-black background
- **AND** the preset list in settings still offers all upstream presets

## REMOVED Requirements

None.

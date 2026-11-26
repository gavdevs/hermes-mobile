# Roadmap

Owns delivery sequencing for the Light Phone III (compact) adaptation of Hermes Mobile.
Milestone identity here is judged by real emulator evidence at 1080x1240 @ 420dpi and, at
milestone end, the real LP3.

## M1 — make the phone usable (this change: add-compact-lightphone-layout)

Chat composer, History list-first, drawer short list, auth-login fit, AMOLED default.
Acceptance numbers are in `changes/add-compact-lightphone-layout/specs/compact-form-factor`.

## M2 — the rest of the chrome (not yet specced)

Remaining screens audited heavy: cron, models, settings index, logs, profile/config detail.
Also: context-usage detail entry point parity between compact and regular layouts.

## M3 — real-phone polish (not yet specced)

Install on the LP3, verify with the hidden dev menu (ADB), tune IME behaviour with the
LightOS keyboard, decide whether to attempt an upstream PR for the compact mode.

## Standing constraints

- Rebase, never diverge: `git fetch upstream && git rebase upstream/main` before any work.
  Conflicts should stay confined to call sites because compact code lives in new files.
- Do not chase upstream feature work; every upstream merge must keep regular phones pixel-identical.
- The AMOLED default preset is fork-only (owner preference) and must not be proposed upstream.

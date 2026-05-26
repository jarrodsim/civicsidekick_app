# Civic Sidekick ⚡

**Your companion for civic engagement.**

Enter your ZIP code and instantly see every person representing you — from your Governor to your State Assembly member — plus track the bills shaping your future. No signup, no tracking, no server.

**Visit the official site:** [civicsidekick.app](https://civicsidekick.app)

---

## Features

- **Find Every Official You Elect** — Governor, U.S. Senators, U.S. Representatives, and state legislators, all in one place. Just type your ZIP.
- **Track Federal Legislation** — Browse current bills from GovTrack.us. Tap to track bills and follow their progress.
- **Full Bios & Photos** — Wikipedia photos and biographies automatically enrich every elected official's profile.
- **No Server. No Database. No Ads.** — Pure client-side app. Your ZIP code touches only public APIs. Zero data stored anywhere.

---

## How It Works

1. Enter your ZIP code
2. Civic Sidekick looks up your state and fetches your officials from **GovTrack.us** (federal) and **OpenStates** (state-level)
3. Wikipedia provides photos and biographical extracts
4. Browse and track federal legislation — all from one dashboard

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Vanilla JavaScript, CSS, HTML |
| Icons | [Lucide](https://lucide.dev) |
| Font | [Inter](https://rsms.me/inter/) |
| Federal Data | [GovTrack.us](https://www.govtrack.us) (free, no API key) |
| State Data | [OpenStates](https://openstates.org) |
| ZIP Lookup | [Zippopotam.us](https://zippopotam.us) |
| Photos & Bios | [Wikipedia API](https://www.mediawiki.org/wiki/API:Main_page) |
| Governor Data | Static lookup (all 50 states + DC) |

---

## Quick Start

This is a static site — no build step, no server, no dependencies.



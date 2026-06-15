# Civic Sidekick ⚡

**Your companion for civic engagement.**

Enter your ZIP code and instantly see every person representing you — from your Governor to your State Assembly member — with educational information about what each office does and how it affects your daily life. No signup, no tracking, no server.

**Visit the official site:** [civicsidekick.app](https://civicsidekick.app)

---

## Features

- **Find Every Official You Elect** — Governor, U.S. Senators, U.S. Representatives, and state legislators, all in one place. Just type your ZIP.
- **Learn What Each Office Does** — Educational tooltips explain the role of every position, how they're elected, and how they affect your life.
- **Full Bios & Photos** — Wikipedia photos and biographies automatically enrich every elected official's profile.
- **No Server. No Database. No Ads.** — Pure client-side app. Your ZIP code touches only public APIs. Zero data stored anywhere.

---

## How It Works

1. Enter your ZIP code
2. Civic Sidekick looks up your state and fetches your officials from **ProPublica Congress API** (federal), **OpenStates** (state-level), and static **Governor** data
3. Wikipedia provides photos and biographical extracts
4. The built-in **Election Education** module explains what each office does

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Vanilla JavaScript, CSS, HTML |
| Icons | [Lucide](https://lucide.dev) |
| Font | [Inter](https://rsms.me/inter/) |
| Federal Data | [ProPublica Congress API](https://propublica.org/datastore/) (free registration) |
| State Data | [OpenStates](https://openstates.org) |
| Local Data | [Google Civic Information API](https://developers.google.com/civic-information) |
| ZIP Lookup | [Zippopotam.us](https://zippopotam.us) |
| Photos & Bios | [Wikipedia API](https://www.mediawiki.org/wiki/API:Main_page) |
| Governor Data | Static lookup (all 50 states + DC) |

---

## Quick Start

This is a static site — no build step, no server, no dependencies.

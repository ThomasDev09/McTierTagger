# TierTagger — Fabric 1.21.4

A client-side Fabric mod that shows each player's tier tag above their head in-game, synced directly from the TierTestingBot MySQL database.

---

## How it works

When you join a world or server, the mod connects to your MySQL database (the same one your TierTestingBot uses), fetches every player's UUID and tier, and caches them. Every 60 seconds it refreshes automatically. The tag is rendered just above the player's name tag in their tier color.

Tag colors:
- HT1 — Red
- LT1 — Gold
- HT2 — Green
- LT2 — Aqua
- HT3 — Yellow
- LT3 — Light Purple
- HT4 — Blue
- LT4 — Dark Aqua
- HT5 — Dark Gray
- LT5 — Gray

---

## Requirements

- Minecraft 1.21.4
- Fabric Loader 0.16.0+
- Fabric API

---

## Installation

1. Build the mod with `./gradlew build` — the jar will appear in `build/libs/`
2. Drop the jar into your `.minecraft/mods` folder
3. Launch Minecraft once to generate the config file
4. Open `.minecraft/config/tiertagger.properties` and fill in your database details

---

## Config

The config file is created automatically at `.minecraft/config/tiertagger.properties` on first launch.

```properties
db_host=localhost
db_port=3306
db_name=tier_testing
db_user=root
db_password=your_password
show_own_tag=true
show_unranked=false
```

`show_own_tag` — whether to show the tag above your own head
`show_unranked` — whether to show a tag for unranked players (off by default)

---

## Building

You need JDK 21 and Gradle installed.

```
./gradlew build
```

The compiled mod jar will be at `build/libs/tiertagger-1.0.0.jar`.

---

## Notes

- This is a **client-side only** mod. It does not need to be installed on the server.
- The database connection is made from the client machine, so whoever uses the mod needs network access to the MySQL host.
- If you want to share this with your community, consider setting up a small read-only MySQL user with access only to the `players` and `tiers` tables.

# Lost City Terrain Fit

A NeoForge 1.21.1 world-generation mod for [Big Lost City](https://modrinth.com/mod/big-lost-city).
Works great with [William Wythers' Overhauled Overworld](https://modrinth.com/mod/wwoo).

**The problem:** Big Lost City's buildings stamp a flat pad of foreign ground onto the world. Vanilla
projects each building onto the heightmap at one point, so on a slope half the footprint floats and half
is buried. Its `beard_thin` terrain adaptation then carves every hill inside the footprint into a level
plane, and the template's air cuts sheer walls into hillsides. In dramatic terrain like WWOO's, the ruins
look pasted on.

**The fix:**
- **Seating.** The pad drops to the footprint's own floor: about 70% of the footprint sits at or under
  the natural ground, and the seabed is used when the building is under water.
- **No carving.** Terrain adaptation is off for Big Lost City's buildings. A fill-only skirt draws the
  ground up beneath the pad instead, and nothing above the pad is removed.
- **Ground runs through the ruins.** The pad's natural blocks give way to the biome's own surface.
  Hills climb over the outer rooms, the sea floods buildings that stand in it, and stone or dirt
  footings prop up any pad left hanging on the downhill side.
- **All-biome buildings (optional).** Copies of the big buildings (skyscrapers, power plant, houses,
  store, warehouse, ferris wheel) can start in every overworld biome, oceans included. Big Lost City
  only lets them start in plains.

## Install

Drop the jar in `mods/` with Big Lost City `1.0.1` or newer. World generation runs on the server, so a
dedicated server needs this mod and clients joining it do not. Single-player needs it in the client's
`mods/` folder.

Changes only apply to newly generated chunks. Terrain you have already explored is left alone.

## Configuration

`config/lostcityterrainfit-common.toml`:

| Key | Default | What it does |
|---|---|---|
| `terrainFit.enabled` | `true` | Turns seating, no-carve, the skirt and ground-through-ruins on or off. Off means buildings generate as Big Lost City ships them. |
| `allBiomeBuildings.enabled` | `true` | Turns the all-biome building copies on or off. |
| `allBiomeBuildings.chance` | `1.0` | Share of the copies' placement attempts that are kept, from `0.0` to `1.0`. Lower it to thin them out. |

Datapack overrides:
- **Spacing:** `data/lostcityterrainfit/worldgen/structure_set/all_biome_buildings.json` (`random_spread`, spacing 20, separation 8 — one attempt per 320×320 blocks).
- **Biomes:** tag `#lostcityterrainfit:all_biome_buildings` (defaults to `#minecraft:is_overworld`).

## Modpack authors

You're free to include this mod; see [LICENSE](LICENSE) (PolyForm Shield 1.0.0). Big Lost City is
All Rights Reserved and is not bundled, so declare it as a dependency in your pack.

## Building

```bash
./gradlew build               # jar -> build/libs/lostcityterrainfit-neoforge-<version>.jar
./gradlew test                # pure-logic unit tests
./gradlew runClient           # dev client with Big Lost City
./gradlew runClient -Pwwoo    # ... plus WWOO
```

## Origin

Extracted from [Dungeon Train](https://github.com/bh679/dungeon-train-mc), whose Lost City stretch runs
through WWOO terrain. Dungeon Train bundles this mod and keeps its own placement rules on top.

package games.brennan.lostcityterrainfit;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * {@code config/lostcityterrainfit-common.toml}. Read on world-gen threads; a value read before the file has
 * loaded answers its default.
 */
public final class LostCityTerrainFitConfig {

    private LostCityTerrainFitConfig() {}

    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue TERRAIN_FIT;
    private static final ModConfigSpec.BooleanValue ALL_BIOME_ENABLED;
    private static final ModConfigSpec.DoubleValue ALL_BIOME_CHANCE;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("terrainFit");
        TERRAIN_FIT = b.comment(
                        "Seat Big Lost City's buildings in the world's own terrain: pad dropped to the footprint's floor,",
                        "no terrain carving, ground drawn up beneath them, hills and water running through the ruins.",
                        "Off: the buildings generate exactly as Big Lost City ships them. Affects newly generated chunks only.")
                .define("enabled", true);
        b.pop();
        b.push("allBiomeBuildings");
        ALL_BIOME_ENABLED = b.comment(
                        "Let copies of Big Lost City's big buildings (skyscrapers, power plant, houses, store, warehouse,",
                        "ferris wheel) start in every overworld biome, oceans included. Spacing and the biome list are",
                        "datapack-overridable: structure_set lostcityterrainfit:all_biome_buildings and biome tag",
                        "#lostcityterrainfit:all_biome_buildings.")
                .define("enabled", true);
        ALL_BIOME_CHANCE = b.comment(
                        "Share of the structure set's placement attempts that are kept (0.0 = none, 1.0 = all).",
                        "Lower it to thin the all-biome buildings without editing the datapack.")
                .defineInRange("chance", 1.0, 0.0, 1.0);
        b.pop();
        SPEC = b.build();
    }

    public static boolean terrainFit() {
        return read(TERRAIN_FIT, true);
    }

    public static boolean allBiomeEnabled() {
        return read(ALL_BIOME_ENABLED, true);
    }

    public static double allBiomeChance() {
        return read(ALL_BIOME_CHANCE, 1.0);
    }

    private static <T> T read(ModConfigSpec.ConfigValue<T> value, T fallback) {
        try {
            return SPEC.isLoaded() ? value.get() : fallback;
        } catch (IllegalStateException e) {
            return fallback;
        }
    }
}

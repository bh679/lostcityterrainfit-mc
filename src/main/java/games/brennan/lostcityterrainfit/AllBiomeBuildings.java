package games.brennan.lostcityterrainfit;

import net.minecraft.resources.ResourceLocation;

/**
 * The all-biome copies of Big Lost City's big buildings: {@code lostcityterrainfit:all_biome/<name>}.
 *
 * <p>Big Lost City lets its big buildings start almost only in plains. Each copy is the same jigsaw — the same
 * start pool, so the same templates — with the biome list opened to {@code #lostcityterrainfit:all_biome_buildings}
 * (every overworld biome by default). They are placed by one sparse structure set; {@link #keep} is the config's
 * say over which of that set's starts survive.</p>
 */
public final class AllBiomeBuildings {

    private AllBiomeBuildings() {}

    /** Path prefix of the copies inside this mod's namespace. */
    public static final String PREFIX = "all_biome/";

    /** Salt mixed into the keep roll, so it is independent of the structure set's own placement salt. */
    private static final long SALT = 0x4C4354465F414231L;

    /** Whether {@code id} is one of this mod's all-biome copies. */
    public static boolean isCopy(ResourceLocation id) {
        return id != null && LostCityTerrainFit.MOD_ID.equals(id.getNamespace()) && id.getPath().startsWith(PREFIX);
    }

    /**
     * Whether a copy's start in chunk {@code (chunkX, chunkZ)} of the world with {@code seed} is kept: the copies are
     * {@code enabled} and the chunk's roll falls under {@code chance}. Deterministic per world and chunk.
     */
    public static boolean keep(boolean enabled, double chance, long seed, int chunkX, int chunkZ) {
        if (!enabled || chance <= 0.0) return false;
        if (chance >= 1.0) return true;
        return roll(seed, chunkX, chunkZ) < chance;
    }

    /** A uniform value in {@code [0, 1)} for this world and chunk. */
    static double roll(long seed, int chunkX, int chunkZ) {
        long h = seed ^ SALT;
        h = mix(h + chunkX * 0x9E3779B97F4A7C15L);
        h = mix(h + chunkZ * 0xC2B2AE3D27D4EB4FL);
        return (h >>> 11) * 0x1.0p-53;
    }

    private static long mix(long z) {
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}

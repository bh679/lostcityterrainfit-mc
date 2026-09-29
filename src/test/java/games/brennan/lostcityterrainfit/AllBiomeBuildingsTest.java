package games.brennan.lostcityterrainfit;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** {@link AllBiomeBuildings}: which ids are copies, and the config's keep roll. */
final class AllBiomeBuildingsTest {

    @Test
    void recognisesOnlyThisModsCopies() {
        assertTrue(AllBiomeBuildings.isCopy(ResourceLocation.fromNamespaceAndPath("lostcityterrainfit", "all_biome/warehouse")));
        assertFalse(AllBiomeBuildings.isCopy(ResourceLocation.fromNamespaceAndPath("big_lost_city", "warehouse")));
        assertFalse(AllBiomeBuildings.isCopy(ResourceLocation.fromNamespaceAndPath("dungeontrain", "lost_city/warehouse")));
        assertFalse(AllBiomeBuildings.isCopy(ResourceLocation.fromNamespaceAndPath("lostcityterrainfit", "other")));
        assertFalse(AllBiomeBuildings.isCopy(null));
    }

    @Test
    void disabledOrZeroKeepsNothingAndFullKeepsAll() {
        for (int x = -20; x < 20; x++) {
            assertFalse(AllBiomeBuildings.keep(false, 1.0, 42L, x, 3));
            assertFalse(AllBiomeBuildings.keep(true, 0.0, 42L, x, 3));
            assertTrue(AllBiomeBuildings.keep(true, 1.0, 42L, x, 3));
        }
    }

    @Test
    void chanceKeepsItsShareDeterministically() {
        int kept = 0;
        int n = 0;
        for (int x = -100; x < 100; x++) {
            for (int z = -100; z < 100; z++) {
                boolean k = AllBiomeBuildings.keep(true, 0.25, 1234L, x, z);
                assertEquals(k, AllBiomeBuildings.keep(true, 0.25, 1234L, x, z));
                if (k) kept++;
                n++;
            }
        }
        assertEquals(0.25, kept / (double) n, 0.01);
    }

    @Test
    void rollDependsOnSeed() {
        int same = 0;
        for (int x = 0; x < 200; x++) {
            if (AllBiomeBuildings.keep(true, 0.5, 1L, x, 0) == AllBiomeBuildings.keep(true, 0.5, 2L, x, 0)) same++;
        }
        assertTrue(same < 150, "seeds should roll independently, matched " + same);
    }
}

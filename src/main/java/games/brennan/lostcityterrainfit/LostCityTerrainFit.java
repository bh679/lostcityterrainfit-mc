package games.brennan.lostcityterrainfit;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * Lost City Terrain Fit: Big Lost City's buildings sit in the world's own terrain, and — optionally — its big
 * buildings start in every overworld biome.
 *
 * <p>Everything happens during world generation, by mixin ({@code lostcityterrainfit.mixins.json}) and data
 * ({@code data/lostcityterrainfit/worldgen}); the constructor only registers the config.</p>
 */
@Mod(LostCityTerrainFit.MOD_ID)
public final class LostCityTerrainFit {

    public static final String MOD_ID = "lostcityterrainfit";

    public LostCityTerrainFit(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, LostCityTerrainFitConfig.SPEC);
    }
}

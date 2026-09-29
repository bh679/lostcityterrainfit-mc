package games.brennan.lostcityterrainfit.mixin;

import games.brennan.lostcityterrainfit.AllBiomeBuildings;
import games.brennan.lostcityterrainfit.LostCitySeating;
import games.brennan.lostcityterrainfit.LostCityTerrainFitConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Where a Big Lost City start is decided: the all-biome copies' config veto, and the seating.
 *
 * <p><b>Veto</b> at {@code findValidGenerationPoint} {@code HEAD}: a copy the config turns off
 * ({@link AllBiomeBuildings#keep}) finds no generation point, before any template is loaded (a jigsaw assembles
 * every piece while finding its point). Both chunk generation and {@code /locate}'s start check go through this
 * method, so a refused copy is neither built nor located.</p>
 *
 * <p><b>Seat</b> at {@code RETURN}: {@link LostCitySeating#seat} drops the built start's pad to its footprint's
 * floor. {@code priority = 900} so it runs ahead of other mods' {@code RETURN} checks on the start (a default
 * priority is 1000) — they then read the seated bounding box.</p>
 */
@Mixin(value = Structure.class, priority = 900)
public abstract class StructureSeatMixin {

    @Inject(method = "findValidGenerationPoint", at = @At("HEAD"), cancellable = true)
    private void lostcityterrainfit$vetoAllBiomeCopy(Structure.GenerationContext context,
                                                      CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if (!((Object) this instanceof JigsawStructure)) return;
        ResourceLocation id = context.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey((Structure) (Object) this);
        if (!AllBiomeBuildings.isCopy(id)) return;
        if (!AllBiomeBuildings.keep(LostCityTerrainFitConfig.allBiomeEnabled(), LostCityTerrainFitConfig.allBiomeChance(),
                context.seed(), context.chunkPos().x, context.chunkPos().z)) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(method = "generate", at = @At("RETURN"))
    private void lostcityterrainfit$seat(RegistryAccess registryAccess, ChunkGenerator chunkGenerator,
                                         BiomeSource biomeSource, RandomState randomState,
                                         StructureTemplateManager structureTemplateManager, long seed,
                                         ChunkPos chunkPos, int references, LevelHeightAccessor heightAccessor,
                                         Predicate<Holder<Biome>> validBiome,
                                         CallbackInfoReturnable<StructureStart> cir) {
        StructureStart start = cir.getReturnValue();
        if (start == null || !start.isValid()) return;
        if (!LostCityTerrainFitConfig.terrainFit()) return;
        if (!LostCitySeating.isLostCity((Structure) (Object) this)) return;
        LostCitySeating.seat(start, chunkGenerator, heightAccessor, randomState);
    }
}

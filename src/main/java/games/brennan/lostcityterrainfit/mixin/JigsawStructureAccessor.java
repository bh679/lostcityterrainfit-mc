package games.brennan.lostcityterrainfit.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Reads a jigsaw structure's start pool — {@link JigsawStructure} is shared with villages, outposts and trial
 * chambers, so the start pool's namespace is what says a structure is one of Big Lost City's buildings.
 */
@Mixin(JigsawStructure.class)
public interface JigsawStructureAccessor {

    @Accessor("startPool")
    Holder<StructureTemplatePool> lostcityterrainfit$startPool();
}

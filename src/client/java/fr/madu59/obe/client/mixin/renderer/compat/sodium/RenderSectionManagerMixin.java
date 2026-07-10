package fr.madu59.obe.client.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.data.TranslucentData;

@Mixin(value = RenderSectionManager.class, remap = false)
public class RenderSectionManagerMixin {
    @WrapOperation(method = "processChunkBuildResults", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSection;getTranslucentData()Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/data/TranslucentData;"), require = 0) // Before Sodium 0.9.0
    private TranslucentData obe$wrapProcessChunkBuildResults(RenderSection section, Operation<TranslucentData> original) {
        ChunkTaskHolder.executeTasks(section.getPosition());
        return original.call(section);
    }

    @WrapOperation(method = "processChunkBuildResults", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSection;retrievePendingBuildOutput()Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"), require = 0) // Sodium 0.9.0+
    private ChunkBuildOutput obe$wrapProcessChunkBuildResultsBis(RenderSection section, Operation<ChunkBuildOutput> original) {
        ChunkTaskHolder.executeTasks(section.getPosition());
        return original.call(section);
    }
}

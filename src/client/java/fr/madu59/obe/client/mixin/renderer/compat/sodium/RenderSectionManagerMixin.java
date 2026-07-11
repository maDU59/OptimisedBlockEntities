package fr.madu59.obe.client.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;

@Pseudo
@Mixin(value = RenderSectionManager.class, remap = false)
public class RenderSectionManagerMixin {
    @WrapOperation(method = "processChunkBuildResults", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/RenderSection;retrievePendingBuildOutput()Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;"))
    private ChunkBuildOutput obe$wrapProcessChunkBuildResults(RenderSection section, Operation<ChunkBuildOutput> original) {
        ChunkTaskHolder.executeTasks(section.getPosition());
        return original.call(section);
    }
}

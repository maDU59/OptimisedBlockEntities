package fr.madu59.obe.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.CompiledChunk;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

@Mixin(value = LevelRenderer.class, priority = 1001) //We don't want to prevent sodium from injecting here
public class LevelRendererMixin {

    @WrapOperation(
        method = "renderLevel",
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$CompiledChunk;getRenderableBlockEntities()Ljava/util/List;" 
        ),
        require = 0
    )
    private <T extends CompiledChunk> List<BlockEntity> obe$redirectGetRenderableBlockEntities(T sectionMeshInstance, Operation<List<BlockEntity>> originalCall) {
        List<BlockEntity> original = originalCall.call(sectionMeshInstance);

        if (original == null || original.isEmpty())  return original;

        original.removeIf(be -> {
            return (be instanceof BlockEntityExt ext && ext.isEnabled() && (!RenderModeManager.shouldRenderEntityFast(ext) || ext.shouldSkipBeRendering()));
        });

        return original;
    }
}

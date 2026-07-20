package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fr.madu59.obe.client.renderer.blockentity.SpecialBlockEntityRenderingManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.client.renderer.LevelRenderer;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
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

        for (int i = original.size() - 1; i >= 0; i--) {
            if (original.get(i) instanceof BlockEntityExt ext){
                if(ext.isEnabled() && (!RenderModeManager.shouldRenderEntityFast(ext) || ext.shouldSkipRendering() || SpecialBlockEntityRenderingManager.shouldSkipRendering((BlockEntity)ext))){
                    original.remove(i);
                }
            }
        }

        return original;
    }
}

package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @WrapOperation(
        method = "Lnet/minecraft/client/renderer/LevelRenderer;extractVisibleBlockEntities(Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/culling/Frustum;)V",
        at = @At(
            value = "INVOKE", 
            target = "getRenderableBlockEntities" 
        )
    )
    private <T extends SectionMesh> List<BlockEntity> obe$redirectGetRenderableBlockEntities(T sectionMeshInstance, Operation<List<BlockEntity>> originalCall) {
        List<BlockEntity> original = originalCall.call(sectionMeshInstance);

        if (original == null || original.isEmpty())  return original;

        original.removeIf(be -> {
            return (be instanceof BlockEntityExt ext && ext.isEnabled() && (!RenderModeManager.shouldRenderEntityFast(ext) || ext.shouldSkipBeRendering()));
        });

        return original;
    }
}

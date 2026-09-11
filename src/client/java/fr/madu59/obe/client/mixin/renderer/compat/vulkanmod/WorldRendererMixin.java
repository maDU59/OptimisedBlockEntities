package fr.madu59.obe.client.mixin.renderer.compat.vulkanmod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fr.madu59.obe.client.renderer.blockentity.SpecialBlockEntityRenderingManager;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.vulkanmod.render.chunk.WorldRenderer;

@Pseudo
@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {

    @WrapOperation(
            method = "renderBlockEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;tryExtractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;FLnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;",
                    ordinal = 0
            )
    )
    private BlockEntityRenderState obe$preventUselessExtraction(BlockEntityRenderDispatcher instance, BlockEntity be, float partialTick,
                                                                  ModelFeatureRenderer.CrumblingOverlay overlay,
                                                                  Operation<BlockEntityRenderState> original) {
        if (SpecialBlockEntityRenderingManager.shouldSkipRendering(be)) {
            return null;
        }
        return original.call(instance, be, partialTick, overlay);
    }
}
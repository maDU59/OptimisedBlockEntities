package fr.madu59.obe.client.mixin.renderer.compat.vulkanmod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.SpecialBlockEntityRenderingManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.vulkanmod.render.chunk.WorldRenderer;

@Pseudo
@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {

    @WrapOperation(
            method = "renderBlockEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLnet/minecraft/client/renderer/pose/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
                    ordinal = 0
            )
    )
    private void obe$preventUselessExtraction(BlockEntityRenderDispatcher instance, BlockEntity be, float partialTick,
                                                                  PoseStack stack, MultiBufferSource buffer,
                                                                  Operation<Void> original) {
        if (SpecialBlockEntityRenderingManager.shouldSkipRendering(be)) {
            return;
        }
        original.call(instance, be, partialTick, stack, buffer);
    }
}
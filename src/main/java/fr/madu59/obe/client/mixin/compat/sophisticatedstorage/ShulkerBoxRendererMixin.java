package fr.madu59.obe.client.mixin.compat.sophisticatedstorage;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedRenderContext;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedStorageDiagnostics;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedDynamicRenderTracking;
import fr.madu59.obe.client.renderer.blockentity.DynamicBlockEntityRenderManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.client.render.ShulkerBoxRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Guards the public BER entry and the layout-independent tinted shell helper. */
@Mixin(ShulkerBoxRenderer.class)
public abstract class ShulkerBoxRendererMixin {
    @WrapMethod(method = "render(Lnet/p3pp3rf1y/sophisticatedstorage/block/ShulkerBoxBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V")
    private void obe$withShulkerContext(ShulkerBoxBlockEntity shulker, float partialTick, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight, int packedOverlay, Operation<Void> original) {
        if (!DynamicBlockEntityRenderManager.shouldRender(shulker)) {
            return;
        }
        SophisticatedDynamicRenderTracking.shulkerRendererExecuting();
        SophisticatedRenderContext.enter(shulker);
        try {
            original.call(shulker, partialTick, poseStack, buffers, packedLight, packedOverlay);
            SophisticatedStorageDiagnostics.completedDynamicRenderer();
        } finally {
            SophisticatedRenderContext.exit();
        }
    }

    @Inject(method = "renderTintedModel", at = @At("HEAD"), cancellable = true)
    private void obe$suppressTinted(PoseStack poseStack, MultiBufferSource buffers, int light,
            int overlay, int color, Material material, CallbackInfo ci) {
        if (SophisticatedRenderContext.suppressStaticShell()) {
            ci.cancel();
        }
    }
}

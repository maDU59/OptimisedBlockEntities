package fr.madu59.obe.client.mixin.compat.sophisticatedstorage;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedRenderContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.p3pp3rf1y.sophisticatedstorage.client.StorageTextureManager.ChestMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Cancels only static shell calls; dynamic overlays remain in the original BER. */
@Mixin(targets = "net.p3pp3rf1y.sophisticatedstorage.client.render.ChestRenderer$ChestSubRenderer")
public abstract class ChestSubRendererMixin {
    @Inject(method = "renderBottomAndLid(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FIILnet/p3pp3rf1y/sophisticatedstorage/client/StorageTextureManager$ChestMaterial;)V", at = @At("HEAD"), cancellable = true)
    private void obe$suppressBase(PoseStack poseStack, MultiBufferSource buffers, float openness,
            int light, int overlay, ChestMaterial material, CallbackInfo ci) {
        cancelStaticShell(ci);
    }

    @Inject(method = "renderBottomAndLidWithTint(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FIIILnet/p3pp3rf1y/sophisticatedstorage/client/StorageTextureManager$ChestMaterial;)V", at = @At("HEAD"), cancellable = true)
    private void obe$suppressTint(PoseStack poseStack, MultiBufferSource buffers, float openness,
            int light, int overlay, int color, ChestMaterial material, CallbackInfo ci) {
        cancelStaticShell(ci);
    }

    @Inject(method = "renderChestLock", at = @At("HEAD"), cancellable = true)
    private void obe$suppressLatch(PoseStack poseStack, MultiBufferSource buffers, float openness,
            int light, int overlay, CallbackInfo ci) {
        cancelStaticShell(ci);
    }

    @Inject(method = "renderTier", at = @At("HEAD"), cancellable = true)
    private void obe$suppressTier(PoseStack poseStack, MultiBufferSource buffers, float openness,
            int light, int overlay, CallbackInfo ci) {
        cancelStaticShell(ci);
    }

    private static void cancelStaticShell(CallbackInfo ci) {
        if (SophisticatedRenderContext.suppressStaticShell()) {
            ci.cancel();
        }
    }
}

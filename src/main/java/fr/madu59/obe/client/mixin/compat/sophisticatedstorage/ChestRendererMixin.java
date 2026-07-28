package fr.madu59.obe.client.mixin.compat.sophisticatedstorage;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedRenderContext;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedStorageDiagnostics;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedDynamicRenderTracking;
import fr.madu59.obe.client.renderer.blockentity.DynamicBlockEntityRenderManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.client.render.ChestRenderer;
import org.spongepowered.asm.mixin.Mixin;

/** Makes the current chest available to the guarded sub-renderer injections. */
@Mixin(ChestRenderer.class)
public abstract class ChestRendererMixin {
    @WrapMethod(method = "render(Lnet/p3pp3rf1y/sophisticatedstorage/block/ChestBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V")
    private void obe$withChestContext(ChestBlockEntity chest, float partialTick, PoseStack poseStack,
            MultiBufferSource buffers, int packedLight, int packedOverlay, Operation<Void> original) {
        if (!DynamicBlockEntityRenderManager.shouldRender(chest)) {
            return;
        }
        SophisticatedDynamicRenderTracking.chestRendererExecuting();
        SophisticatedRenderContext.enter(chest);
        try {
            original.call(chest, partialTick, poseStack, buffers, packedLight, packedOverlay);
            SophisticatedStorageDiagnostics.completedDynamicRenderer();
        } finally {
            SophisticatedRenderContext.exit();
        }
    }
}

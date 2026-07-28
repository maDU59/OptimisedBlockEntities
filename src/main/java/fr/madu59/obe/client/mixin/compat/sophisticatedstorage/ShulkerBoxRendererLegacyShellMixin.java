package fr.madu59.obe.client.mixin.compat.sophisticatedstorage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedRenderContext;
import net.minecraft.client.model.ShulkerModel;
import net.p3pp3rf1y.sophisticatedstorage.client.render.ShulkerBoxRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Static-shell interception for Storage 1.5.70--1.5.73's six-argument body. */
@Mixin(ShulkerBoxRenderer.class)
public abstract class ShulkerBoxRendererLegacyShellMixin {
    @WrapOperation(
            method = "render(Lnet/p3pp3rf1y/sophisticatedstorage/block/ShulkerBoxBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ShulkerModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V")
    )
    private void obe$suppressUntintedAndTier(ShulkerModel<?> model, PoseStack poseStack,
            VertexConsumer consumer, int light, int overlay, Operation<Void> original) {
        if (!SophisticatedRenderContext.suppressStaticShell()) {
            original.call(model, poseStack, consumer, light, overlay);
        }
    }
}

package fr.madu59.obe.client.mixin.blockentity.renderers.sign;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;

@Mixin(AbstractSignRenderer.class)
public abstract class AbstractSignRendererMixin {
    @Inject(method = "submitSignText", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSignText(CallbackInfo ci, @Local PoseStack poseStack){
        if(ModCompat.isShadowPass() || !obe$isFacingCamera(poseStack)) ci.cancel();
    }

    @Unique
    private static boolean obe$isFacingCamera(PoseStack poseStack) {
        Matrix4f pose = poseStack.last().pose();

        PoseStack.Pose topPose = poseStack.last();
        Matrix3f normalMatrix = topPose.normal();
        Vector3f forwardVector = new Vector3f();
        normalMatrix.getColumn(2, forwardVector);

        Vector3f pos = pose.transformPosition(0, 0, 0, new Vector3f());

        return forwardVector.dot(pos) < 0.0f;
    }
}

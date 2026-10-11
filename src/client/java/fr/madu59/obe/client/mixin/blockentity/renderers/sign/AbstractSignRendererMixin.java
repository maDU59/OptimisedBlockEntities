package fr.madu59.obe.client.mixin.blockentity.renderers.sign;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;

@Mixin(AbstractSignRenderer.class)
public abstract class AbstractSignRendererMixin {
    @Inject(method = "submitSignText", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSignText(CallbackInfo ci, @Local PoseStack poseStack){
        if(SettingsManager.SIGN_TEXT_CULLING.getValue() && (ModCompat.isShadowPass() || !obe$isFacingCamera(poseStack))) ci.cancel();
    }

    @Unique
    private static boolean obe$isFacingCamera(PoseStack poseStack) {
        PoseStack.Pose topPose = poseStack.last();
        Matrix4f pose = topPose.pose();
        Matrix3f normal = topPose.normal();
        return Math.fma(normal.m20(), pose.m30(), Math.fma(normal.m21(), pose.m31(), normal.m22() * pose.m32())) < 0.0f;
    }
}

package fr.madu59.obe.client.mixin.blockentity.renderers.sign;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

@Mixin(SignRenderer.class)
public abstract class AbstractSignRendererMixin {

    @Shadow
    public abstract void renderSignText(BlockPos blockPos, SignText signText, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int k, boolean bl);

    @Shadow
    public abstract void translateSignText(PoseStack poseStack, boolean bl, Vec3 vec3);

    @Shadow
    public abstract Vec3 getTextOffset();

    @Shadow
    public abstract void translateSign(PoseStack poseStack, float f, BlockState blockState);

    @Inject(method = "renderSignText", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSignText(BlockPos blockPos, SignText signText, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int k, boolean bl, CallbackInfo ci){
        poseStack.pushPose();
        this.translateSignText(poseStack, bl, this.getTextOffset());
        if(SettingsManager.SIGN_TEXT_CULLING.getValue() && (ModCompat.isShadowPass() || !obe$isFacingCamera(poseStack))) {
            ci.cancel();
        }
        poseStack.popPose();
    }

    @WrapWithCondition(
            method = "renderSignWithText",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/SignRenderer;renderSign(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/level/block/state/properties/WoodType;Lnet/minecraft/client/model/Model;)V"))
    private boolean obe$renderSignModel(SignRenderer self, PoseStack ps, MultiBufferSource buf, int light, int overlay, WoodType type, Model model, @Local(argsOnly = true) SignBlockEntity signBlockEntity) {
        return RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_SIGNS.getValue(), signBlockEntity);
    }

    @Unique
    private static boolean obe$isFacingCamera(PoseStack poseStack) {
        PoseStack.Pose topPose = poseStack.last();
        Matrix4f pose = topPose.pose();
        Matrix3f normal = topPose.normal();
        return Math.fma(normal.m20(), pose.m30(), Math.fma(normal.m21(), pose.m31(), normal.m22() * pose.m32())) < 0.0f;
    }
}

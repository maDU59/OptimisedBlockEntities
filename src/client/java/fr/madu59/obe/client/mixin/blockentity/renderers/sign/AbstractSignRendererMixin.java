package fr.madu59.obe.client.mixin.blockentity.renderers.sign;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractSignRenderer.class)
public abstract class AbstractSignRendererMixin<S extends SignRenderState> {

    @Shadow
    public abstract void submitSignText(SignRenderState signRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, boolean bl);

    @Shadow
    public abstract void translateSignText(PoseStack poseStack, boolean bl, Vec3 vec3);

    @Shadow
    public abstract Vec3 getTextOffset();

    @Shadow
    public abstract void translateSign(PoseStack poseStack, float f, BlockState blockState);

    @Inject(method = "submitSignText", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSignText(SignRenderState signRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, boolean bl, CallbackInfo ci){
        poseStack.pushPose();
        this.translateSignText(poseStack, bl, this.getTextOffset());
        if(ModCompat.isShadowPass() || !obe$isFacingCamera(poseStack)) {
            poseStack.popPose();
            ci.cancel();
        }
        else poseStack.popPose();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void obe$cancelExtract(CallbackInfo ci, @Local S state, @Local SignBlockEntity be){
        ((BlockEntityRenderStateExt)state).blockEntity(be);
    }

    @Inject(method = "submitSignWithText", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(SignRenderState signRenderState, PoseStack poseStack, BlockState blockState, SignBlock signBlock, WoodType woodType, Model.Simple simple, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, SubmitNodeCollector submitNodeCollector, CallbackInfo ci){
        if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_SIGNS.getValue(), signRenderState)){
            ci.cancel();

            poseStack.pushPose();
            this.translateSign(poseStack, -signBlock.getYRotationDegrees(blockState), blockState);
            // this.submitSign(poseStack, signRenderState.lightCoords, woodType, simple, crumblingOverlay, submitNodeCollector);
            this.submitSignText(signRenderState, poseStack, submitNodeCollector, true);
            this.submitSignText(signRenderState, poseStack, submitNodeCollector, false);
            poseStack.popPose();
        }
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

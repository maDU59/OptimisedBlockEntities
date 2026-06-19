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
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

@Mixin(AbstractSignRenderer.class)
public abstract class AbstractSignRendererMixin<S extends SignRenderState> {

    @Shadow
    public abstract void submitSignText(final S state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final SignText signText);

    @Inject(method = "submitSignText", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSignText(CallbackInfo ci, @Local PoseStack poseStack){
        if(ModCompat.isShadowPass() || !obe$isFacingCamera(poseStack)) ci.cancel();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void obe$cancelExtract(CallbackInfo ci, @Local S state, @Local SignBlockEntity be){
        ((BlockEntityRenderStateExt)state).blockEntity(be);
    }

    @Inject(method = "submitSignWithText", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(final S state, final PoseStack poseStack, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final SubmitNodeCollector submitNodeCollector, CallbackInfo ci){
        if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_SIGNS.getValue(), state)){
            ci.cancel();

            poseStack.pushPose();
            poseStack.mulPose(state.transformations.body());
            // this.submitSign(poseStack, state.lightCoords, state.woodType, bodyModel, breakProgress, submitNodeCollector);
            poseStack.popPose();
            if (state.frontText != null) {
                poseStack.pushPose();
                poseStack.mulPose(state.transformations.frontText());
                this.submitSignText(state, poseStack, submitNodeCollector, state.frontText);
                poseStack.popPose();
            }

            if (state.backText != null) {
                poseStack.pushPose();
                poseStack.mulPose(state.transformations.backText());
                this.submitSignText(state, poseStack, submitNodeCollector, state.backText);
                poseStack.popPose();
            }
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

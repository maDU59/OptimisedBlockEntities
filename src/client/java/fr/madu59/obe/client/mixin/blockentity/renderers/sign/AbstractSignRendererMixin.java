package fr.madu59.obe.client.mixin.blockentity.renderers.sign;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

@Mixin(AbstractSignRenderer.class)
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
        if((ModCompat.isShadowPass() || !obe$isFacingCamera(poseStack)) && SettingsManager.SIGN_TEXT_CULLING.getValue()) {
            poseStack.popPose();
            ci.cancel();
        }
        else poseStack.popPose();
    }

    @Inject(method = "renderSignWithText", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(SignBlockEntity signBlockEntity, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BlockState blockState, SignBlock signBlock, WoodType woodType, Model model, CallbackInfo ci){
        if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_SIGNS.getValue(), signBlockEntity)){
            ci.cancel();

            poseStack.pushPose();
            this.translateSign(poseStack, -signBlock.getYRotationDegrees(blockState), blockState);
            //   this.renderSign(poseStack, multiBufferSource, i, j, woodType, model);
            this.renderSignText(signBlockEntity.getBlockPos(), signBlockEntity.getFrontText(), poseStack, multiBufferSource, i, signBlockEntity.getTextLineHeight(), signBlockEntity.getMaxTextLineWidth(), true);
            this.renderSignText(signBlockEntity.getBlockPos(), signBlockEntity.getBackText(), poseStack, multiBufferSource, i, signBlockEntity.getTextLineHeight(), signBlockEntity.getMaxTextLineWidth(), false);
            poseStack.popPose();
        }
        RenderModeManager.updateOnRender(signBlockEntity);
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

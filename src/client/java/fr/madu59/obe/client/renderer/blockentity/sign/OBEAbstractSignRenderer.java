package fr.madu59.obe.client.renderer.blockentity.sign;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;

public abstract class OBEAbstractSignRenderer<S extends SignRenderState> extends AbstractSignRenderer<S> {

    public OBEAbstractSignRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submitSignWithText(final S state, final PoseStack poseStack, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final SubmitNodeCollector submitNodeCollector) {

        if(RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_SIGNS.getValue(), state)){
            Model.Simple bodyModel = this.getSignModel(state);
            poseStack.pushPose();
            poseStack.mulPose(state.transformations.body());
            this.submitSign(poseStack, state.lightCoords, state.woodType, bodyModel, breakProgress, submitNodeCollector);
            poseStack.popPose();
        }
        if(ModCompat.isShadowPass()) return;
        if (state.frontText == null && state.backText == null) return;
        Vec3 cameraPos = Minecraft.getInstance().getCameraEntity().position();
        poseStack.pushPose();
        poseStack.mulPose(state.transformations.frontText());

        if (isFacingCamera(poseStack, cameraPos)) {
            if (state.frontText != null) {
                this.submitSignText(state, poseStack, submitNodeCollector, state.frontText);
                poseStack.popPose();
            }
        }
        else{
            if (state.backText != null) {
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.mulPose(state.transformations.backText());
                this.submitSignText(state, poseStack, submitNodeCollector, state.backText);
                poseStack.popPose();
            }
        }
    }

    private static boolean isFacingCamera(PoseStack poseStack, Vec3 cameraPos) {
        Matrix4f pose = poseStack.last().pose();

        PoseStack.Pose topPose = poseStack.last();
        Matrix3f normalMatrix = topPose.normal();
        Vector3f forwardVector = new Vector3f();
        normalMatrix.getColumn(2, forwardVector);

        Vector3f pos = pose.transformPosition(0, 0, 0, new Vector3f());

        return forwardVector.dot(pos) < 0.0f;
    }

    private void submitSignText(final S state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final SignText signText) {
        int darkColor = getDarkColor(signText);
        int signMidpoint = 4 * state.textLineHeight / 2;
        FormattedCharSequence[] formattedLines = signText.getRenderMessages(state.isTextFilteringEnabled, (input) -> {
            List<FormattedCharSequence> components = this.font.split(input, state.maxTextLineWidth);
            return components.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)components.get(0);
        });
        int textColor;
        boolean drawOutline;
        int lightVal;
        if (signText.hasGlowingText()) {
            textColor = signText.getColor().getTextColor();
            drawOutline = textColor == DyeColor.BLACK.getTextColor() || state.drawOutline;
            lightVal = 15728880;
        } else {
            textColor = darkColor;
            drawOutline = false;
            lightVal = state.lightCoords;
        }

        for(int i = 0; i < 4; ++i) {
            FormattedCharSequence actualLine = formattedLines[i];
            float x1 = (float)(-this.font.width(actualLine) / 2);
            submitNodeCollector.submitText(poseStack, x1, (float)(i * state.textLineHeight - signMidpoint), actualLine, false, DisplayMode.POLYGON_OFFSET, lightVal, textColor, 0, drawOutline ? darkColor : 0);
        }

   }
}

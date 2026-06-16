package fr.madu59.obe.client.renderer.blockentity.sign;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.sign.ext.SignBlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.sign.ext.SignRenderStateExt;

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
import org.jspecify.annotations.Nullable;

public abstract class OBEAbstractSignRenderer<S extends SignRenderState> extends AbstractSignRenderer<S> {

    public OBEAbstractSignRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submitSignWithText(final S state, final PoseStack poseStack, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final SubmitNodeCollector submitNodeCollector) {

        if(!SettingsManager.OPTIMISED_SIGNS.getValue()){
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
                this.submitSignText(state, poseStack, submitNodeCollector, state.frontText, true);
                poseStack.popPose();
            }
        }
        else{
            if (state.backText != null) {
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.mulPose(state.transformations.backText());
                this.submitSignText(state, poseStack, submitNodeCollector, state.backText, false);
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

    public void submitSignText(final S state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final SignText signText, final boolean front) {
        SignRenderStateExt stateExt = (SignRenderStateExt) (Object) state;
        int darkColor = getDarkColor(signText);
        int signMidpoint = 4 * state.textLineHeight / 2;
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
            FormattedCharSequence actualLine = stateExt.getCachedLines(front)[i];
            float x1 = stateExt.getLineWidths(front)[i];
            submitNodeCollector.submitText(poseStack, x1, (float)(i * state.textLineHeight - signMidpoint), actualLine, false, DisplayMode.POLYGON_OFFSET, lightVal, textColor, 0, drawOutline ? darkColor : 0);
        }

    }

    public void extractRenderState(final SignBlockEntity blockEntity, final S state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        ((BlockEntityRenderStateExt)state).blockEntity(blockEntity);

        SignBlockEntityExt beExt = (SignBlockEntityExt) (Object) blockEntity;
        SignRenderStateExt stateExt = (SignRenderStateExt) (Object) state;

        boolean filteringChanged = state.isTextFilteringEnabled != beExt.getLastFiltering();
        if (filteringChanged) {
            beExt.setLastFiltering(state.isTextFilteringEnabled);
        }

        rebuildIfDirty(blockEntity.getFrontText(), true,  filteringChanged, beExt, state);
        rebuildIfDirty(blockEntity.getBackText(),  false, filteringChanged, beExt, state);

        stateExt.setCachedLines(beExt.getCachedLines(true), beExt.getCachedLines(false));
        stateExt.setLineWidths(beExt.getLineWidths(true), beExt.getLineWidths(false));
    }

    private void rebuildIfDirty(SignText text, boolean front, boolean filteringChanged,
            SignBlockEntityExt beExt, S state) {
        if (!filteringChanged && text == beExt.getLastText(front)) return;

        FormattedCharSequence[] lines = text.getRenderMessages(
            state.isTextFilteringEnabled,
            input -> {
                List<FormattedCharSequence> split = this.font.split(input, state.maxTextLineWidth);
                return split.isEmpty() ? FormattedCharSequence.EMPTY : split.get(0);
            }
        );
        float[] widths = new float[4];
        for (int i = 0; i < 4; i++) widths[i] = -this.font.width(lines[i]) / 2f;

        beExt.setCachedLines(front, lines);
        beExt.setLineWidths(front, widths);
        beExt.setLastText(front, text);
    }
}

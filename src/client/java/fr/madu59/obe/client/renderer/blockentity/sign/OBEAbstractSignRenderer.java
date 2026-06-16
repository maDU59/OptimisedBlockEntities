package fr.madu59.obe.client.renderer.blockentity.sign;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

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
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class OBEAbstractSignRenderer extends AbstractSignRenderer {

    public OBEAbstractSignRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submitSignWithText(SignRenderState signRenderState, PoseStack poseStack, BlockState blockState, SignBlock signBlock, WoodType woodType, Model.Simple simple, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, SubmitNodeCollector submitNodeCollector) {
        poseStack.pushPose();
        this.translateSign(poseStack, -signBlock.getYRotationDegrees(blockState), blockState);
        if(!SettingsManager.OPTIMISED_SIGNS.getValue()) this.submitSign(poseStack, signRenderState.lightCoords, woodType, simple, crumblingOverlay, submitNodeCollector);
        this.submitSignText(signRenderState, poseStack, submitNodeCollector, true);
        this.submitSignText(signRenderState, poseStack, submitNodeCollector, false);
        poseStack.popPose();
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

    private void submitSignText(SignRenderState signRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, boolean bl) {
        SignText signText = bl ? signRenderState.frontText : signRenderState.backText;
        SignRenderStateExt stateExt = (SignRenderStateExt) (Object) signRenderState;
        if (signText != null) {
            Vec3 cameraPos = Minecraft.getInstance().getCameraEntity().position();
            poseStack.pushPose();
            this.translateSignText(poseStack, bl, this.getTextOffset());
            if (!isFacingCamera(poseStack, cameraPos) || ModCompat.isShadowPass()) {
                poseStack.popPose();
                return;
            }
            int i = getDarkColor(signText);
            int j = 4 * signRenderState.textLineHeight / 2;
            int k;
            boolean bl2;
            int l;
            if (signText.hasGlowingText()) {
                k = signText.getColor().getTextColor();
                bl2 = k == DyeColor.BLACK.getTextColor() || signRenderState.drawOutline;
                l = 15728880;
            } else {
                k = i;
                bl2 = false;
                l = signRenderState.lightCoords;
            }

            for(int m = 0; m < 4; ++m) {
                FormattedCharSequence formattedCharSequence = stateExt.getCachedLines(bl)[m];
                float f = stateExt.getLineWidths(bl)[m];
                submitNodeCollector.submitText(poseStack, f, (float)(m * signRenderState.textLineHeight - j), formattedCharSequence, false, DisplayMode.POLYGON_OFFSET, l, k, 0, bl2 ? i : 0);
            }

            poseStack.popPose();
        }
    }

    private void translateSignText(PoseStack poseStack, boolean bl, Vec3 vec3) {
        if (!bl) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        }

        float f = 0.015625F * this.getSignTextRenderScale();
        poseStack.translate(vec3);
        poseStack.scale(f, -f, f);
    }

    public void extractRenderState(SignBlockEntity blockEntity, SignRenderState state, float f, Vec3 vec3, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(blockEntity, state, f, vec3, crumblingOverlay);

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

    private void rebuildIfDirty(SignText text, boolean front, boolean filteringChanged, SignBlockEntityExt beExt, SignRenderState state) {
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

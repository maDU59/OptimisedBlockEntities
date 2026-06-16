package fr.madu59.obe.client.renderer.blockentity.sign;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.sign.ext.SignBlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.sign.ext.SignRenderStateExt;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class OBEAbstractSignRenderer extends AbstractSignRenderer {

    public OBEAbstractSignRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void renderSignWithText(SignBlockEntity signBlockEntity, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BlockState blockState, SignBlock signBlock, WoodType woodType, Model model) {
        poseStack.pushPose();
        this.translateSign(poseStack, -signBlock.getYRotationDegrees(blockState), blockState);
        if(!SettingsManager.OPTIMISED_SIGNS.getValue()) this.renderSign(poseStack, multiBufferSource, i, j, woodType, model);
        this.renderSignText(signBlockEntity.getBlockPos(), signBlockEntity.getFrontText(), poseStack, multiBufferSource, i, signBlockEntity.getTextLineHeight(), signBlockEntity.getMaxTextLineWidth(), true);
        this.renderSignText(signBlockEntity.getBlockPos(), signBlockEntity.getBackText(), poseStack, multiBufferSource, i, signBlockEntity.getTextLineHeight(), signBlockEntity.getMaxTextLineWidth(), false);
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

    private void renderSignText(BlockPos blockPos, SignText signText, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int k, boolean bl) {
        if (signText == null) return;
        poseStack.pushPose();
        this.translateSignText(poseStack, bl, this.getTextOffset());
        Vec3 cameraPos = Minecraft.getInstance().getCameraEntity().position();
        if (!isFacingCamera(poseStack, cameraPos) || ModCompat.isShadowPass()) {
            poseStack.popPose();
            return;
        }
        int l = getDarkColor(signText);
        int m = 4 * j / 2;
        FormattedCharSequence[] formattedCharSequences = signText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (component) -> {
            List<FormattedCharSequence> list = this.font.split(component, k);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)list.get(0);
        });
        int n;
        boolean bl2;
        int o;
        if (signText.hasGlowingText()) {
            n = signText.getColor().getTextColor();
            bl2 = isOutlineVisible(blockPos, n);
            o = 15728880;
        } else {
            n = l;
            bl2 = false;
            o = i;
        }

        for(int p = 0; p < 4; ++p) {
            FormattedCharSequence formattedCharSequence = formattedCharSequences[p];
            float f = (float)(-this.font.width(formattedCharSequence) / 2);
            if (bl2) {
                this.font.drawInBatch8xOutline(formattedCharSequence, f, (float)(p * j - m), n, l, poseStack.last().pose(), multiBufferSource, o);
            } else {
                this.font.drawInBatch(formattedCharSequence, f, (float)(p * j - m), n, false, poseStack.last().pose(), multiBufferSource, DisplayMode.POLYGON_OFFSET, 0, o);
            }
        }

        poseStack.popPose();
    }
}

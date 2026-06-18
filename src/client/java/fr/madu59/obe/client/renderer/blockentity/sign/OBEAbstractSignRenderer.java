package fr.madu59.obe.client.renderer.blockentity.sign;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.compat.ModCompat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractSignRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class OBEAbstractSignRenderer<S extends SignRenderState> extends AbstractSignRenderer<S> {

    public OBEAbstractSignRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(final S state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if(ModCompat.isShadowPass()) return;
        Vec3 cameraPos = Minecraft.getInstance().getCameraEntity().position();
        if (state.frontText != null) {
            poseStack.pushPose();
            poseStack.mulPose(state.transformations.frontText());
            if(isFacingCamera(poseStack, cameraPos)) this.submitSignText(state, poseStack, submitNodeCollector, state.frontText);
            poseStack.popPose();
        }

        if (state.backText != null) {
            poseStack.pushPose();
            poseStack.mulPose(state.transformations.backText());
            if(isFacingCamera(poseStack, cameraPos)) this.submitSignText(state, poseStack, submitNodeCollector, state.backText);
            poseStack.popPose();
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
}

package fr.madu59.obe.client.renderer.blockentity.shulkerbox;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.Vec3;

public class OBEShulkerBoxRenderer extends ShulkerBoxRenderer {

    public OBEShulkerBoxRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(final ShulkerBoxRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if(RenderModeManager.shouldRenderEntity(state)){
            super.submit(state, poseStack, submitNodeCollector, camera);
        }
    }
    
    @Override
    public void extractRenderState(final ShulkerBoxBlockEntity blockEntity, final ShulkerBoxRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        ((BlockEntityRenderStateExt)state).blockEntity(blockEntity);
        if(RenderModeManager.shouldRenderEntity(state)) super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    }
}

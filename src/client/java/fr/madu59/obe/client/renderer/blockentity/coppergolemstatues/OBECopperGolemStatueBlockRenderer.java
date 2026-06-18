package fr.madu59.obe.client.renderer.blockentity.coppergolemstatues;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.CopperGolemStatueBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.phys.Vec3;

public class OBECopperGolemStatueBlockRenderer extends CopperGolemStatueBlockRenderer {

    public OBECopperGolemStatueBlockRenderer(Context context) {
        super(context);
    }
    
    @Override
    public void submit(final CopperGolemStatueRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if(RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_COPPER_GOLEMS.getValue(), state)) super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public void extractRenderState(final CopperGolemStatueBlockEntity blockEntity, final CopperGolemStatueRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        ((BlockEntityRenderStateExt)state).blockEntity(blockEntity);
        if(RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_COPPER_GOLEMS.getValue(), blockEntity)) super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    }
}

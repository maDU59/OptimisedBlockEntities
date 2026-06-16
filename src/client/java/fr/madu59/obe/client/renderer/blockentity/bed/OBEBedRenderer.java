package fr.madu59.obe.client.renderer.blockentity.bed;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BedRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.phys.Vec3;

public class OBEBedRenderer extends BedRenderer{

    public OBEBedRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(final BedRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if(!SettingsManager.OPTIMISED_BEDS.getValue()) super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public void extractRenderState(final BedBlockEntity blockEntity, final BedRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        if(!SettingsManager.OPTIMISED_BEDS.getValue()) super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    }
}

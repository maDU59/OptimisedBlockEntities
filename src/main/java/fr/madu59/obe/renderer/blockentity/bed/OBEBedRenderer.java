package fr.madu59.obe.renderer.blockentity.bed;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.config.SettingsManager;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BedBlockEntity;

public class OBEBedRenderer extends BedRenderer{

    public OBEBedRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BedBlockEntity bedBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if(RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_BEDS.getValue(), bedBlockEntity)) super.render(bedBlockEntity, f, poseStack, multiBufferSource, i, j);
    }
}

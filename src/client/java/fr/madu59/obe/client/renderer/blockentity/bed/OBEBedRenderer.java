package fr.madu59.obe.client.renderer.blockentity.bed;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.phys.Vec3;

public class OBEBedRenderer extends BedRenderer{

    public OBEBedRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BedBlockEntity bedBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Vec3 vec3) {
        if(!SettingsManager.OPTIMISED_BEDS.getValue()) super.render(bedBlockEntity, f, poseStack, multiBufferSource, i, j, vec3);
    }
}

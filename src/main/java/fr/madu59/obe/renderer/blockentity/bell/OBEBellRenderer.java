package fr.madu59.obe.renderer.blockentity.bell;


import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BellBlockEntity;

public class OBEBellRenderer extends BellRenderer{

    public OBEBellRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BellBlockEntity bellBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if(RenderModeManager.shouldRenderEntity(bellBlockEntity)){
            super.render(bellBlockEntity, f, poseStack, multiBufferSource, i, j);
        }
    }
}
package fr.madu59.obe.renderer.blockentity.shulkerbox;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

public class OBEShulkerBoxRenderer extends ShulkerBoxRenderer {

    public OBEShulkerBoxRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if(RenderModeManager.shouldRenderEntity(shulkerBoxBlockEntity)){
            super.render(shulkerBoxBlockEntity, f, poseStack, multiBufferSource, i, j);
        }
    }
}

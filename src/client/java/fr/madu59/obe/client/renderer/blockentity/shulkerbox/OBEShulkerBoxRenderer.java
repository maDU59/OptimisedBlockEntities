package fr.madu59.obe.client.renderer.blockentity.shulkerbox;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.Vec3;

public class OBEShulkerBoxRenderer extends ShulkerBoxRenderer {

    public OBEShulkerBoxRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Vec3 vec3) {
        if(RenderModeManager.shouldRenderEntity(shulkerBoxBlockEntity)){
            super.render(shulkerBoxBlockEntity, f, poseStack, multiBufferSource, i, j, vec3);
        }
    }
}

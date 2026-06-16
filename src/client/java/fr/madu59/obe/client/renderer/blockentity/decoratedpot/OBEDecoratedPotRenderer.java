package fr.madu59.obe.client.renderer.blockentity.decoratedpot;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.phys.Vec3;

public class OBEDecoratedPotRenderer extends DecoratedPotRenderer {

    public OBEDecoratedPotRenderer(Context context) {
        super(context);
    }
    
    @Override
   public void render(DecoratedPotBlockEntity decoratedPotBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if(RenderModeManager.shouldRenderEntity(decoratedPotBlockEntity)){
            super.render(decoratedPotBlockEntity, f, poseStack, multiBufferSource, i, j);
        }
    }
}

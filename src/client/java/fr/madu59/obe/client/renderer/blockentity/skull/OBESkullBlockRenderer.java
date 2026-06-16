package fr.madu59.obe.client.renderer.blockentity.skull;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

public class OBESkullBlockRenderer extends SkullBlockRenderer {

   public OBESkullBlockRenderer(final BlockEntityRendererProvider.Context context) {
      super(context);
   }

   @Override
   public void render(SkullBlockEntity skullBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
      if(RenderModeManager.shouldRenderEntity(skullBlockEntity)){
         super.render(skullBlockEntity, f, poseStack, multiBufferSource, i, j);
      }
   }
}

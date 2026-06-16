package fr.madu59.obe.client.renderer.blockentity.skull;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.SkullBlockRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;

public class OBESkullBlockRenderer extends SkullBlockRenderer {

   public OBESkullBlockRenderer(final BlockEntityRendererProvider.Context context) {
      super(context);
   }

   @Override
   public void submit(final SkullBlockRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      if(RenderModeManager.shouldRenderEntity(state)){
         super.submit(state, poseStack, submitNodeCollector, camera);
      }
   }

   @Override
   public void extractRenderState(final SkullBlockEntity blockEntity, final SkullBlockRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      ((BlockEntityRenderStateExt)state).blockEntity(blockEntity);
      if(RenderModeManager.shouldRenderEntity(blockEntity)) super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
   }
}

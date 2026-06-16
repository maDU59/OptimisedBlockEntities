package fr.madu59.obe.client.renderer.blockentity.sign;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.Map;
import java.util.Objects;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class OBEStandingSignRenderer extends OBEAbstractSignRenderer {
   private static final Vec3 TEXT_OFFSET = new Vec3((double)0.0F, (double)0.33333334F, (double)0.046666667F);
   private final Map<WoodType, Models> signModels;

   public OBEStandingSignRenderer(BlockEntityRendererProvider.Context context) {
      super(context);
      this.signModels = (Map)WoodType.values().collect(ImmutableMap.toImmutableMap((woodType) -> woodType, (woodType) -> new Models(createSignModel(context.getModelSet(), woodType, true), createSignModel(context.getModelSet(), woodType, false))));
   }

   protected Model getSignModel(BlockState blockState, WoodType woodType) {
      Models models = (Models)this.signModels.get(woodType);
      return blockState.getBlock() instanceof StandingSignBlock ? models.standing() : models.wall();
   }

   protected Material getSignMaterial(WoodType woodType) {
      return Sheets.getSignMaterial(woodType);
   }

   public float getSignModelRenderScale() {
      return 0.6666667F;
   }

   protected float getSignTextRenderScale() {
      return 0.6666667F;
   }

   public static void translateBase(PoseStack poseStack, float f) {
      poseStack.translate(0.5F, 0.5F, 0.5F);
      poseStack.mulPose(Axis.YP.rotationDegrees(f));
   }

   public void translateSign(PoseStack poseStack, float f, BlockState blockState) {
      translateBase(poseStack, f);
      if (!(blockState.getBlock() instanceof StandingSignBlock)) {
         poseStack.translate(0.0F, -0.3125F, -0.4375F);
      }
   }

   protected Vec3 getTextOffset() {
      return TEXT_OFFSET;
   }

   public static void renderInHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Model model, Material material) {
      poseStack.pushPose();
      applyInHandTransforms(poseStack);
      Objects.requireNonNull(model);
      VertexConsumer vertexConsumer = material.buffer(multiBufferSource, model::renderType);
      model.renderToBuffer(poseStack, vertexConsumer, i, j);
      poseStack.popPose();
   }

   public static void applyInHandTransforms(PoseStack poseStack) {
      translateBase(poseStack, 0.0F);
      poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
   }

   public static Model createSignModel(EntityModelSet entityModelSet, WoodType woodType, boolean bl) {
      ModelLayerLocation modelLayerLocation = bl ? ModelLayers.createStandingSignModelName(woodType) : ModelLayers.createWallSignModelName(woodType);
      return new Model.Simple(entityModelSet.bakeLayer(modelLayerLocation), RenderType::entityCutoutNoCull);
   }

   public static LayerDefinition createSignLayer(boolean bl) {
      MeshDefinition meshDefinition = new MeshDefinition();
      PartDefinition partDefinition = meshDefinition.getRoot();
      partDefinition.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), PartPose.ZERO);
      if (bl) {
         partDefinition.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.ZERO);
      }

      return LayerDefinition.create(meshDefinition, 64, 32);
   }

   @Environment(EnvType.CLIENT)
   static record Models(Model standing, Model wall) {
   }
}

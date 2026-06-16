package fr.madu59.obe.client.renderer.blockentity.sign;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer.AttachmentType;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class OBEHangingSignRenderer extends OBEAbstractSignRenderer {
   public static final float MODEL_RENDER_SCALE = 1.0F;
   private static final float TEXT_RENDER_SCALE = 0.9F;
   private static final Vec3 TEXT_OFFSET = new Vec3((double)0.0F, (double)-0.32F, (double)0.073F);
   private final Map<ModelKey, Model.Simple> hangingSignModels;

   public OBEHangingSignRenderer(BlockEntityRendererProvider.Context context) {
      super(context);
      Stream<ModelKey> stream = WoodType.values().flatMap((woodType) -> Arrays.stream(AttachmentType.values()).map((attachmentType) -> new ModelKey(woodType, attachmentType)));
      this.hangingSignModels = (Map)stream.collect(ImmutableMap.toImmutableMap((modelKey) -> modelKey, (modelKey) -> createSignModel(context.entityModelSet(), modelKey.woodType, modelKey.attachmentType)));
   }

   public static Model.Simple createSignModel(EntityModelSet entityModelSet, WoodType woodType, AttachmentType attachmentType) {
      return new Model.Simple(entityModelSet.bakeLayer(ModelLayers.createHangingSignModelName(woodType, attachmentType)), RenderType::entityCutoutNoCull);
   }

   public float getSignModelRenderScale() {
      return 1.0F;
   }

   protected float getSignTextRenderScale() {
      return 0.9F;
   }

   public static void translateBase(PoseStack poseStack, float f) {
      poseStack.translate((double)0.5F, (double)0.9375F, (double)0.5F);
      poseStack.mulPose(Axis.YP.rotationDegrees(f));
      poseStack.translate(0.0F, -0.3125F, 0.0F);
   }

   public void translateSign(PoseStack poseStack, float f, BlockState blockState) {
      translateBase(poseStack, f);
   }

   protected Model.Simple getSignModel(BlockState blockState, WoodType woodType) {
      AttachmentType attachmentType = HangingSignRenderer.AttachmentType.byBlockState(blockState);
      return (Model.Simple)this.hangingSignModels.get(new ModelKey(woodType, attachmentType));
   }

   protected Material getSignMaterial(WoodType woodType) {
      return Sheets.getHangingSignMaterial(woodType);
   }

   protected Vec3 getTextOffset() {
      return TEXT_OFFSET;
   }

   public static void submitSpecial(MaterialSet materialSet, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, int j, Model.Simple simple, Material material) {
      poseStack.pushPose();
      translateBase(poseStack, 0.0F);
      poseStack.scale(1.0F, -1.0F, -1.0F);
      Unit var10002 = Unit.INSTANCE;
      Objects.requireNonNull(simple);
      submitNodeCollector.submitModel(simple, var10002, poseStack, material.renderType(simple::renderType), i, j, -1, materialSet.get(material), OverlayTexture.NO_OVERLAY, (ModelFeatureRenderer.CrumblingOverlay)null);
      poseStack.popPose();
   }

   public static LayerDefinition createHangingSignLayer(AttachmentType attachmentType) {
      MeshDefinition meshDefinition = new MeshDefinition();
      PartDefinition partDefinition = meshDefinition.getRoot();
      partDefinition.addOrReplaceChild("board", CubeListBuilder.create().texOffs(0, 12).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), PartPose.ZERO);
      if (attachmentType == HangingSignRenderer.AttachmentType.WALL) {
         partDefinition.addOrReplaceChild("plank", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), PartPose.ZERO);
      }

      if (attachmentType == HangingSignRenderer.AttachmentType.WALL || attachmentType == HangingSignRenderer.AttachmentType.CEILING) {
         PartDefinition partDefinition2 = partDefinition.addOrReplaceChild("normalChains", CubeListBuilder.create(), PartPose.ZERO);
         partDefinition2.addOrReplaceChild("chainL1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, (-(float)Math.PI / 4F), 0.0F));
         partDefinition2.addOrReplaceChild("chainL2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));
         partDefinition2.addOrReplaceChild("chainR1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, (-(float)Math.PI / 4F), 0.0F));
         partDefinition2.addOrReplaceChild("chainR2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));
      }

      if (attachmentType == HangingSignRenderer.AttachmentType.CEILING_MIDDLE) {
         partDefinition.addOrReplaceChild("vChains", CubeListBuilder.create().texOffs(14, 6).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 0.0F), PartPose.ZERO);
      }

      return LayerDefinition.create(meshDefinition, 64, 32);
   }

   public static record ModelKey(WoodType woodType, AttachmentType attachmentType) {}
}
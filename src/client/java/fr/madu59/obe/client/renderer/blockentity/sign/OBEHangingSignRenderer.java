package fr.madu59.obe.client.renderer.blockentity.sign;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.Map;
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
import net.minecraft.client.renderer.blockentity.WallAndGroundTransformations;
import net.minecraft.client.renderer.blockentity.state.HangingSignRenderState;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.HangingSignBlock.Attachment;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class OBEHangingSignRenderer extends OBEAbstractSignRenderer<HangingSignRenderState> {
   private static final Vector3fc TEXT_OFFSET = new Vector3f(0.0F, -0.32F, 0.073F);
   public static final WallAndGroundTransformations<SignRenderState.SignTransformations> TRANSFORMATIONS = new WallAndGroundTransformations<>(OBEHangingSignRenderer::createWallTransformation, OBEHangingSignRenderer::createGroundTransformation, 16);
   private final Map<WoodType, Models> signModels;

   public OBEHangingSignRenderer(final BlockEntityRendererProvider.Context context) {
      super(context);
      this.signModels = (Map)WoodType.values().collect(ImmutableMap.toImmutableMap((type) -> type, (type) -> OBEHangingSignRenderer.Models.create(context, type)));
   }

   public HangingSignRenderState createRenderState() {
      return new HangingSignRenderState();
   }

   public void extractRenderState(final SignBlockEntity blockEntity, final HangingSignRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      BlockState blockState = blockEntity.getBlockState();
      state.attachmentType = HangingSignBlock.getAttachmentPoint(blockState);
      if (blockState.getBlock() instanceof WallHangingSignBlock) {
         state.transformations = (SignRenderState.SignTransformations)TRANSFORMATIONS.wallTransformation((Direction)blockState.getValue(WallHangingSignBlock.FACING));
      } else {
         state.transformations = (SignRenderState.SignTransformations)TRANSFORMATIONS.freeTransformations((Integer)blockState.getValue(CeilingHangingSignBlock.ROTATION));
      }
   }

   public static Model.Simple createSignModel(final EntityModelSet entityModelSet, final WoodType woodType, final HangingSignBlock.Attachment attachmentType) {
      return new Model.Simple(entityModelSet.bakeLayer(ModelLayers.createHangingSignModelName(woodType, attachmentType)), RenderTypes::entityCutout);
   }

   private static Matrix4f baseTransformation(final float angle) {
      return (new Matrix4f()).translation(0.5F, 0.9375F, 0.5F).rotate(Axis.YP.rotationDegrees(-angle)).translate(0.0F, -0.3125F, 0.0F);
   }

   private static Transformation bodyTransformation(final float angle) {
      return new Transformation(baseTransformation(angle).scale(1.0F, -1.0F, -1.0F));
   }

   private static Transformation textTransformation(final float angle, final boolean isFrontText) {
      Matrix4f result = baseTransformation(angle);
      if (!isFrontText) {
         result.rotate(Axis.YP.rotationDegrees(180.0F));
      }

      result.translate(TEXT_OFFSET);
      result.scale(0.0140625F, -0.0140625F, 0.0140625F);
      return new Transformation(result);
   }

   private static SignRenderState.SignTransformations createTransformations(final float angle) {
      return new SignRenderState.SignTransformations(bodyTransformation(angle), textTransformation(angle, true), textTransformation(angle, false));
   }

   private static SignRenderState.SignTransformations createGroundTransformation(final int segment) {
      return createTransformations(RotationSegment.convertToDegrees(segment));
   }

   private static SignRenderState.SignTransformations createWallTransformation(final Direction direction) {
      return createTransformations(direction.toYRot());
   }

   protected Model.Simple getSignModel(final HangingSignRenderState state) {
      return ((Models)this.signModels.get(state.woodType)).get(state.attachmentType);
   }

   protected SpriteId getSignSprite(final WoodType type) {
      return Sheets.getHangingSignSprite(type);
   }

   public static void submitSpecial(final SpriteGetter sprites, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final Model.Simple model, final SpriteId sprite) {
      submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, lightCoords, overlayCoords, -1, sprite, sprites, 0, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public static LayerDefinition createHangingSignLayer(final HangingSignBlock.Attachment type) {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("board", CubeListBuilder.create().texOffs(0, 12).addBox(-7.0F, 0.0F, -1.0F, 14.0F, 10.0F, 2.0F), PartPose.ZERO);
      if (type == Attachment.WALL) {
         root.addOrReplaceChild("plank", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -2.0F, 16.0F, 2.0F, 4.0F), PartPose.ZERO);
      }

      if (type == Attachment.WALL || type == Attachment.CEILING) {
         PartDefinition normalChains = root.addOrReplaceChild("normalChains", CubeListBuilder.create(), PartPose.ZERO);
         normalChains.addOrReplaceChild("chainL1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, (-(float)Math.PI / 4F), 0.0F));
         normalChains.addOrReplaceChild("chainL2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(-5.0F, -6.0F, 0.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));
         normalChains.addOrReplaceChild("chainR1", CubeListBuilder.create().texOffs(0, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, (-(float)Math.PI / 4F), 0.0F));
         normalChains.addOrReplaceChild("chainR2", CubeListBuilder.create().texOffs(6, 6).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 6.0F, 0.0F), PartPose.offsetAndRotation(5.0F, -6.0F, 0.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));
      }

      if (type == Attachment.CEILING_MIDDLE) {
         root.addOrReplaceChild("vChains", CubeListBuilder.create().texOffs(14, 6).addBox(-6.0F, -6.0F, 0.0F, 12.0F, 6.0F, 0.0F), PartPose.ZERO);
      }

      return LayerDefinition.create(mesh, 64, 32);
   }

   private static record Models(Model.Simple ceiling, Model.Simple ceilingMiddle, Model.Simple wall) {
      public static Models create(final BlockEntityRendererProvider.Context context, final WoodType type) {
         return new Models(HangingSignRenderer.createSignModel(context.entityModelSet(), type, Attachment.CEILING), HangingSignRenderer.createSignModel(context.entityModelSet(), type, Attachment.CEILING_MIDDLE), HangingSignRenderer.createSignModel(context.entityModelSet(), type, Attachment.WALL));
      }

      public Model.Simple get(final HangingSignBlock.Attachment attachmentType) {
         Model.Simple var10000;
         switch (attachmentType) {
            case CEILING -> var10000 = this.ceiling;
            case CEILING_MIDDLE -> var10000 = this.ceilingMiddle;
            case WALL -> var10000 = this.wall;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }
}

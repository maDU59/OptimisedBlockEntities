package fr.madu59.obe.client.renderer.blockentity.sign;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;

import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class OBESignRenderer implements BlockEntityRenderer<SignBlockEntity> {
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private static final Vec3 TEXT_OFFSET = new Vec3(0.0, 0.33333334F, 0.046666667F);
    private final Map<WoodType, OBESignRenderer.SignModel> signModels;
    private final Font font;

    public OBESignRenderer(final BlockEntityRendererProvider.Context context) {
        this.signModels = WoodType.values()
            .collect(
                ImmutableMap.toImmutableMap(
                    p_173645_ -> (WoodType)p_173645_, p_173651_ -> new OBESignRenderer.SignModel(context.bakeLayer(ModelLayers.createSignModelName(p_173651_)))
                )
            );
        this.font = context.getFont();
    }

    public void render(SignBlockEntity p_112497_, float p_112498_, PoseStack p_112499_, MultiBufferSource p_112500_, int p_112501_, int p_112502_) {
        BlockState blockstate = p_112497_.getBlockState();
        SignBlock signblock = (SignBlock)blockstate.getBlock();
        WoodType woodtype = SignBlock.getWoodType(signblock);
        OBESignRenderer.SignModel signrenderer$signmodel = this.signModels.get(woodtype);
        signrenderer$signmodel.stick.visible = blockstate.getBlock() instanceof StandingSignBlock;
        this.renderSignWithText(p_112497_, p_112499_, p_112500_, p_112501_, p_112502_, blockstate, signblock, woodtype, signrenderer$signmodel);
    }
    
    public void renderSignWithText(SignBlockEntity signBlockEntity, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BlockState blockState, SignBlock signBlock, WoodType woodType, Model model) {
        poseStack.pushPose();
        this.translateSign(poseStack, -signBlock.getYRotationDegrees(blockState), blockState);
        if(RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_SIGNS.getValue(), signBlockEntity)) this.renderSign(poseStack, multiBufferSource, i, j, woodType, model);
        this.renderSignText(signBlockEntity.getBlockPos(), signBlockEntity.getFrontText(), poseStack, multiBufferSource, i, signBlockEntity.getTextLineHeight(), signBlockEntity.getMaxTextLineWidth(), true);
        this.renderSignText(signBlockEntity.getBlockPos(), signBlockEntity.getBackText(), poseStack, multiBufferSource, i, signBlockEntity.getTextLineHeight(), signBlockEntity.getMaxTextLineWidth(), false);
        poseStack.popPose();
    }

    private static boolean isFacingCamera(PoseStack poseStack, Vec3 cameraPos) {
        Matrix4f pose = poseStack.last().pose();

        PoseStack.Pose topPose = poseStack.last();
        Matrix3f normalMatrix = topPose.normal();
        Vector3f forwardVector = new Vector3f();
        normalMatrix.getColumn(2, forwardVector);

        Vector3f pos = pose.transformPosition(0, 0, 0, new Vector3f());

        return forwardVector.dot(pos) < 0.0f;
    }

    private void renderSignText(BlockPos blockPos, SignText signText, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int k, boolean bl) {
        if (signText == null) return;
        poseStack.pushPose();
        this.translateSignText(poseStack, bl, this.getTextOffset());
        Vec3 cameraPos = Minecraft.getInstance().getCameraEntity().position();
        if (!isFacingCamera(poseStack, cameraPos) || ModCompat.isShadowPass()) {
            poseStack.popPose();
            return;
        }
        int l = getDarkColor(signText);
        int m = 4 * j / 2;
        FormattedCharSequence[] formattedCharSequences = signText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (component) -> {
            List<FormattedCharSequence> list = this.font.split(component, k);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)list.get(0);
        });
        int n;
        boolean bl2;
        int o;
        if (signText.hasGlowingText()) {
            n = signText.getColor().getTextColor();
            bl2 = isOutlineVisible(blockPos, n);
            o = 15728880;
        } else {
            n = l;
            bl2 = false;
            o = i;
        }

        for(int p = 0; p < 4; ++p) {
            FormattedCharSequence formattedCharSequence = formattedCharSequences[p];
            float f = (float)(-this.font.width(formattedCharSequence) / 2);
            if (bl2) {
                this.font.drawInBatch8xOutline(formattedCharSequence, f, (float)(p * j - m), n, l, poseStack.last().pose(), multiBufferSource, o);
            } else {
                this.font.drawInBatch(formattedCharSequence, f, (float)(p * j - m), n, false, poseStack.last().pose(), multiBufferSource, DisplayMode.POLYGON_OFFSET, 0, o);
            }
        }

        poseStack.popPose();
    }

    public float getSignModelRenderScale() {
        return 0.6666667F;
    }

    public float getSignTextRenderScale() {
        return 0.6666667F;
    }

    public void translateSign(PoseStack p_278074_, float p_277875_, BlockState p_277559_) {
        p_278074_.translate(0.5F, 0.75F * this.getSignModelRenderScale(), 0.5F);
        p_278074_.mulPose(Axis.YP.rotationDegrees(p_277875_));
        if (!(p_277559_.getBlock() instanceof StandingSignBlock)) {
            p_278074_.translate(0.0F, -0.3125F, -0.4375F);
        }
    }

    public void renderSign(PoseStack p_279104_, MultiBufferSource p_279408_, int p_279494_, int p_279344_, WoodType p_279170_, Model p_279159_) {
        p_279104_.pushPose();
        float f = this.getSignModelRenderScale();
        p_279104_.scale(f, -f, -f);
        Material material = this.getSignMaterial(p_279170_);
        VertexConsumer vertexconsumer = material.buffer(p_279408_, p_279159_::renderType);
        this.renderSignModel(p_279104_, p_279494_, p_279344_, p_279159_, vertexconsumer);
        p_279104_.popPose();
    }

    public void renderSignModel(PoseStack p_250252_, int p_249399_, int p_249042_, Model p_250082_, VertexConsumer p_251093_) {
        SignRenderer.SignModel signrenderer$signmodel = (SignRenderer.SignModel)p_250082_;
        signrenderer$signmodel.root.render(p_250252_, p_251093_, p_249399_, p_249042_);
    }

    public Material getSignMaterial(WoodType p_251961_) {
        return Sheets.getSignMaterial(p_251961_);
    }

    public void translateSignText(PoseStack p_279133_, boolean p_279134_, Vec3 p_279280_) {
        if (!p_279134_) {
            p_279133_.mulPose(Axis.YP.rotationDegrees(180.0F));
        }

        float f = 0.015625F * this.getSignTextRenderScale();
        p_279133_.translate(p_279280_.x, p_279280_.y, p_279280_.z);
        p_279133_.scale(f, -f, f);
    }

    public Vec3 getTextOffset() {
        return TEXT_OFFSET;
    }

    static boolean isOutlineVisible(BlockPos p_277741_, int p_278022_) {
        if (p_278022_ == DyeColor.BLACK.getTextColor()) {
            return true;
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localplayer = minecraft.player;
            if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping()) {
                return true;
            } else {
                Entity entity = minecraft.getCameraEntity();
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(p_277741_)) < (double)OUTLINE_RENDER_DISTANCE;
            }
        }
    }

    public static int getDarkColor(SignText p_277914_) {
        int i = p_277914_.getColor().getTextColor();
        if (i == DyeColor.BLACK.getTextColor() && p_277914_.hasGlowingText()) {
            return -988212;
        } else {
            double d0 = 0.4;
            int j = (int)((double)FastColor.ARGB32.red(i) * 0.4);
            int k = (int)((double)FastColor.ARGB32.green(i) * 0.4);
            int l = (int)((double)FastColor.ARGB32.blue(i) * 0.4);
            return FastColor.ARGB32.color(0, j, k, l);
        }
    }

    public static SignRenderer.SignModel createSignModel(EntityModelSet p_173647_, WoodType p_173648_) {
        return new SignRenderer.SignModel(p_173647_.bakeLayer(ModelLayers.createSignModelName(p_173648_)));
    }

    public static LayerDefinition createSignLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox(SignBlockEntity blockEntity) {
        if (blockEntity.getBlockState().getBlock() instanceof StandingSignBlock) {
            net.minecraft.core.BlockPos pos = blockEntity.getBlockPos();
            return new net.minecraft.world.phys.AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.125, pos.getZ() + 1.0);
        }
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }

    @OnlyIn(Dist.CLIENT)
    public static final class SignModel extends Model {
        public final ModelPart root;
        public final ModelPart stick;

        public SignModel(ModelPart p_173657_) {
            super(RenderType::entityCutoutNoCull);
            this.root = p_173657_;
            this.stick = p_173657_.getChild("stick");
        }

        @Override
        public void renderToBuffer(PoseStack p_112510_, VertexConsumer p_112511_, int p_112512_, int p_112513_, int p_350294_) {
            this.root.render(p_112510_, p_112511_, p_112512_, p_112513_, p_350294_);
        }
    }
}

package fr.madu59.obe.client.renderer.blockentity.banner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class OBEBannerRenderer extends BannerRenderer{

    public static EntityModelSet entityModelSet;

    public OBEBannerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        entityModelSet = context.getModelSet();
    }

    @Override
    public void render(BannerBlockEntity bannerBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        BlockState blockState = bannerBlockEntity.getBlockState();
        BannerModel bannerModel;
        BannerFlagModel bannerFlagModel;
        float g;
        if (blockState.getBlock() instanceof BannerBlock) {
            g = -RotationSegment.convertToDegrees(blockState.getValue(BannerBlock.ROTATION));
            bannerModel = this.standingModel;
            bannerFlagModel = this.standingFlagModel;
        } else {
            g = -(blockState.getValue(WallBannerBlock.FACING)).toYRot();
            bannerModel = this.wallModel;
            bannerFlagModel = this.wallFlagModel;
        }

        long l = bannerBlockEntity.getLevel().getGameTime();
        BlockPos blockPos = bannerBlockEntity.getBlockPos();
        float h = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + f) / 100.0F;
        OBEBannerRenderer.renderBanner(poseStack, multiBufferSource, i, j, g, bannerModel, bannerFlagModel, h, bannerBlockEntity.getBaseColor(), bannerBlockEntity.getPatterns(), bannerBlockEntity);
    }

    private static void renderBanner(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, float f, BannerModel bannerModel, BannerFlagModel bannerFlagModel, float g, DyeColor dyeColor, BannerPatternLayers bannerPatternLayers, BlockEntity be) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(f));
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        if(RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_BANNERS.getValue(), be)) bannerModel.renderToBuffer(poseStack, ModelBakery.BANNER_BASE.buffer(multiBufferSource, RenderType::entitySolid), i, j);
        bannerFlagModel.setupAnim(g);
        renderPatterns(poseStack, multiBufferSource, i, j, bannerFlagModel.root(), ModelBakery.BANNER_BASE, true, dyeColor, bannerPatternLayers);
        poseStack.popPose();
    }
}
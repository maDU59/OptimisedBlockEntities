package fr.madu59.obe.client.renderer.blockentity.banner;


import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class OBEBannerRenderer extends BannerRenderer{

    public static EntityModelSet entityModelSet;

    public OBEBannerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        entityModelSet = context.entityModelSet();
    }

    @Override
    public void submit(final BannerRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        BannerModel bannerModel;
        BannerFlagModel bannerFlagModel;
        if (state.standing) {
            bannerModel = this.standingModel;
            bannerFlagModel = this.standingFlagModel;
        } else {
            bannerModel = this.wallModel;
            bannerFlagModel = this.wallFlagModel;
        }
        OBEBannerRenderer.submitBanner(this.materials, poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.angle, bannerModel, bannerFlagModel, state.phase, state.baseColor, state.patterns, state.breakProgress, 0);
    }

   private static void submitBanner(MaterialSet materialSet, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, int j, float f, BannerModel bannerModel, BannerFlagModel bannerFlagModel, float g, DyeColor dyeColor, BannerPatternLayers bannerPatternLayers, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, int k) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(f));
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);    
        Material material = ModelBakery.BANNER_BASE;
        if(!SettingsManager.OPTIMISED_BANNERS.getValue()) submitNodeCollector.submitModel(bannerModel, Unit.INSTANCE, poseStack, material.renderType(RenderType::entitySolid), i, j, -1, materialSet.get(material), k, crumblingOverlay);
        submitPatterns(materialSet, poseStack, submitNodeCollector, i, j, bannerFlagModel, g, material, true, dyeColor, bannerPatternLayers, false, crumblingOverlay, k);
        poseStack.popPose();
    }
}
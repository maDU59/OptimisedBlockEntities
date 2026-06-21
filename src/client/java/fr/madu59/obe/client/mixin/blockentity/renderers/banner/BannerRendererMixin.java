package fr.madu59.obe.client.mixin.blockentity.renderers.banner;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

@Mixin(BannerRenderer.class)
public abstract class BannerRendererMixin {

    @Shadow
    public MaterialSet materials;

    @Shadow
    public BannerModel standingModel;
    
    @Shadow
    public BannerModel wallModel;
    
    @Shadow
    public BannerFlagModel standingFlagModel;
    
    @Shadow
    public BannerFlagModel wallFlagModel;

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(final BannerRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera, CallbackInfo ci){
        if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_BANNERS.getValue(), state)) {
            ci.cancel();
            BannerModel bannerModel;
            BannerFlagModel bannerFlagModel;
            if (state.standing) {
                bannerModel = this.standingModel;
                bannerFlagModel = this.standingFlagModel;
            } else {
                bannerModel = this.wallModel;
                bannerFlagModel = this.wallFlagModel;
            }

            obe$submitBanner(this.materials, poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.angle, bannerModel, bannerFlagModel, state.phase, state.baseColor, state.patterns, state.breakProgress, 0);
        }
    }

    @Unique
    private static void obe$submitBanner(MaterialSet materialSet, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, int j, float f, BannerModel bannerModel, BannerFlagModel bannerFlagModel, float g, DyeColor dyeColor, BannerPatternLayers bannerPatternLayers, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, int k) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(f));
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        Material material = ModelBakery.BANNER_BASE;
        // submitNodeCollector.submitModel(bannerModel, Unit.INSTANCE, poseStack, material.renderType(RenderTypes::entitySolid), i, j, -1, materialSet.get(material), k, crumblingOverlay);
        BannerRenderer.submitPatterns(materialSet, poseStack, submitNodeCollector, i, j, bannerFlagModel, g, material, true, dyeColor, bannerPatternLayers, false, crumblingOverlay, k);
        poseStack.popPose();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void obe$cancelExtract(CallbackInfo ci, @Local BannerRenderState state, @Local BannerBlockEntity be){
        ((BlockEntityRenderStateExt)state).blockEntity(be);
    }
}

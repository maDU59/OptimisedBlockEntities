package fr.madu59.obe.client.renderer.blockentity.banner;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.blockentity.state.BellRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class OBEBannerRenderer extends BannerRenderer{

    public static EntityModelSet entityModelSet;

    public OBEBannerRenderer(final BlockEntityRendererProvider.Context context) {
        entityModelSet = context.entityModelSet();
        super(context);
    }

    @Override
    public void submit(final BannerRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(state.transformation);
        OBEBannerRenderer.submitBanner(this.sprites, poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, this.bannerModel(state.attachmentType), this.flagModel(state.attachmentType), state.phase, state.baseColor, state.patterns, state.breakProgress, 0, state);
        poseStack.popPose();
    }

    public static void submitBanner(final SpriteGetter sprites, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final BannerModel model, final BannerFlagModel flagModel, final float phase, final DyeColor baseColor, final BannerPatternLayers patterns, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final int outlineColor, BlockEntityRenderState state) {
        SpriteId sprite = Sheets.BANNER_BASE;
        if(RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_BANNERS.getValue(), state)) submitNodeCollector.submitModel(model, Unit.INSTANCE, poseStack, lightCoords, overlayCoords, -1, sprite, sprites, outlineColor, breakProgress);
        submitNodeCollector.submitModel(flagModel, phase, poseStack, lightCoords, overlayCoords, -1, sprite, sprites, outlineColor, breakProgress);
        submitPatterns(sprites, poseStack, submitNodeCollector, lightCoords, overlayCoords, flagModel, phase, true, baseColor, patterns, breakProgress);
    }

    @Override
    public void extractRenderState(final BannerBlockEntity blockEntity, final BannerRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        ((BlockEntityRenderStateExt)state).blockEntity(blockEntity);
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    }
}
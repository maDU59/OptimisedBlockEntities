package fr.madu59.obe.client.mixin.blockentity.renderers.banner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;

@Mixin(BannerRenderer.class)
public abstract class BannerRendererMixin {

    @Shadow
    public BannerModel standingModel;
    
    @Shadow
    public BannerModel wallModel;
    
    @Shadow
    public BannerFlagModel standingFlagModel;
    
    @Shadow
    public BannerFlagModel wallFlagModel;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(BannerBlockEntity bannerBlockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Vec3 vec3, CallbackInfo ci){
        if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_BANNERS.getValue(), bannerBlockEntity)) {
            ci.cancel();

            BlockState blockState = bannerBlockEntity.getBlockState();
            BannerModel bannerModel;
            BannerFlagModel bannerFlagModel;
            float g;
            if (blockState.getBlock() instanceof BannerBlock) {
                g = -RotationSegment.convertToDegrees((Integer)blockState.getValue(BannerBlock.ROTATION));
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
            obe$renderBanner(poseStack, multiBufferSource, i, j, g, bannerModel, bannerFlagModel, h, bannerBlockEntity.getBaseColor(), bannerBlockEntity.getPatterns());
        }
        RenderModeManager.updateOnRender(bannerBlockEntity);
    }

    @Unique
    private static void obe$renderBanner(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, float f, BannerModel bannerModel, BannerFlagModel bannerFlagModel, float g, DyeColor dyeColor, BannerPatternLayers bannerPatternLayers) {
      poseStack.pushPose();
      poseStack.translate(0.5F, 0.0F, 0.5F);
      poseStack.mulPose(Axis.YP.rotationDegrees(f));
      poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
    //   bannerModel.renderToBuffer(poseStack, ModelBakery.BANNER_BASE.buffer(multiBufferSource, RenderType::entitySolid), i, j);
      bannerFlagModel.setupAnim(g);
      BannerRenderer.renderPatterns(poseStack, multiBufferSource, i, j, bannerFlagModel.root(), ModelBakery.BANNER_BASE, true, dyeColor, bannerPatternLayers);
      poseStack.popPose();
   }
}

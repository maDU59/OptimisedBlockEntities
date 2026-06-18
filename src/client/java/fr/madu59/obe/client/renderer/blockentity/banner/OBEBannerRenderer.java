package fr.madu59.obe.client.renderer.blockentity.banner;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
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
        List<Pair<Holder<BannerPattern>, DyeColor>> list = bannerBlockEntity.getPatterns();
        float g = 0.6666667F;
        boolean bl = bannerBlockEntity.getLevel() == null;
        poseStack.pushPose();
        long l;
        if (bl) {
            l = 0L;
            poseStack.translate(0.5F, 0.5F, 0.5F);
            this.pole.visible = true;
        } else {
            l = bannerBlockEntity.getLevel().getGameTime();
            BlockState blockState = bannerBlockEntity.getBlockState();
            if (blockState.getBlock() instanceof BannerBlock) {
                poseStack.translate(0.5F, 0.5F, 0.5F);
                float h = -RotationSegment.convertToDegrees((Integer)blockState.getValue(BannerBlock.ROTATION));
                poseStack.mulPose(Axis.YP.rotationDegrees(h));
                this.pole.visible = true;
            } else {
                poseStack.translate(0.5F, -0.16666667F, 0.5F);
                float h = -(blockState.getValue(WallBannerBlock.FACING)).toYRot();
                poseStack.mulPose(Axis.YP.rotationDegrees(h));
                poseStack.translate(0.0F, -0.3125F, -0.4375F);
                this.pole.visible = false;
            }
        }

        poseStack.pushPose();
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        VertexConsumer vertexConsumer = ModelBakery.BANNER_BASE.buffer(multiBufferSource, RenderType::entitySolid);
        if(RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_BANNERS.getValue(), bannerBlockEntity)){
            this.pole.render(poseStack, vertexConsumer, i, j);
            this.bar.render(poseStack, vertexConsumer, i, j);
        }
        BlockPos blockPos = bannerBlockEntity.getBlockPos();
        float k = ((float)Math.floorMod((long)(blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + l, 100L) + f) / 100.0F;
        this.flag.xRot = (-0.0125F + 0.01F * Mth.cos(((float)Math.PI * 2F) * k)) * (float)Math.PI;
        this.flag.y = -32.0F;
        renderPatterns(poseStack, multiBufferSource, i, j, this.flag, ModelBakery.BANNER_BASE, true, list);
        poseStack.popPose();
        poseStack.popPose();
    }
}
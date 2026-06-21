package fr.madu59.obe.renderer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.config.SettingsManager;
import fr.madu59.obe.model.BlockEntityStateModel;
import fr.madu59.obe.model.CompositeBlockStateModel;
import fr.madu59.obe.registry.MaterialGetter;
import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.util.ResourceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.client.resources.model.BakedModel;

public class OBEBlockRenderer {

    public OBEBlockRenderer(){}

    public @Nullable BakedModel getModel(BlockState state, BlockPos pos, long seed, BakedModel originalModel){
        if (!RenderModeManager.hasBlockEntity(state)) return null;

        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (be == null) return null;

        BlockEntityExt ext = (BlockEntityExt)be;
        if (ext == null || !ext.isSupportedBlockEntity() || !ext.hasSpecialRenderer()) return null;

        Block block = state.getBlock();
        RandomSource random = RandomSource.create(seed);

        if(originalModel == null){
            originalModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
        }

        if(be.getType() == BlockEntityType.BELL && ext.renderMode() == RenderMode.TERRAIN){
            return getBellModel(state, random, originalModel);
        }

        return null;
    }

    public BakedModel getStandingSignModel(BlockState state, RandomSource random, BakedModel originalModel){
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        SignBlock block = (SignBlock) state.getBlock();

        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        final ModelLayerLocation layerLocation = ResourceUtil.getSignLayerLocation(state, woodType);

        poseStack.translate(0.5F, 0.75F * 0.6666667F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-block.getYRotationDegrees(state)));
        if (!(block instanceof StandingSignBlock)) {
            poseStack.translate(0.0F, -0.3125F, -0.4375F);
        }

        float f = 0.6666667F;
        poseStack.scale(f, -f, -f);

        BakedModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "sign"), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());

        return model;
    }

    public BakedModel getHangingSignModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        SignBlock block = (SignBlock) state.getBlock();

        WoodType woodType = ((SignBlock) state.getBlock()).type();
        ModelLayerLocation layerLocation = ResourceUtil.getHangingSignLayerLocation(state, woodType);

        poseStack.translate((double)0.5F, (double)0.9375F, (double)0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-block.getYRotationDegrees(state)));
        poseStack.translate(0.0F, -0.3125F, 0.0F);

        float f = 1f;
        poseStack.scale(f, -f, -f);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "sign"), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getSkullBlockModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        Block block = state.getBlock();
        boolean bl = block instanceof WallSkullBlock;
        Direction direction = bl ? (Direction)state.getValue(WallSkullBlock.FACING) : null;
        if (direction == null) {
            poseStack.translate(0.5F, 0.0F, 0.5F);
        } else {
            float h = 0.25F;
            poseStack.translate(0.5F - (float)direction.getStepX() * 0.25F, 0.25F, 0.5F - (float)direction.getStepZ() * 0.25F);
        }

        int i = bl ? RotationSegment.convertToSegment(direction.getOpposite()) : state.getValue(SkullBlock.ROTATION);
        poseStack.mulPose(Axis.YP.rotationDegrees(-RotationSegment.convertToDegrees(i)));

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        if(type == SkullBlock.Types.DRAGON){
            poseStack.translate(0.0F, -0.374375F, 0.0F);
            poseStack.scale(0.75F, 0.75F, 0.75F);
        }
        
        ModelLayerLocation layerLocation = ResourceUtil.getSkullBlockLayerLocation(state, type);
        if(layerLocation == null) return new BlockEntityStateModel();

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "skull"), state, poseStack, SettingsManager.SKULL_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getBedModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getBedLayerLocation(state);

        Direction facing = state.getValue(BedBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5625F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F + facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bed"), state, poseStack, SettingsManager.BED_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getChestModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getChestLayerLocation(state);

        Direction facing = state.getValue(ChestBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        BakedModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "chest"), state, poseStack, SettingsManager.CHEST_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());

        return model;
    }

    public BakedModel getBellModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getBellLayerLocation(state);

        BakedModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bell"), state, poseStack, SettingsManager.BELL_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
        model = new CompositeBlockStateModel(model, Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state));
        ResourceUtil.cache(layerLocation, state, model);
        return model;
    }

    public BakedModel getBannerModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        Block block = state.getBlock();
        boolean isWall = block instanceof WallBannerBlock;
        
        ModelLayerLocation layerLocation = ResourceUtil.getBannerLayerLocation(state);

        poseStack.pushPose();
        if (!isWall) {
            float angle = -RotationSegment.convertToDegrees(state.getValue(BannerBlock.ROTATION));
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        } else {
            float angle = -(state.getValue(WallBannerBlock.FACING)).toYRot();
            poseStack.translate(0.5F, -0.16666667F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.translate(0.0F, -0.3125F, -0.4375F);
        }
        poseStack.pushPose();
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "banner"), state, poseStack, SettingsManager.BANNER_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getShulkerBoxModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getShulkerBoxLayerLocation(state);
        if(layerLocation == null) return new BlockEntityStateModel();

        Direction facing = state.getValue(ShulkerBoxBlock.FACING);
        poseStack.translate(0.5F, 0.5F, 0.5F);
        float g = 0.9995F;
        poseStack.scale(0.9995F, 0.9995F, 0.9995F);
        poseStack.mulPose(facing.getRotation());
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "shulker_box"), state, poseStack, SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getDecoratedPotModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getDecoratedPotLayerLocation(state, true);

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        poseStack.pushPose();
        poseStack.translate((double)0.5F, (double)0.0F, (double)0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        poseStack.translate((double)-0.5F, (double)0.0F, (double)-0.5F);

        BakedModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "decorated_pot"), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());

        layerLocation = ResourceUtil.getDecoratedPotLayerLocation(state, false);

        BakedModel sideModel = ResourceUtil.getSubModel(layerLocation, Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BLANK).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
        
        model = new CompositeBlockStateModel(model, sideModel);

        ResourceUtil.cache(layerLocation, state, model);

        return model;
    }
}

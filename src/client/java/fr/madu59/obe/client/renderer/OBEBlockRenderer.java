package fr.madu59.obe.client.renderer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.CopperGolemStatueBlockRenderer;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.PlainSignBlock.Attachment;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WoodType;

public class OBEBlockRenderer {

    public OBEBlockRenderer(){}

    public @Nullable BlockStateModel getModel(BlockState state, BlockPos pos, long seed){
        if (!state.hasBlockEntity()) return null;

        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (be == null) return null;

        BlockEntityExt ext = (BlockEntityExt)be;
        if (ext == null || !ext.isSupportedBlockEntity() || !ext.hasSpecialRenderer()) return null;

        RandomSource random = RandomSource.create(seed);

        if(be.getType() == BlockEntityType.BELL && ext.renderMode() == RenderMode.TERRAIN){
            return getBellModel(state, random);
        }

        return null;
    }

    public BlockStateModel getStandingSignModel(BlockState state, RandomSource random){
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        boolean isWallSign = PlainSignBlock.getAttachmentPoint(state) == Attachment.WALL;

        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        final ModelLayerLocation layerLocation = ResourceUtil.getSignLayerLocation(state, isWallSign, woodType);

        if (isWallSign) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.mulPose(StandingSignRenderer.TRANSFORMATIONS.wallTransformation(facing).body());
        }
        else {
            int rotationSegment = state.getValue(BlockStateProperties.ROTATION_16);
            poseStack.mulPose(StandingSignRenderer.TRANSFORMATIONS.freeTransformations(rotationSegment).body());
        }

        BlockStateModel model = ResourceUtil.getModel(layerLocation, Sheets.getSignSprite(woodType).texture(), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue());

        return model;
    }

    public BlockStateModel getHangingSignModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        boolean isWall = !state.hasProperty(CeilingHangingSignBlock.ATTACHED);
        WoodType woodType = ((SignBlock) state.getBlock()).type();

        ModelLayerLocation layerLocation = ResourceUtil.getHangingSignLayerLocation(state, HangingSignBlock.getAttachmentPoint(state), woodType);

        if (!isWall) {
            int rotationSegment = state.getValue(BlockStateProperties.ROTATION_16);
            poseStack.mulPose(HangingSignRenderer.TRANSFORMATIONS.freeTransformations(rotationSegment).body());
        }
        else {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.mulPose(HangingSignRenderer.TRANSFORMATIONS.wallTransformation(facing).body());
        }

        return ResourceUtil.getModel(layerLocation, Sheets.getHangingSignSprite(woodType).texture(), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getSkullBlockModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        
        ModelLayerLocation layerLocation = ResourceUtil.getSkullBlockLayerLocation(state, type);

        if (state.getBlock() instanceof WallSkullBlock) {
            Direction facing = state.getValue(WallSkullBlock.FACING);
            poseStack.mulPose(SkullBlockRenderer.TRANSFORMATIONS.wallTransformation(facing));
        } else {
            poseStack.mulPose(SkullBlockRenderer.TRANSFORMATIONS.freeTransformations(state.getValue(SkullBlock.ROTATION)));
        }
        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "skull"), state, poseStack, SettingsManager.SKULL_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getBedModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getBedLayerLocation(state);

        Direction facing = state.getValue(BedBlock.FACING);
        poseStack.mulPose(BedRenderer.modelTransform(facing));

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bed"), state, poseStack, SettingsManager.BED_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getChestModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getChestLayerLocation(state);

        Direction facing = state.getValue(ChestBlock.FACING);
        poseStack.mulPose(ChestRenderer.modelTransformation(facing));
        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "chest"), state, poseStack, SettingsManager.CHEST_AMBIENT_OCCLUSION.getValue());

        return model;
    }

    public BlockStateModel getBellModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getBellLayerLocation(state);

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bell"), state, poseStack, SettingsManager.BELL_AMBIENT_OCCLUSION.getValue());
        model = new CompositeBlockStateModel(model, Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state));
        ResourceUtil.cache(layerLocation, state, model);
        return model;
    }

    public BlockStateModel getBannerModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        Block block = state.getBlock();
        boolean isWall = block instanceof WallBannerBlock;
        
        ModelLayerLocation layerLocation = ResourceUtil.getBannerLayerLocation(state, isWall);
        if (isWall) {
            Direction facing = state.getValue(WallBannerBlock.FACING);
            poseStack.mulPose(BannerRenderer.TRANSFORMATIONS.wallTransformation(facing));
        } else {
            poseStack.mulPose(BannerRenderer.TRANSFORMATIONS.freeTransformations(state.getValue(BannerBlock.ROTATION)));
        }

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "banner"), state, poseStack, SettingsManager.BANNER_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getCopperGolemStatueModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getCopperGolemStatueLayerLocation(state);

        Direction facing = state.getValue(CopperGolemStatueBlock.FACING);
        poseStack.mulPose(CopperGolemStatueBlockRenderer.modelTransformation(facing));

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "copper_golem_statue"), state, poseStack, SettingsManager.COPPER_GOLEM_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getShulkerBoxModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getShulkerBoxLayerLocation(state);

        Direction facing = state.getValue(ShulkerBoxBlock.FACING);
        poseStack.mulPose(ShulkerBoxRenderer.modelTransform(facing));

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "shulker_box"), state, poseStack, SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getDecoratedPotModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getDecoratedPotLayerLocation(state, true);

        Direction facing = state.getValue(DecoratedPotBlock.HORIZONTAL_FACING);
        poseStack.mulPose(DecoratedPotRenderer.modelTransformation(facing));

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "decorated_pot"), state, poseStack, true);

        layerLocation = ResourceUtil.getDecoratedPotLayerLocation(state, false);

        BlockStateModel sideModel = ResourceUtil.getSubModel(layerLocation, Sheets.getDecoratedPotSprite(DecoratedPotPatterns.BLANK).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue());
        
        model = new CompositeBlockStateModel(model, sideModel);

        ResourceUtil.cache(layerLocation, state, model);

        return model;
    }
}

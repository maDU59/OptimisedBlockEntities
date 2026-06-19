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
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.golem.CopperGolemOxidationLevels;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.RotationSegment;
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

        Block block = state.getBlock();
        RandomSource random = RandomSource.create(seed);

        if(be.getType() == BlockEntityType.BELL && ext.renderMode() == RenderMode.TERRAIN){
            return getBellModel(state, random);
        }

        return null;
    }

    public BlockStateModel getStandingSignModel(BlockState state, RandomSource random){
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        SignBlock block = (SignBlock) state.getBlock();
        boolean isWallSign = block instanceof WallSignBlock;

        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        final ModelLayerLocation layerLocation = ResourceUtil.getSignLayerLocation(state, isWallSign, woodType);

        SignRenderer.translateBase(poseStack, -block.getYRotationDegrees(state));
        if (isWallSign) {
            poseStack.translate(0.0F, -0.3125F, -0.4375F);
        }

        float f = 0.6666667F;
        poseStack.scale(f, -f, -f);

        BlockStateModel model = ResourceUtil.getModel(layerLocation, Sheets.getSignMaterial(woodType).texture(), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue());

        return model;
    }

    public BlockStateModel getHangingSignModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        SignBlock block = (SignBlock) state.getBlock();

        ModelLayerLocation layerLocation = ResourceUtil.getHangingSignLayerLocation(state, HangingSignRenderer.AttachmentType.byBlockState(state));

        WoodType woodType = ((SignBlock) state.getBlock()).type();

        HangingSignRenderer.translateBase(poseStack, -block.getYRotationDegrees(state));

        float f = 1f;
        poseStack.scale(f, -f, -f);

        return ResourceUtil.getModel(layerLocation, Sheets.getHangingSignMaterial(woodType).texture(), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getSkullBlockModel(BlockState state, RandomSource random) {
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
        
        ModelLayerLocation layerLocation = ResourceUtil.getSkullBlockLayerLocation(state, type);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "skull"), state, poseStack, SettingsManager.SKULL_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getBedModel(BlockState state, RandomSource random) {
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

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bed"), state, poseStack, SettingsManager.BED_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getChestModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getChestLayerLocation(state);

        Direction facing = state.getValue(ChestBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "chest"), state, poseStack, SettingsManager.CHEST_AMBIENT_OCCLUSION.getValue());

        return model;
    }

    public BlockStateModel getBellModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getBellLayerLocation(state);

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bell"), state, poseStack, SettingsManager.BELL_AMBIENT_OCCLUSION.getValue());
        model = new CompositeBlockStateModel(model, Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state));
        ResourceUtil.cache(layerLocation, state, model);
        return model;
    }

    public BlockStateModel getBannerModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        Block block = state.getBlock();
        boolean isWall = block instanceof WallBannerBlock;
        
        ModelLayerLocation layerLocation = ResourceUtil.getBannerLayerLocation(state, isWall);

        float angle;
        if (!isWall) {
            angle = -RotationSegment.convertToDegrees(state.getValue(BannerBlock.ROTATION));
        } else {
            angle = -(state.getValue(WallBannerBlock.FACING)).toYRot();
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "banner"), state, poseStack, SettingsManager.BANNER_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getCopperGolemStatueModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getCopperGolemStatueLayerLocation(state);

        Direction facing = state.getValue(CopperGolemStatueBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));

        WeatherState oxydationLevel;
        if (state.getBlock() instanceof CopperGolemStatueBlock copperGolemStatueBlock) {
            oxydationLevel = copperGolemStatueBlock.getWeatheringState();
        } else {
            oxydationLevel = WeatherState.UNAFFECTED;
        }

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "copper_golem_statue"), state, poseStack, SettingsManager.COPPER_GOLEM_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getShulkerBoxModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getShulkerBoxLayerLocation(state);

        Direction facing = state.getValue(ShulkerBoxBlock.FACING);
        poseStack.translate(0.5F, 0.5F, 0.5F);
        float g = 0.9995F;
        poseStack.scale(0.9995F, 0.9995F, 0.9995F);
        poseStack.mulPose(facing.getRotation());
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "shulker_box"), state, poseStack, SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getDecoratedPotModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getDecoratedPotLayerLocation(state, true);

        Direction facing = state.getValue(DecoratedPotBlock.HORIZONTAL_FACING);
        poseStack.pushPose();
        poseStack.translate((double)0.5F, (double)0.0F, (double)0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        poseStack.translate((double)-0.5F, (double)0.0F, (double)-0.5F);

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "decorated_pot"), state, poseStack, true);

        layerLocation = ResourceUtil.getDecoratedPotLayerLocation(state, false);

        BlockStateModel sideModel = ResourceUtil.getSubModel(layerLocation, Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BLANK).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue());
        
        model = new CompositeBlockStateModel(model, sideModel);

        ResourceUtil.cache(layerLocation, state, model);

        return model;
    }
}

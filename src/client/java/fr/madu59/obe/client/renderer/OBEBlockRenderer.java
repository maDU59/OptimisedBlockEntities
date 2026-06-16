package fr.madu59.obe.client.renderer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.model.MaterialResolver;
import fr.madu59.obe.client.renderer.blockentity.banner.OBEBannerRenderer;
import fr.madu59.obe.client.renderer.blockentity.bed.OBEBedRenderer;
import fr.madu59.obe.client.renderer.blockentity.bell.OBEBellRenderer;
import fr.madu59.obe.client.renderer.blockentity.chest.OBEChestRenderer;
import fr.madu59.obe.client.renderer.blockentity.coppergolemstatues.OBECopperGolemStatueBlockRenderer;
import fr.madu59.obe.client.renderer.blockentity.decoratedpot.OBEDecoratedPotRenderer;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.renderer.blockentity.shulkerbox.OBEShulkerBoxRenderer;
import fr.madu59.obe.client.renderer.blockentity.sign.OBEHangingSignRenderer;
import fr.madu59.obe.client.renderer.blockentity.sign.OBEStandingSignRenderer;
import fr.madu59.obe.client.renderer.blockentity.skull.OBESkullBlockRenderer;
import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.golem.CopperGolemOxidationLevels;
import net.minecraft.world.item.DyeColor;
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
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class OBEBlockRenderer {

    private static final boolean xmasTexture = OBEChestRenderer.xmasTextures();

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
        boolean isWallSign = PlainSignBlock.getAttachmentPoint(state) == Attachment.WALL;

        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        final ModelLayerLocation layerLocation = ResourceUtil.getSignLayerLocation(state, isWallSign, woodType);

        if (isWallSign) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.mulPose(OBEStandingSignRenderer.TRANSFORMATIONS.wallTransformation(facing).body());
        }
        else {
            int rotationSegment = state.getValue(BlockStateProperties.ROTATION_16);
            poseStack.mulPose(OBEStandingSignRenderer.TRANSFORMATIONS.freeTransformations(rotationSegment).body());
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
            poseStack.mulPose(OBEHangingSignRenderer.TRANSFORMATIONS.freeTransformations(rotationSegment).body());
        }
        else {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            poseStack.mulPose(OBEHangingSignRenderer.TRANSFORMATIONS.wallTransformation(facing).body());
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
            poseStack.mulPose(OBESkullBlockRenderer.TRANSFORMATIONS.wallTransformation(facing));
        } else {
            poseStack.mulPose(OBESkullBlockRenderer.TRANSFORMATIONS.freeTransformations(state.getValue(SkullBlock.ROTATION)));
        }
        return  ResourceUtil.getModel(layerLocation, MaterialResolver.getSkullBlockMaterial(type).texture(), state, poseStack, SettingsManager.SKULL_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getBedModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        BedBlock block = (BedBlock) state.getBlock();
        
        ModelLayerLocation layerLocation = ResourceUtil.getBedLayerLocation(state);

        Direction facing = state.getValue(BedBlock.FACING);
        poseStack.mulPose(OBEBedRenderer.modelTransform(facing));

        return ResourceUtil.getModel(layerLocation, Sheets.getBedSprite(block.getColor()).texture(), state, poseStack, SettingsManager.BED_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getChestModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        Block block = state.getBlock();
        
        ModelLayerLocation layerLocation = ResourceUtil.getChestLayerLocation(state);

        Direction facing = state.getValue(ChestBlock.FACING);
        poseStack.mulPose(OBEChestRenderer.modelTransformation(facing));

        ChestType type = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);
        BlockStateModel model = ResourceUtil.getModel(layerLocation, Sheets.chooseSprite(OBEChestRenderer.getChestMaterial(block, xmasTexture), type).texture(), state, poseStack, SettingsManager.CHEST_AMBIENT_OCCLUSION.getValue());

        return model;
    }

    public BlockStateModel getBellModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getBellLayerLocation(state);

        BlockStateModel model = ResourceUtil.getModel(layerLocation, OBEBellRenderer.BELL_TEXTURE.texture(), state, poseStack, SettingsManager.BELL_AMBIENT_OCCLUSION.getValue());
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
            poseStack.mulPose(OBEBannerRenderer.TRANSFORMATIONS.wallTransformation(facing));
        } else {
            poseStack.mulPose(OBEBannerRenderer.TRANSFORMATIONS.freeTransformations(state.getValue(BannerBlock.ROTATION)));
        }

        return ResourceUtil.getModel(layerLocation, Sheets.BANNER_BASE.texture(), state, poseStack, SettingsManager.BANNER_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getCopperGolemStatueModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getCopperGolemStatueLayerLocation(state);

        Direction facing = state.getValue(CopperGolemStatueBlock.FACING);
        poseStack.mulPose(OBECopperGolemStatueBlockRenderer.modelTransformation(facing));

        WeatherState oxydationLevel;
        if (state.getBlock() instanceof CopperGolemStatueBlock copperGolemStatueBlock) {
            oxydationLevel = copperGolemStatueBlock.getWeatheringState();
        } else {
            oxydationLevel = WeatherState.UNAFFECTED;
        }

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        Identifier identifier = CopperGolemOxidationLevels.getOxidationLevel(oxydationLevel).texture();

        return ResourceUtil.getModel(layerLocation, MaterialResolver.entityTextureFormatter(identifier).texture(), state, poseStack, SettingsManager.COPPER_GOLEM_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getShulkerBoxModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getShulkerBoxLayerLocation(state);

        Direction facing = state.getValue(ShulkerBoxBlock.FACING);
        poseStack.mulPose(OBEShulkerBoxRenderer.modelTransform(facing));

        ShulkerBoxBlock block = (ShulkerBoxBlock) state.getBlock();
        
        DyeColor color = block.getColor();
        SpriteId sprite;
        if (color == null) {
            sprite = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
        } else {
            sprite = Sheets.getShulkerBoxSprite(color);
        }

        return ResourceUtil.getModel(layerLocation, sprite.texture(), state, poseStack, SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION.getValue());
    }

    public BlockStateModel getDecoratedPotModel(BlockState state, RandomSource random) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ResourceUtil.getDecoratedPotLayerLocation(state, true);

        Direction facing = state.getValue(DecoratedPotBlock.HORIZONTAL_FACING);
        poseStack.mulPose(OBEDecoratedPotRenderer.modelTransformation(facing));

        BlockStateModel model = ResourceUtil.getModel(layerLocation, Sheets.DECORATED_POT_BASE.texture(), state, poseStack, true);

        layerLocation = ResourceUtil.getDecoratedPotLayerLocation(state, false);

        BlockStateModel sideModel = ResourceUtil.getSubModel(layerLocation, Sheets.getDecoratedPotSprite(DecoratedPotPatterns.BLANK).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue());
        
        model = new CompositeBlockStateModel(model, sideModel);

        ResourceUtil.cache(layerLocation, state, model);

        return model;
    }
}

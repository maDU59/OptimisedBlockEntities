package fr.madu59.obe.client.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.OBEClient;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ResourceUtil{

    private static Map<ModelLayerLocation, BlockStateModel> modelCache = new ConcurrentHashMap<>();
    private static Map<BlockState, BlockStateModel> transformedModelCache = new ConcurrentHashMap<>();
    private static Map<ModelCacheKey, BlockStateModel> transformedSubModelCache = new ConcurrentHashMap<>();

    public static ModelLayerLocation getSignLayerLocation(BlockState state, boolean isWallSign){
        return getSignLayerLocation(state, isWallSign, WoodType.OAK);
    }

    public static ModelLayerLocation getSignLayerLocation(BlockState state, boolean isWallSign, WoodType woodType){
        if (isWallSign) {
            return ModelLayers.createWallSignModelName(woodType);
        }
        else{
            return ModelLayers.createStandingSignModelName(woodType);
        }
    }

    public static ModelLayerLocation getHangingSignLayerLocation(BlockState state, HangingSignRenderer.AttachmentType attachment){
        return getHangingSignLayerLocation(state, attachment, WoodType.OAK);
    }

    public static ModelLayerLocation getHangingSignLayerLocation(BlockState state, HangingSignRenderer.AttachmentType attachment, WoodType woodType){
        return ModelLayers.createHangingSignModelName(woodType, attachment);
    }

    public static ModelLayerLocation getSkullBlockLayerLocation(BlockState state, SkullBlock.Type type){
        if (type instanceof SkullBlock.Types vanillaType) {
            return switch (vanillaType) {
                case SKELETON -> ModelLayers.SKELETON_SKULL;
                case WITHER_SKELETON -> ModelLayers.WITHER_SKELETON_SKULL;
                case PLAYER -> ModelLayers.PLAYER_HEAD;
                case ZOMBIE -> ModelLayers.ZOMBIE_HEAD;
                case CREEPER -> ModelLayers.CREEPER_HEAD;
                case DRAGON -> ModelLayers.DRAGON_SKULL;
                case PIGLIN -> ModelLayers.PIGLIN_HEAD;
            };
        }
        else return null;
    }

    public static ModelLayerLocation getBedLayerLocation(BlockState state){
        if(state.getValue(BedBlock.PART) == BedPart.FOOT) return ModelLayers.BED_FOOT;
        else return ModelLayers.BED_HEAD;
    }

    public static ModelLayerLocation getChestLayerLocation(BlockState state){
        return switch(state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE)){
            case ChestType.SINGLE -> ModelLayers.CHEST;
            case ChestType.LEFT -> ModelLayers.DOUBLE_CHEST_LEFT;
            case ChestType.RIGHT -> ModelLayers.DOUBLE_CHEST_RIGHT;
        };
    }

    public static ModelLayerLocation getBellLayerLocation(BlockState state){
        return ModelLayers.BELL;
    }

    public static ModelLayerLocation getBannerLayerLocation(BlockState state, boolean isWall){
        return isWall? ModelLayers.WALL_BANNER : ModelLayers.STANDING_BANNER;
    }

    public static ModelLayerLocation getCopperGolemStatueLayerLocation(BlockState state){
        return switch(state.getValue(CopperGolemStatueBlock.POSE)) {
            case CopperGolemStatueBlock.Pose.STANDING-> ModelLayers.COPPER_GOLEM;
            case CopperGolemStatueBlock.Pose.RUNNING-> ModelLayers.COPPER_GOLEM_RUNNING;
            case CopperGolemStatueBlock.Pose.SITTING-> ModelLayers.COPPER_GOLEM_SITTING;
            case CopperGolemStatueBlock.Pose.STAR-> ModelLayers.COPPER_GOLEM_STAR;
        };
    }

    public static ModelLayerLocation getShulkerBoxLayerLocation(BlockState state){
        return ModelLayers.SHULKER_BOX;
    }

    public static ModelLayerLocation getDecoratedPotLayerLocation(BlockState state, boolean isCenter){
        return isCenter? ModelLayers.DECORATED_POT_BASE : ModelLayers.DECORATED_POT_SIDES;
    }

    public static TextureAtlasSprite getSprite(Identifier id) {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(id);
    }

    public static TextureAtlasSprite getBakedMaterial(TextureAtlasSprite sprite) {
        return sprite;
    }

    public static void collectParts(List<BlockModelPart> partsList, ModelLayerLocation modelLayerLocation, RandomSource random, Identifier texture, boolean useAo){
        modelCache.computeIfAbsent(modelLayerLocation, layer -> new BlockEntityStateModel(layer, texture, useAo)).collectParts(random, partsList);
    }

    public static void collectParts(List<BlockModelPart> partsList, BlockStateModel model, RandomSource random){
        model.collectParts(random, partsList);
    }

    public static BlockStateModel getModel(ModelLayerLocation modelLayerLocation, Identifier texture, boolean useAo){
        return modelCache.computeIfAbsent(modelLayerLocation, layer -> new BlockEntityStateModel(modelLayerLocation, texture, useAo));
    }

    public static BlockStateModel getModel(ModelLayerLocation modelLayerLocation, Identifier texture, BlockState blockState, PoseStack poseStack, boolean useAo){
        return transformedModelCache.computeIfAbsent(blockState, layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo));
    }

    public static BlockStateModel getSubModel(ModelLayerLocation modelLayerLocation, Identifier texture, BlockState blockState, PoseStack poseStack, boolean useAo){
        return transformedSubModelCache.computeIfAbsent(new ModelCacheKey(modelLayerLocation, blockState), layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo));
    }

    public static BlockStateModel getModel(BlockState state){
        return transformedModelCache.get(state);
    }

    public static boolean cacheContains(BlockState state){
        return transformedModelCache.containsKey(state);
    }

    public static BlockStateModel getPart(ModelLayerLocation modelLayerLocation, String name){
        if(modelCache.containsKey(modelLayerLocation)){
            return modelCache.get(modelLayerLocation);
        }
        else{
            OBEClient.debug("Requested part " + name + " does not exist");
            return new BlockEntityStateModel();
        }
    }

    public static void cache(ModelLayerLocation modelLayerLocation, BlockState blockState, BlockStateModel model){
        transformedModelCache.put(blockState, model);
    }

    public static void clearCache(){
        modelCache.clear();
        transformedModelCache.clear();
        transformedSubModelCache.clear();
    }

    public record ModelCacheKey(ModelLayerLocation modelLayerLocation, BlockState blockState) {}
}
package fr.madu59.obe.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.OBEClient;
import fr.madu59.obe.model.BlockEntityStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.client.resources.model.BakedModel;

public class ResourceUtil{

    private static Map<ModelLayerLocation, BakedModel> modelCache = new ConcurrentHashMap<>();
    private static Map<BlockState, BakedModel> transformedModelCache = new ConcurrentHashMap<>();
    private static Map<ModelCacheKey, BakedModel> transformedSubModelCache = new ConcurrentHashMap<>();

    private static ResourceLocation blockAtlas = ResourceLocation.tryParse("minecraft:textures/atlas/blocks.png");

    public static ModelLayerLocation getSignLayerLocation(BlockState state, WoodType woodType){
        return ModelLayers.createSignModelName(woodType);
    }

    public static ModelLayerLocation getHangingSignLayerLocation(BlockState state, WoodType woodType){
        return ModelLayers.createHangingSignModelName(woodType);
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
        return switch(BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE)){
            case ChestType.SINGLE -> ModelLayers.CHEST;
            case ChestType.LEFT -> ModelLayers.DOUBLE_CHEST_LEFT;
            case ChestType.RIGHT -> ModelLayers.DOUBLE_CHEST_RIGHT;
        };
    }

    public static ModelLayerLocation getBellLayerLocation(BlockState state){
        return ModelLayers.BELL;
    }

    public static ModelLayerLocation getBannerLayerLocation(BlockState state){
        return ModelLayers.BANNER;
    }

    public static ModelLayerLocation getShulkerBoxLayerLocation(BlockState state){
        return ModelLayers.SHULKER;
    }

    public static ModelLayerLocation getDecoratedPotLayerLocation(BlockState state, boolean isCenter){
        return isCenter? ModelLayers.DECORATED_POT_BASE : ModelLayers.DECORATED_POT_SIDES;
    }

    public static TextureAtlasSprite getSprite(ResourceLocation id) {
        return Minecraft.getInstance().getTextureAtlas(blockAtlas).apply(id);
    }

    public static TextureAtlasSprite getBakedMaterial(TextureAtlasSprite sprite) {
        return sprite;
    }

    public static BakedModel getModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, boolean useAo){
        return modelCache.computeIfAbsent(modelLayerLocation, layer -> new BlockEntityStateModel(modelLayerLocation, texture, useAo, null));
    }

    public static BakedModel getModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, BlockState blockState, PoseStack poseStack, boolean useAo){
        return transformedModelCache.computeIfAbsent(blockState, layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState));
    }

    public static BakedModel getSubModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, BlockState blockState, PoseStack poseStack, boolean useAo){
        return transformedSubModelCache.computeIfAbsent(new ModelCacheKey(modelLayerLocation, blockState), layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState));
    }

    public static BakedModel getModel(BlockState state){
        return transformedModelCache.get(state);
    }

    public static boolean cacheContains(BlockState state){
        return transformedModelCache.containsKey(state);
    }

    public static BakedModel getPart(ModelLayerLocation modelLayerLocation, String name){
        if(modelCache.containsKey(modelLayerLocation)){
            return modelCache.get(modelLayerLocation);
        }
        else{
            OBEClient.debug("Requested part " + name + " does not exist");
            return new BlockEntityStateModel();
        }
    }

    public static void cache(ModelLayerLocation modelLayerLocation, BlockState blockState, BakedModel model){
        transformedModelCache.put(blockState, model);
    }

    public static void clearCache(){
        modelCache.clear();
        transformedModelCache.clear();
        transformedSubModelCache.clear();
    }

    public record ModelCacheKey(ModelLayerLocation modelLayerLocation, BlockState blockState) {}
}
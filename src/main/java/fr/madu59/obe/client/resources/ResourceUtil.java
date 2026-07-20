package fr.madu59.obe.client.resources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.model.BlockEntityStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;

public class ResourceUtil{

    private static Map<BlockState, BakedModel> transformedModelCache = new ConcurrentHashMap<>();
    private static Map<SpecialModelCacheKey, BakedModel> transformedSpecialModelCache = new ConcurrentHashMap<>();
    private static Map<ModelCacheKey, BakedModel> transformedSubModelCache = new ConcurrentHashMap<>();

    private static ResourceLocation blockAtlas = ResourceLocation.tryParse("minecraft:textures/atlas/blocks.png");

    public static TextureAtlasSprite getSprite(ResourceLocation id) {
        return Minecraft.getInstance().getTextureAtlas(blockAtlas).apply(id);
    }

    public static BakedModel getModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, BlockState blockState, PoseStack poseStack, boolean useAo, TextureAtlasSprite particleMaterial){
        return transformedModelCache.computeIfAbsent(blockState, layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState, particleMaterial));
    }

    public static BakedModel getModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, BlockState blockState, Object cacheKey, PoseStack poseStack, boolean useAo, TextureAtlasSprite particleMaterial){
        return transformedSpecialModelCache.computeIfAbsent(new SpecialModelCacheKey(blockState, cacheKey), layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState, particleMaterial));
    }

    public static BakedModel getSubModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, BlockState blockState, PoseStack poseStack, boolean useAo, TextureAtlasSprite particleMaterial){
        return transformedSubModelCache.computeIfAbsent(new ModelCacheKey(modelLayerLocation, blockState), layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState, particleMaterial));
    }

    public static BakedModel getModel(BlockState state){
        return transformedModelCache.get(state);
    }

    public static BakedModel getModel(BlockState state, BlockEntity be){
        return transformedSpecialModelCache.get(new SpecialModelCacheKey(state, be));
    }

    public static boolean cacheContains(BlockState state){
        return transformedModelCache.containsKey(state);
    }

    public static boolean cacheContains(BlockState state, BlockEntity be){
        return transformedSpecialModelCache.containsKey(new SpecialModelCacheKey(state, be));
    }

    public static void cache(BlockState blockState, BakedModel model){
        transformedModelCache.put(blockState, model);
    }

    public static void cache(BlockState blockState, Object cacheKey, BakedModel model){
        transformedSpecialModelCache.put(new SpecialModelCacheKey(blockState, cacheKey), model);
    }

    public static void clearCache(){
        transformedSpecialModelCache.clear();
        transformedModelCache.clear();
        transformedSubModelCache.clear();
    }

    public static ResourceLocation entityTextureFormatter(ResourceLocation resourceLocation){
        return ResourceLocation.tryBuild(resourceLocation.getNamespace(), resourceLocation.getPath().replace(".png", "").replace("textures/", ""));
    }

    public record ModelCacheKey(ModelLayerLocation modelLayerLocation, BlockState blockState) {}
    public record SpecialModelCacheKey(BlockState blockState, Object cacheKey) {}
}
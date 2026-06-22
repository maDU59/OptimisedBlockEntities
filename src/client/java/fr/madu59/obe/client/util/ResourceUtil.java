package fr.madu59.obe.client.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.OBEClient;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;

public class ResourceUtil{

    private static Map<ModelLayerLocation, BakedModel> modelCache = new ConcurrentHashMap<>();
    private static Map<BlockState, BakedModel> transformedModelCache = new ConcurrentHashMap<>();
    private static Map<ModelCacheKey, BakedModel> transformedSubModelCache = new ConcurrentHashMap<>();

    private static ResourceLocation blockAtlas = ResourceLocation.tryParse("minecraft:textures/atlas/blocks.png");

    public static TextureAtlasSprite getSprite(ResourceLocation id) {
        return Minecraft.getInstance().getTextureAtlas(blockAtlas).apply(id);
    }

    public static TextureAtlasSprite getBakedMaterial(TextureAtlasSprite sprite) {
        return sprite;
    }

    public static BakedModel getModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, BlockState blockState, PoseStack poseStack, boolean useAo, TextureAtlasSprite particleMaterial){
        return transformedModelCache.computeIfAbsent(blockState, layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState, particleMaterial));
    }

    public static BakedModel getSubModel(ModelLayerLocation modelLayerLocation, ResourceLocation texture, BlockState blockState, PoseStack poseStack, boolean useAo, TextureAtlasSprite particleMaterial){
        return transformedSubModelCache.computeIfAbsent(new ModelCacheKey(modelLayerLocation, blockState), layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState, particleMaterial));
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

    public static ResourceLocation entityTextureFormatter(ResourceLocation resourceLocation){
        return ResourceLocation.tryBuild(resourceLocation.getNamespace(), resourceLocation.getPath().replace(".png", "").replace("textures/", ""));
    }

    public record ModelCacheKey(ModelLayerLocation modelLayerLocation, BlockState blockState) {}
}
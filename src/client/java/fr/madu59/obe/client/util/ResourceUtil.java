package fr.madu59.obe.client.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.model.BlockEntityStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResourceUtil{

    private static Map<BlockState, BlockStateModel> transformedModelCache = new ConcurrentHashMap<>();
    private static Map<SpecialModelCacheKey, BlockStateModel> transformedSpecialModelCache = new ConcurrentHashMap<>();
    private static Map<ModelCacheKey, BlockStateModel> transformedSubModelCache = new ConcurrentHashMap<>();

    public static TextureAtlasSprite getSprite(Identifier id) {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(id);
    }

    public static Material.Baked getBakedMaterial(TextureAtlasSprite sprite) {
        return new Material.Baked(sprite, false);
    }

    public static void collectParts(List<BlockStateModelPart> partsList, BlockStateModel model, RandomSource random){
        model.collectParts(random, partsList);
    }

    public static BlockStateModel getModel(ModelLayerLocation modelLayerLocation, Identifier texture, BlockState blockState, PoseStack poseStack, boolean useAo, Material.Baked particleMaterial){
        return transformedModelCache.computeIfAbsent(blockState, layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState, particleMaterial));
    }

    public static BlockStateModel getModel(ModelLayerLocation modelLayerLocation, Identifier texture, BlockState blockState, Object cacheKey, PoseStack poseStack, boolean useAo, Material.Baked particleMaterial){
        return transformedSpecialModelCache.computeIfAbsent(new SpecialModelCacheKey(blockState, cacheKey), layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState, particleMaterial));
    }

    public static BlockStateModel getSubModel(ModelLayerLocation modelLayerLocation, Identifier texture, BlockState blockState, PoseStack poseStack, boolean useAo, Material.Baked particleMaterial){
        return transformedSubModelCache.computeIfAbsent(new ModelCacheKey(modelLayerLocation, blockState), layer -> new BlockEntityStateModel(modelLayerLocation, texture, poseStack, useAo, blockState, particleMaterial));
    }

    public static BlockStateModel getModel(BlockState state){
        return transformedModelCache.get(state);
    }

    public static BlockStateModel getModel(BlockState state, BlockEntity be){
        return transformedSpecialModelCache.get(new SpecialModelCacheKey(state, be));
    }

    public static boolean cacheContains(BlockState state){
        return transformedModelCache.containsKey(state);
    }

    public static boolean cacheContains(BlockState state, BlockEntity be){
        return transformedSpecialModelCache.containsKey(new SpecialModelCacheKey(state, be));
    }

    public static void cache(BlockState blockState, BlockStateModel model){
        transformedModelCache.put(blockState, model);
    }

    public static void cache(BlockState blockState, Object cacheKey, BlockStateModel model){
        transformedSpecialModelCache.put(new SpecialModelCacheKey(blockState, cacheKey), model);
    }

    public static void clearCache(){
        transformedSpecialModelCache.clear();
        transformedModelCache.clear();
        transformedSubModelCache.clear();
    }

    public static Identifier entityTextureFormatter(Identifier identifier){
        return Identifier.tryBuild(identifier.getNamespace(), identifier.getPath().replace(".png", "").replace("textures/", ""));
    }

    public record ModelCacheKey(ModelLayerLocation modelLayerLocation, BlockState blockState) {}
    public record SpecialModelCacheKey(BlockState blockState, Object cacheKey) {}
}
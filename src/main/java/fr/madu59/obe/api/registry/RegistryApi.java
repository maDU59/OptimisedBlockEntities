package fr.madu59.obe.api.registry;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.registry.MaterialGetter;
import fr.madu59.obe.registry.ModelLayerLocationGetter;
import fr.madu59.obe.registry.Registry;
import fr.madu59.obe.registry.TransformationGetter;
import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RegistryApi {
    /*
     * Register a new group of optimised block entities
     * @param id The id of the group to register
     * @since 1.1.0
     */
    public static void registerGroup(String id){
        Registry.registerGroup(id);
    }

    /*
     * Register a new optimised block entity type inside of a group
     * @param type The block entity type to register
     * @param id The id of the group to register the block entity type in
     * @since 1.1.0
     */
    public static void registerBlockEntityType(BlockEntityType<?> type, String id){
        Registry.addBlockEntityTypeInGroup(id, type);
    }

    /*
     * Register a material provider for a group
     * @param id The group to which the material provider should be registered
     * @param provider The material provider, it must be a function accepting a BlockState and returning an ResourceLocation
     * @since 1.1.0
     */
    public static void registerMaterialProvider(String id, Function<BlockState, ResourceLocation> provider){
        MaterialGetter.registerDefault(id, provider);
    }

    /*
     * Register a material provider for a block entity type
     * @param type The block entity type to which the material provider should be registered
     * @param provider The material provider, it must be a function accepting a BlockState and returning an ResourceLocation
     * @since 1.1.0
     */
    public static void registerMaterialProvider(BlockEntityType<?> type, Function<BlockState, ResourceLocation> provider){
        MaterialGetter.register(type, provider);
    }

    /*
     * Register a block entity as supported - note that if the block entity type of this block entity has already been registered in one of the vanilla groups, this may not be needed
     * @param be The block entity to register as supported - this must be done at the block entity init
     * @since 1.1.0
     */
    public static <T extends BlockEntity> void registerSupportedBlockEntity(T be){
        ((BlockEntityExt)be).isSupportedBlockEntity(true);
    }

    /*
     * Register a transformation provider for a group
     * @param id The group to which the transformation provider should be registered
     * @param provider The transformation provider, it must be a consumer accepting a BlockState and a PoseStack
     * @since 1.1.7
     */
    public static void registerTransformationProvider(String id, BiConsumer<BlockState, PoseStack> provider){
        TransformationGetter.registerDefault(id, provider);
    }

    /*
     * Register a transformation provider for a block entity type
     * @param type The block entity type to which the transformation provider should be registered
     * @param provider The transformation provider, it must be a function accepting a BlockState and returning an ModelLayerLocation
     * @since 1.1.7
     */
    public static void registerTransformationProvider(BlockEntityType<?> type, BiConsumer<BlockState, PoseStack> provider){
        TransformationGetter.register(type, provider);
    }

    /*
     * Register a model layer location provider for a group
     * @param id The group to which the transformation provider should be registered
     * @param provider The transformation provider, it must be a function accepting a BlockState and returning an ModelLayerLocation
     * @since 1.1.7
     */
    public static void registerModelLayerLocationProvider(String id, Function<BlockState, ModelLayerLocation> provider){
        ModelLayerLocationGetter.registerDefault(id, provider);
    }

    /*
     * Register a model layer location provider for a block entity type
     * @param type The block entity type to which the model layer location provider should be registered
     * @param provider The model layer location provider, it must be a consumer accepting a BlockState and a PoseStack
     * @since 1.1.7
     */
    public static void registerModelLayerLocationProvider(BlockEntityType<?> type, Function<BlockState, ModelLayerLocation> provider){
        ModelLayerLocationGetter.register(type, provider);
    }
}

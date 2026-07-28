package fr.madu59.obe.client.api.registry;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.function.TriConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.api.model.SpecialBakedModelProvider;
import fr.madu59.obe.client.api.render.DynamicBlockEntityRenderPredicate;
import fr.madu59.obe.client.registry.SpecialBakedModelRegistry;
import fr.madu59.obe.client.registry.TransformationGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.DynamicBlockEntityRenderManager;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RegistryApi {
    /**
     * Registers a per-frame decision for whether a block entity needs its
     * original dynamic renderer. This is separate from stable compiled-list
     * skip decisions and is evaluated again whenever the renderer is invoked.
     * Missing predicates and predicate failures render normally.
     *
     * @param type block-entity type handled by the predicate
     * @param predicate stateless decision that must not retain world objects
     * @since 1.1.37
     */
    public static <T extends BlockEntity> void registerDynamicRenderPredicate(
            BlockEntityType<T> type, DynamicBlockEntityRenderPredicate<? super T> predicate) {
        DynamicBlockEntityRenderManager.register(type, predicate);
    }

    /**
     * Registers an arbitrary, potentially layered terrain-model provider for a
     * block-entity type. This API is independent from and takes precedence over
     * the legacy single-layer special-model provider.
     *
     * @param type block-entity type handled by the provider
     * @param provider provider whose appearance keys must be immutable and must
     *                 not retain world or block-entity references
     * @since 1.1.37
     */
    public static <K> void registerSpecialBakedModelProvider(BlockEntityType<?> type, SpecialBakedModelProvider<K> provider) {
        SpecialBakedModelRegistry.register(type, provider);
    }

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
        ((BlockEntityExt)be).isSupported(true);
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
     * @param provider The transformation provider, it must be a consumer accepting a BlockState and a PoseStack
     * @since 1.1.7
     */
    public static void registerTransformationProvider(BlockEntityType<?> type, BiConsumer<BlockState, PoseStack> provider){
        TransformationGetter.register(type, provider);
    }

    /*
     * Register a model layer location provider for a group
     * @param id The group to which the model layer location provider should be registered
     * @param provider The model layer location provider, it must be a function accepting a BlockState and returning a ModelLayerLocation
     * @since 1.1.7
     */
    public static void registerModelLayerLocationProvider(String id, Function<BlockState, ModelLayerLocation> provider){
        ModelLayerLocationGetter.registerDefault(id, provider);
    }

    /*
     * Register a model layer location provider for a block entity type
     * @param type The block entity type to which the model layer location provider should be registered
     * @param provider The model layer location provider, it must be a function accepting a BlockState and returning a ModelLayerLocation
     * @since 1.1.7
     */
    public static void registerModelLayerLocationProvider(BlockEntityType<?> type, Function<BlockState, ModelLayerLocation> provider){
        ModelLayerLocationGetter.register(type, provider);
    }

    /*
     * Register a special model provider for a block entity type
     * @param type The block entity type to which the model layer location provider should be registered
     * @param modelLayerLocationProvider The model layer location provider, it must be a function accepting a BlockState and returning a ModelLayerLocation
     * @param materialProvider The material provider, it must be a function accepting a BlockState and returning an ResourceLocation
     * @param transformationProvider The transformation provider, it must be a consumer accepting a BlockState and a PoseStack
     * @param cacheKeyProvider The cacheKeyProvider provider, it must be a function accepting a BlockEntity and returning an Object, it is used to differentiate different models having the same blockstate, choose something relevant
     * @since 1.1.21
     */
    public static void registerSpecialModelProvider(BlockEntityType<?> type, BiFunction<BlockState, BlockEntity, ModelLayerLocation> modelLayerLocationProvider, BiFunction<BlockState, BlockEntity, ResourceLocation> materialProvider, TriConsumer<BlockState, BlockEntity, PoseStack> transformationProvider, Function<BlockEntity, Object> cacheKeyProvider){
        SpecialModelGetter.register(type, new SpecialModelProvider(modelLayerLocationProvider, materialProvider, transformationProvider, cacheKeyProvider));
    }

    /*
     * Register a special model provider for a group
     * @param type The block entity type to which the model layer location provider should be registered
     * @param modelLayerLocationProvider The model layer location provider, it must be a function accepting a BlockState and returning a ModelLayerLocation
     * @param materialProvider The material provider, it must be a function accepting a BlockState and returning an ResourceLocation
     * @param transformationProvider The transformation provider, it must be a consumer accepting a BlockState and a PoseStack
     * @param cacheKeyProvider The cacheKeyProvider provider, it must be a function accepting a BlockEntity and returning an Object, it is used to differentiate different models having the same blockstate, choose something relevant
     * @since 1.1.21
     */
    public static void registerSpecialModelProvider(String id, BiFunction<BlockState, BlockEntity, ModelLayerLocation> modelLayerLocationProvider, BiFunction<BlockState, BlockEntity, ResourceLocation> materialProvider, TriConsumer<BlockState, BlockEntity, PoseStack> transformationProvider, Function<BlockEntity, Object> cacheKeyProvider){
        SpecialModelGetter.registerDefault(id, new SpecialModelProvider(modelLayerLocationProvider, materialProvider, transformationProvider, cacheKeyProvider));
    }
}

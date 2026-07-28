package fr.madu59.obe.client.renderer.blockentity;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.api.model.SpecialBakedModelContext;
import fr.madu59.obe.client.api.model.SpecialBakedModelProvider;
import fr.madu59.obe.client.registry.SpecialBakedModelRegistry;
import fr.madu59.obe.client.registry.SpecialBakedModelRegistry.Registration;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.TransformationGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.ResourceUtil;
import fr.madu59.obe.client.resources.SpecialBakedModelCache;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedStorageDiagnostics;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityModelsManager {

    public BlockEntityModelsManager(){}

    public @Nullable BakedModel getModel(BlockState state, BlockPos pos, long seed, BakedModel originalModel, BlockAndTintGetter level){
        if (!state.hasBlockEntity()) return null;
        return getModel(state, pos, seed, originalModel, level.getBlockEntity(pos));
    }


    public @Nullable BakedModel getModel(BlockState state, BlockPos pos, long seed, BakedModel originalModel, BlockEntity be){

        if (be == null) return null;

        BlockEntityExt ext = (BlockEntityExt)be;
        if (ext == null || !ext.isSupported() || !ext.hasSpecialRenderer() || !ext.isEnabled()) return null;

        String group = Registry.getGroup(state);
        Registration<?> arbitraryRegistration = SpecialBakedModelRegistry.get(be.getType()).orElse(null);
        SpecialModelProvider customModelProvider = SpecialModelGetter.getSpecialModelProvider(state, group);
        if(ext.renderModeDelayed() == RenderMode.TERRAIN){
            if (arbitraryRegistration != null) {
                return getArbitraryModel(arbitraryRegistration, state, originalModel, be);
            }
            if(customModelProvider != null){
                Object cacheKey = customModelProvider.getCacheKeyProvider().apply(be);
                if(ResourceUtil.specialModelCacheContains(state, cacheKey)) return ResourceUtil.getSpecialModel(state, cacheKey);
                PoseStack poseStack = new PoseStack();

                ModelLayerLocation layerLocation = customModelProvider.getModelLayerLocationProvider().apply(state, be);
                if(layerLocation == null) return fail(be);

                TransformationGetter.applyTransformation(state, poseStack, group);

                ResourceLocation material = customModelProvider.getMaterialProvider().apply(state, be);
                if(material == null) return fail(be);

                BakedModel model = ResourceUtil.getModel(layerLocation, material, state, cacheKey, poseStack, getAmbientOcclusion(group), originalModel.getParticleIcon());
                if(customModelProvider.shouldKeepOriginalModel()) model = new CompositeBlockStateModel(model, originalModel);
                ResourceUtil.cache(state, cacheKey, model);
                return model;
            }
        }
        else if(arbitraryRegistration != null && !arbitraryRegistration.provider().showOriginalWhenEntityRendered()){
            return new BlockEntityStateModel(originalModel.getParticleIcon());
        }
        else if(customModelProvider != null && !customModelProvider.shouldShowOriginalWhenHidden()){
            return new BlockEntityStateModel(originalModel.getParticleIcon());
        }

        return originalModel;
    }

    @SuppressWarnings("unchecked")
    private <K> @Nullable BakedModel getArbitraryModel(
            Registration<?> rawRegistration,
            BlockState state,
            BakedModel originalModel,
            BlockEntity blockEntity
    ) {
        Registration<K> registration = (Registration<K>) rawRegistration;
        SpecialBakedModelProvider<K> provider = registration.provider();
        SpecialBakedModelContext context = new SpecialBakedModelContext(
                state,
                blockEntity,
                originalModel,
                originalModel.getParticleIcon()
        );

        K appearance = null;
        try {
            appearance = provider.resolveAppearance(context);
            if (appearance == null) {
                return failArbitrary(blockEntity, new FailureKey(typeId(blockEntity), state), "appearance resolver returned null");
            }
            if (!((BlockEntityExt) blockEntity).specialModelState()
                    .canAttempt(appearance, SpecialBakedModelCache.generation())) {
                return failArbitrary(blockEntity, appearance, "previous failure for this appearance");
            }
            K resolvedAppearance = appearance;
            ResourceLocation typeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
            BakedModel model = SpecialBakedModelCache.getOrBake(
                    registration.identity(),
                    typeId,
                    state,
                    resolvedAppearance,
                    () -> provider.bake(resolvedAppearance, context)
            );
            ((BlockEntityExt) blockEntity).specialModelState()
                    .prepareTerrain(resolvedAppearance, SpecialBakedModelCache.generation());
            return provider.keepOriginalModel() ? new CompositeBlockStateModel(model, originalModel) : model;
        } catch (Exception exception) {
            fr.madu59.obe.OBE.LOGGER.warn(
                    "Failed to build arbitrary block-entity model for {} at {}",
                    typeId(blockEntity),
                    blockEntity.getBlockPos(),
                    exception
            );
            Object failureKey = appearance != null ? appearance : new FailureKey(typeId(blockEntity), state);
            return failArbitrary(blockEntity, failureKey, exception.toString());
        }
    }

    private ResourceLocation typeId(BlockEntity blockEntity) {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
    }

    public BakedModel getBlockModel(BlockState state, RandomSource random, BakedModel originalModel, String group) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, group);
        if(layerLocation == null) return null;

        ResourceLocation material = MaterialGetter.getMaterial(state, group);
        if(material == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, group);

        BakedModel model = new BlockEntityStateModel(layerLocation, material, poseStack, getAmbientOcclusion(group), state, originalModel.getParticleIcon());

        model = new CompositeBlockStateModel(model, originalModel);
        ResourceUtil.cache(state, model);
        return model;
    }

    public BakedModel getDecoratedPotModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "decorated_pot");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "decorated_pot");

        BakedModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "decorated_pot"), state, poseStack, getAmbientOcclusion("decorated_pot"), originalModel.getParticleIcon());

        layerLocation = DecoratedPotUtil.getSideModelLayerLocation(state);

        BakedModel sideModel = ResourceUtil.getSubModel(layerLocation, Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BLANK).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
        
        model = new CompositeBlockStateModel(model, sideModel);

        ResourceUtil.cache(state, model);

        return model;
    }

    public boolean getAmbientOcclusion(String group){
        if (group == null) return false;
        return switch (group) {
            case "chest" -> SettingsManager.CHEST_AMBIENT_OCCLUSION.getValue();
            case "banner" -> SettingsManager.BANNER_AMBIENT_OCCLUSION.getValue();
            case "skull" -> SettingsManager.SKULL_AMBIENT_OCCLUSION.getValue();
            case "bell" -> SettingsManager.BELL_AMBIENT_OCCLUSION.getValue();
            case "decorated_pot" -> SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue();
            case "copper_golem_statue" -> SettingsManager.COPPER_GOLEM_AMBIENT_OCCLUSION.getValue();
            case "shulker_box" -> SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION.getValue();
            case "sophisticated_storage_chest" -> SettingsManager.SOPHISTICATED_CHEST_AMBIENT_OCCLUSION.getValue();
            case "sophisticated_storage_shulker_box" -> SettingsManager.SOPHISTICATED_SHULKER_AMBIENT_OCCLUSION.getValue();
            case "sign" -> SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue();
            case "hanging_sign" -> SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue();
            case "bed" -> SettingsManager.BED_AMBIENT_OCCLUSION.getValue();
            default -> false;
        };
    }

    public @Nullable BakedModel fail(BlockEntity be){
        ((BlockEntityExt) be).hasSpecialRenderer(false);
        ((BlockEntityExt) be).renderModeDelayed(RenderMode.ENTITY);
        return null;
    }

    private @Nullable BakedModel failArbitrary(BlockEntity blockEntity, Object appearance, String reason) {
        BlockEntityExt ext = (BlockEntityExt) blockEntity;
        ext.specialModelState().fail(appearance, SpecialBakedModelCache.generation(), reason);
        ResourceLocation type = typeId(blockEntity);
        if (type != null && type.getNamespace().equals("sophisticatedstorage")) {
            SophisticatedStorageDiagnostics.fallback();
        }
        RenderModeManager.setRenderModeDelayed(blockEntity, RenderMode.ENTITY, blockEntity.getBlockPos());
        return null;
    }

    private record FailureKey(ResourceLocation blockEntityType, BlockState state) {}
}

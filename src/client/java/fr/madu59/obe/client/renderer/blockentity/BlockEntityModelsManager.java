package fr.madu59.obe.client.renderer.blockentity;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.TransformationGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.ResourceUtil;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityModelsManager {

    public BlockEntityModelsManager(){}

    public @Nullable BlockStateModel getModel(BlockState state, BlockPos pos, long seed, BlockStateModel originalModel, BlockAndTintGetter level){
        if (!state.hasBlockEntity()) return null;
        return getModel(state, pos, seed, originalModel, level.getBlockEntity(pos));
    }


    public @Nullable BlockStateModel getModel(BlockState state, BlockPos pos, long seed, BlockStateModel originalModel, BlockEntity be){

        if (be == null) return null;

        BlockEntityExt ext = (BlockEntityExt)be;
        if (ext == null || !ext.isSupportedBlockEntity() || !ext.hasSpecialRenderer() || !ext.isEnabled()) return null;

        String group = Registry.getGroup(state);
        SpecialModelProvider customModelProvider = SpecialModelGetter.getSpecialModelProvider(state, group);
        if(ext.renderModeDelayed() == RenderMode.TERRAIN){
            if(customModelProvider != null){
                if(ResourceUtil.cacheContains(state, be)) return ResourceUtil.getModel(state, be);
                Object cacheKey = customModelProvider.getCacheKeyProvider().apply(be);
                PoseStack poseStack = new PoseStack();

                ModelLayerLocation layerLocation = customModelProvider.getModelLayerLocationProvider().apply(state, be);
                if(layerLocation == null) return fail(be);

                TransformationGetter.applyTransformation(state, poseStack, group);

                Identifier material = customModelProvider.getMaterialProvider().apply(state, be);
                if(material == null) return fail(be);

                BlockStateModel model = ResourceUtil.getModel(layerLocation, material, state, cacheKey, poseStack, getAmbientOcclusion(group), originalModel.particleMaterial());
                if(customModelProvider.shouldKeepOriginalModel()) model = new CompositeBlockStateModel(model, originalModel);
                ResourceUtil.cache(state, cacheKey, model);
                return model;
            }
        }
        else if(!customModelProvider.shouldShowOriginalWhenHidden()){
            return new BlockEntityStateModel(originalModel.particleMaterial());
        }

        return fail(be);
    }

    public BlockStateModel getBlockModel(BlockState state, RandomSource random, BlockStateModel originalModel, String group) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, group);
        if(layerLocation == null) return null;

        Identifier material = MaterialGetter.getMaterial(state, group);
        if(material == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, group);

        BlockStateModel model = new BlockEntityStateModel(layerLocation, material, poseStack, getAmbientOcclusion(group), state, originalModel.particleMaterial());

        model = new CompositeBlockStateModel(model, originalModel);
        ResourceUtil.cache(state, model);
        return model;
    }

    public BlockStateModel getDecoratedPotModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "decorated_pot");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "decorated_pot");

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "decorated_pot"), state, poseStack, getAmbientOcclusion("decorated_pot"), originalModel.particleMaterial());

        layerLocation = DecoratedPotUtil.getDecoratedPotSideModelLayerLocation(state);

        BlockStateModel sideModel = ResourceUtil.getSubModel(layerLocation, Sheets.getDecoratedPotSprite(DecoratedPotPatterns.BLANK).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.particleMaterial());
        
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
            case "sign" -> SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue();
            case "hanging_sign" -> SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue();
            case "bed" -> SettingsManager.BED_AMBIENT_OCCLUSION.getValue();
            default -> false;
        };
    }

    public @Nullable BlockStateModel fail(BlockEntity be){
        ((BlockEntityExt) be).hasSpecialRenderer(false);
        ((BlockEntityExt) be).renderModeDelayed(RenderMode.ENTITY);
        return null;
    }
}

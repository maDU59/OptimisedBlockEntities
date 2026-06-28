package fr.madu59.obe.client.renderer;

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
import fr.madu59.obe.client.util.ResourceUtil;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;

public class OBEBlockRenderer {

    public OBEBlockRenderer(){}

    public @Nullable BlockStateModel getModel(BlockState state, BlockPos pos, long seed, BlockStateModel originalModel, BlockAndTintGetter level){
        if (!state.hasBlockEntity()) return null;
        return getModel(state, pos, seed, originalModel, level.getBlockEntity(pos));
    }


    public @Nullable BlockStateModel getModel(BlockState state, BlockPos pos, long seed, BlockStateModel originalModel, BlockEntity be){

        if (be == null) return null;

        BlockEntityExt ext = (BlockEntityExt)be;
        if (ext == null || !ext.isSupportedBlockEntity() || !ext.hasSpecialRenderer() || !ext.isEnabled()) return null;

        Block block = state.getBlock();
        RandomSource random = RandomSource.create(seed);

        if(originalModel == null){
            originalModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
        }

        String group = Registry.getGroup(state);
        SpecialModelProvider customModelProvider = SpecialModelGetter.getSpecialModelProvider(state, group);
        if(ext.renderMode() == RenderMode.TERRAIN || ext.renderMode() == RenderMode.INTERMEDIATE){
            if(customModelProvider != null){
                if(ResourceUtil.cacheContains(state, be)) return ResourceUtil.getModel(state, be);
                PoseStack poseStack = new PoseStack();
                
                ModelLayerLocation layerLocation = customModelProvider.getModelLayerLocationProvider().apply(state, be);
                if(layerLocation == null) return null;

                TransformationGetter.applyTransformation(state, poseStack, "bell");

                Identifier material = customModelProvider.getMaterialProvider().apply(state, be);
                if(material == null) return null;

                BlockStateModel model = ResourceUtil.getModel(layerLocation, material, state, be, poseStack, getAmbientOcclusion(group), originalModel.particleIcon());
                if(customModelProvider.shouldKeepOriginalModel()) model = new CompositeBlockStateModel(model, originalModel);
                ResourceUtil.cache(state, be, model);
                return model;
            }
        }
        else if(!customModelProvider.shouldShowOriginalWhenHidden()){
            return new BlockEntityStateModel(originalModel.particleIcon());
        }

        return null;
    }

    public BlockStateModel getStandingSignModel(BlockState state, RandomSource random, BlockStateModel originalModel){
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "sign");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "sign");

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "sign"), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());

        return model;
    }

    public BlockStateModel getHangingSignModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "hanging_sign");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "hanging_sign");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "hanging_sign"), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
    }

    public BlockStateModel getSkullBlockModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "skull");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "skull");
        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "skull"), state, poseStack, getAmbientOcclusion("skull"), originalModel.particleIcon());
    }

    public BlockStateModel getBedModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "bed");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "bed");

        Identifier material = MaterialGetter.getMaterial(state, "bed");
        if(material == null) return null;

        BlockStateModel model = ResourceUtil.getModel(layerLocation, material, state, poseStack, SettingsManager.BED_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
        model = new CompositeBlockStateModel(model, originalModel);
        ResourceUtil.cache(state, model);
        return model;
    }

    public BlockStateModel getChestModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "chest");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "chest");
        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "chest"), state, poseStack, getAmbientOcclusion("chest"), originalModel.particleIcon());
        model = new CompositeBlockStateModel(model, originalModel);
        ResourceUtil.cache(state, model);
        return model;
    }

    public BlockStateModel getBannerModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "banner");
        if(layerLocation == null) return null;
        
        TransformationGetter.applyTransformation(state, poseStack, "banner");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "banner"), state, poseStack, getAmbientOcclusion("banner"), originalModel.particleIcon());
    }

    public BlockStateModel getCopperGolemStatueModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "copper_golem_statue");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "copper_golem_statue");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "copper_golem_statue"), state, poseStack, getAmbientOcclusion("copper_golem_statue"), originalModel.particleIcon());
    }

    public BlockStateModel getShulkerBoxModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "shulker_box");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "shulker_box");

        Identifier material = MaterialGetter.getMaterial(state, "shulker_box");
        if(material == null) return null;

        BlockStateModel model = ResourceUtil.getModel(layerLocation, material, state, poseStack, getAmbientOcclusion("shulker_box"), originalModel.particleIcon());
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

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "decorated_pot"), state, poseStack, getAmbientOcclusion("decorated_pot"), originalModel.particleIcon());

        layerLocation = DecoratedPotUtil.getDecoratedPotSideModelLayerLocation(state);

        BlockStateModel sideModel = ResourceUtil.getSubModel(layerLocation, Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BLANK).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
        
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
            default -> false;
        };
    }
}

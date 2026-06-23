package fr.madu59.obe.client.renderer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.TransformationGetter;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.util.ResourceUtil;
import fr.madu59.obe.client.util.blockentity.BannerUtil;
import fr.madu59.obe.client.util.blockentity.BellUtil;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.CopperGolemStatueUtil;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import fr.madu59.obe.client.util.blockentity.SkullBlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;

public class OBEBlockRenderer {

    public OBEBlockRenderer(){}

    public @Nullable BlockStateModel getModel(BlockState state, BlockPos pos, long seed, BlockStateModel originalModel){
        if (!state.hasBlockEntity()) return null;

        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (be == null) return null;

        BlockEntityExt ext = (BlockEntityExt)be;
        if (ext == null || !ext.isSupportedBlockEntity() || !ext.hasSpecialRenderer()) return null;

        RandomSource random = RandomSource.create(seed);

        if(be.getType() == BlockEntityTypes.BELL && (ext.renderMode() == RenderMode.TERRAIN || ext.renderMode() == RenderMode.INTERMEDIATE)){
            return getBellModel(state, random, originalModel);
        }

        return null;
    }

    public BlockStateModel getSkullBlockModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "skull");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "skull");
        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "skull"), state, poseStack, SettingsManager.SKULL_AMBIENT_OCCLUSION.getValue(), originalModel.particleMaterial());
    }

    public BlockStateModel getChestModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "chest");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "chest");
        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "chest"), state, poseStack, SettingsManager.CHEST_AMBIENT_OCCLUSION.getValue(), originalModel.particleMaterial());
    }

    public BlockStateModel getBellModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        System.out.println("Getting bell for state " + state);
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "bell");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "bell");

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bell"), state, poseStack, SettingsManager.BELL_AMBIENT_OCCLUSION.getValue(), originalModel.particleMaterial());
        model = new CompositeBlockStateModel(model, Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state));
        ResourceUtil.cache(layerLocation, state, model);
        return model;
    }

    public BlockStateModel getBannerModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "banner");
        if(layerLocation == null) return null;
        
        TransformationGetter.applyTransformation(state, poseStack, "banner");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "banner"), state, poseStack, SettingsManager.BANNER_AMBIENT_OCCLUSION.getValue(), originalModel.particleMaterial());
    }

    public BlockStateModel getCopperGolemStatueModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "copper_golem_statue");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "copper_golem_statue");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "copper_golem_statue"), state, poseStack, SettingsManager.COPPER_GOLEM_AMBIENT_OCCLUSION.getValue(), originalModel.particleMaterial());
    }

    public BlockStateModel getShulkerBoxModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "shulker_box");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "shulker_box");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "shulker_box"), state, poseStack, SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION.getValue(), originalModel.particleMaterial());
    }

    public BlockStateModel getDecoratedPotModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "decorated_pot");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "decorated_pot");

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "decorated_pot"), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.particleMaterial());

        layerLocation = DecoratedPotUtil.getDecoratedPotSideModelLayerLocation(state);

        Holder.Reference<DecoratedPotPattern> pattern = BuiltInRegistries.DECORATED_POT_PATTERN.getOrThrow(DecoratedPotPatterns.BLANK);

        BlockStateModel sideModel = ResourceUtil.getSubModel(layerLocation,  Sheets.DECORATED_POT_MAPPER.apply((pattern.value()).assetId()).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.particleMaterial());
        
        model = new CompositeBlockStateModel(model, sideModel);

        ResourceUtil.cache(layerLocation, state, model);

        return model;
    }
}

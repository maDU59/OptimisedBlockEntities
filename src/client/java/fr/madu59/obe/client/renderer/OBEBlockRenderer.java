package fr.madu59.obe.client.renderer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.TransformationGetter;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.util.ResourceUtil;
import fr.madu59.obe.client.util.blockentity.BannerUtil;
import fr.madu59.obe.client.util.blockentity.BedUtil;
import fr.madu59.obe.client.util.blockentity.BellUtil;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import fr.madu59.obe.client.util.blockentity.HangingSignUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import fr.madu59.obe.client.util.blockentity.SignUtil;
import fr.madu59.obe.client.util.blockentity.SkullBlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;

public class OBEBlockRenderer {

    public OBEBlockRenderer(){}

    public @Nullable BakedModel getModel(BlockState state, BlockPos pos, long seed, BakedModel originalModel){
        if (!RenderModeManager.hasBlockEntity(state)) return null;

        BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
        if (be == null) return null;

        BlockEntityExt ext = (BlockEntityExt)be;
        if (ext == null || !ext.isSupportedBlockEntity() || !ext.hasSpecialRenderer()) return null;

        Block block = state.getBlock();
        RandomSource random = RandomSource.create(seed);

        if(originalModel == null){
            originalModel = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state);
        }

        if(be.getType() == BlockEntityType.BELL && ext.renderMode() == RenderMode.TERRAIN){
            return getBellModel(state, random, originalModel);
        }

        return null;
    }

    public BakedModel getStandingSignModel(BlockState state, RandomSource random, BakedModel originalModel){
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "sign");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "sign");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "sign"), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getHangingSignModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "hanging_sign");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "hanging_sign");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "hanging_sign"), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getSkullBlockModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "skull");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "skull");
        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "skull"), state, poseStack, SettingsManager.SKULL_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getBedModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "bed");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "bed");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bed"), state, poseStack, SettingsManager.BED_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getChestModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "chest");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "chest");
        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "chest"), state, poseStack, SettingsManager.CHEST_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getBellModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "bell");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "bell");

        BakedModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bell"), state, poseStack, SettingsManager.BELL_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
        model = new CompositeBlockStateModel(model, Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state));
        ResourceUtil.cache(layerLocation, state, model);
        return model;
    }

    public BakedModel getBannerModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "banner");
        if(layerLocation == null) return null;
        
        TransformationGetter.applyTransformation(state, poseStack, "banner");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "banner"), state, poseStack, SettingsManager.BANNER_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getShulkerBoxModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "shulker_box");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "shulker_box");

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "shulker_box"), state, poseStack, SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
    }

    public BakedModel getDecoratedPotModel(BlockState state, RandomSource random, BakedModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, "decorated_pot");
        if(layerLocation == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, "decorated_pot");

        BakedModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "decorated_pot"), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());

        layerLocation = DecoratedPotUtil.getDecoratedPotSideModelLayerLocation(state);

        BakedModel sideModel = ResourceUtil.getSubModel(layerLocation, Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BLANK).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.getParticleIcon());
        
        model = new CompositeBlockStateModel(model, sideModel);

        ResourceUtil.cache(layerLocation, state, model);

        return model;
    }
}

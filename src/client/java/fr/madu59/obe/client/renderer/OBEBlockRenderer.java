package fr.madu59.obe.client.renderer;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
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
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

    public BlockStateModel getStandingSignModel(BlockState state, RandomSource random, BlockStateModel originalModel){
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        final ModelLayerLocation layerLocation = SignUtil.getSignModelLayerLocation(state);
        if(layerLocation == null) return null;

        SignUtil.transformSign(state, poseStack);

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "sign"), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());

        return model;
    }

    public BlockStateModel getHangingSignModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();

        ModelLayerLocation layerLocation = HangingSignUtil.getHangingSignModelLayerLocation(state);
        if(layerLocation == null) return null;

        HangingSignUtil.transformHangingSign(state, poseStack);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "hanging_sign"), state, poseStack, SettingsManager.SIGN_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
    }

    public BlockStateModel getSkullBlockModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = SkullBlockUtil.getSkullBlockModelLayerLocation(state);
        if(layerLocation == null) return null;

        SkullBlockUtil.transformSkullBlock(state, poseStack);
        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "skull"), state, poseStack, SettingsManager.SKULL_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
    }

    public BlockStateModel getBedModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = BedUtil.getBedModelLayerLocation(state);
        if(layerLocation == null) return null;

        BedUtil.transformBed(state, poseStack);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bed"), state, poseStack, SettingsManager.BED_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
    }

    public BlockStateModel getChestModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ChestUtil.getChestModelLayerLocation(state);
        if(layerLocation == null) return null;

        ChestUtil.transformChest(state, poseStack);
        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "chest"), state, poseStack, SettingsManager.CHEST_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
    }

    public BlockStateModel getBellModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = BellUtil.getBellModelLayerLocation(state);
        if(layerLocation == null) return null;

        BellUtil.transformBell(state, poseStack);

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "bell"), state, poseStack, SettingsManager.BELL_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
        model = new CompositeBlockStateModel(model, Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state));
        ResourceUtil.cache(layerLocation, state, model);
        return model;
    }

    public BlockStateModel getBannerModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = BannerUtil.getBannerModelLayerLocation(state);
        if(layerLocation == null) return null;
        
        BannerUtil.transformBanner(state, poseStack);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "banner"), state, poseStack, SettingsManager.BANNER_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
    }

    public BlockStateModel getShulkerBoxModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = ShulkerBoxUtil.getShulkerBoxModelLayerLocation(state);
        if(layerLocation == null) return null;

        ShulkerBoxUtil.transformShulkerBox(state, poseStack);

        return ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "shulker_box"), state, poseStack, SettingsManager.SHULKER_BOX_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
    }

    public BlockStateModel getDecoratedPotModel(BlockState state, RandomSource random, BlockStateModel originalModel) {
        if(ResourceUtil.cacheContains(state)) return ResourceUtil.getModel(state);
        PoseStack poseStack = new PoseStack();
        
        ModelLayerLocation layerLocation = DecoratedPotUtil.getDecoratedPotModelLayerLocation(state);
        if(layerLocation == null) return null;

        DecoratedPotUtil.transformDecoratedPot(state, poseStack);

        BlockStateModel model = ResourceUtil.getModel(layerLocation, MaterialGetter.getMaterial(state, "decorated_pot"), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());

        layerLocation = DecoratedPotUtil.getDecoratedPotSideModelLayerLocation(state);

        BlockStateModel sideModel = ResourceUtil.getSubModel(layerLocation, Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.BLANK).texture(), state, poseStack, SettingsManager.DECORATED_POT_AMBIENT_OCCLUSION.getValue(), originalModel.particleIcon());
        
        model = new CompositeBlockStateModel(model, sideModel);

        ResourceUtil.cache(layerLocation, state, model);

        return model;
    }
}

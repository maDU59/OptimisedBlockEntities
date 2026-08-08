package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState.ChestMaterialType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestUtil {
    public static final boolean isXmas = ChestRenderer.xmasTextures();

    public static Identifier getMaterial(BlockState state) {
        if (state.getBlock() instanceof EntityBlock entityBlock) {
            ChestMaterialType materialType = ChestRenderer.getChestMaterial(entityBlock.newBlockEntity(BlockPos.ZERO, state), isXmas);
            ChestType type = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);
            return Sheets.chooseSprite(materialType, type).texture();
        }
        return null;
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return ChestRenderer.LAYERS.select(state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE));
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state, BlockEntity entity){
        return getModelLayerLocation(state);
    }

    public static void transform(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(ChestBlock.FACING);
        poseStack.mulPose(ChestRenderer.modelTransformation(facing));
    }

    public static void transform(BlockState state, BlockEntity entity, PoseStack poseStack){
        transform(state, poseStack);
    }

    // Legacy methods, kept here to not break Quark's compatibility

    @Deprecated
    public static Identifier getChestMaterial(BlockState state) {
        return getMaterial(state);
    }

    @Deprecated
    public static ModelLayerLocation getChestModelLayerLocation(BlockState state){
        return getModelLayerLocation(state);
    }

    @Deprecated
    public static void transformChest(BlockState state, PoseStack poseStack){
        transform(state, poseStack);
    }

    @Deprecated
    public static Identifier getChestMaterial(BlockState state, BlockEntity be) {
        return getMaterial(state);
    }

    @Deprecated
    public static ModelLayerLocation getChestModelLayerLocation(BlockState state, BlockEntity be){
        return getModelLayerLocation(state, be);
    }

    @Deprecated
    public static void transformChest(BlockState state, BlockEntity be, PoseStack poseStack){
        transform(state, be, poseStack);
    }
}

package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState.ChestMaterialType;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CopperChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestUtil {
    private static final boolean xmasTexture = ChestRenderer.xmasTextures();

    public static Identifier getMaterial(BlockState state) {
        ChestMaterialType materialType;
        Block block = state.getBlock();
        if (block instanceof CopperChestBlock copperChestBlock) {
            switch (copperChestBlock.getState()) {
                case UNAFFECTED -> materialType = ChestMaterialType.COPPER_UNAFFECTED;
                case EXPOSED -> materialType = ChestMaterialType.COPPER_EXPOSED;
                case WEATHERED -> materialType = ChestMaterialType.COPPER_WEATHERED;
                case OXIDIZED -> materialType = ChestMaterialType.COPPER_OXIDIZED;
                default -> throw new MatchException((String)null, (Throwable)null);
            }
        } else if (block instanceof EnderChestBlock) {
            materialType = ChestMaterialType.ENDER_CHEST;
        } else if (xmasTexture) {
            materialType = ChestMaterialType.CHRISTMAS;
        } else {
            materialType = block instanceof TrappedChestBlock ? ChestMaterialType.TRAPPED : ChestMaterialType.REGULAR;
        }
        ChestType type = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);
        return Sheets.chooseSprite(materialType, type).texture();
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
        return getChestModelLayerLocation(state);
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
        return getChestModelLayerLocation(state, be);
    }

    @Deprecated
    public static void transformChest(BlockState state, BlockEntity be, PoseStack poseStack){
        transform(state, be, poseStack);
    }
}

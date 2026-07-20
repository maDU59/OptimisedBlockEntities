package fr.madu59.obe.client.util.blockentity;

import com.ibm.icu.util.Calendar;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.util.BackportUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestUtil {
    private static final Calendar calendar = Calendar.getInstance();
    public static final boolean isXmas = calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;

    public static ResourceLocation getMaterial(BlockState state) {
        Block block = state.getBlock();
        ChestType chestType = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);
        if (block instanceof EnderChestBlock) {
            return Sheets.ENDER_CHEST_LOCATION.texture();
        } else if (isXmas) {
            return chooseMaterial(chestType, Sheets.CHEST_XMAS_LOCATION, Sheets.CHEST_XMAS_LOCATION_LEFT, Sheets.CHEST_XMAS_LOCATION_RIGHT);
        } else {
            return block instanceof TrappedChestBlock ? chooseMaterial(chestType, Sheets.CHEST_TRAP_LOCATION, Sheets.CHEST_TRAP_LOCATION_LEFT, Sheets.CHEST_TRAP_LOCATION_RIGHT) : chooseMaterial(chestType, Sheets.CHEST_LOCATION, Sheets.CHEST_LOCATION_LEFT, Sheets.CHEST_LOCATION_RIGHT);
        }
    }

    private static ResourceLocation chooseMaterial(ChestType chestType, Material material, Material material2, Material material3) {
        if(chestType == null) return material.texture();
        return switch (chestType) {
            case RIGHT -> material3.texture();
            case LEFT -> material2.texture();
            case SINGLE -> material.texture();
        };
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return switch(BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE)){
            case SINGLE -> ModelLayers.CHEST;
            case LEFT -> ModelLayers.DOUBLE_CHEST_LEFT;
            case RIGHT -> ModelLayers.DOUBLE_CHEST_RIGHT;
        };
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state, BlockEntity entity){
        return getModelLayerLocation(state);
    }

    public static void transform(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(ChestBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
    }

    public static void transform(BlockState state, BlockEntity entity, PoseStack poseStack){
        transform(state, poseStack);
    }

    // Legacy methods, kept here to not break Quark's compatibility

    @Deprecated
    public static ResourceLocation getChestMaterial(BlockState state) {
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
    public static ResourceLocation getChestMaterial(BlockState state, BlockEntity be) {
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

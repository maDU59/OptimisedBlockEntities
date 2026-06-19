package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestUtil {
    private static final boolean xmasTexture = ChestRenderer.xmasTextures();

    public static ResourceLocation getChestMaterial(BlockState state) {
        Block block = state.getBlock();
        ChestType chestType = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);
        if (block instanceof EnderChestBlock) {
            return Sheets.ENDER_CHEST_LOCATION.texture();
        } else if (xmasTexture) {
            return chooseMaterial(chestType, Sheets.CHEST_XMAS_LOCATION, Sheets.CHEST_XMAS_LOCATION_LEFT, Sheets.CHEST_XMAS_LOCATION_RIGHT);
        } else {
            return block instanceof TrappedChestBlock ? chooseMaterial(chestType, Sheets.CHEST_TRAP_LOCATION, Sheets.CHEST_TRAP_LOCATION_LEFT, Sheets.CHEST_TRAP_LOCATION_RIGHT) : chooseMaterial(chestType, Sheets.CHEST_LOCATION, Sheets.CHEST_LOCATION_LEFT, Sheets.CHEST_LOCATION_RIGHT);
        }
    }

    private static ResourceLocation chooseMaterial(ChestType chestType, Material material, Material material2, Material material3) {
        return switch (chestType) {
            case ChestType.RIGHT -> material3.texture();
            case ChestType.LEFT -> material2.texture();
            case null, default -> material.texture();
        };
    }
}

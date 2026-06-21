package fr.madu59.obe.util.blockentity;

import com.ibm.icu.util.Calendar;

import fr.madu59.obe.util.BackportUtil;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestUtil {
    private static final Calendar calendar = Calendar.getInstance();
    private static final boolean xmasTexture = calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;

    public static ResourceLocation getChestMaterial(BlockState state) {
        Block block = state.getBlock();
        ChestType chestType = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);
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
            case RIGHT -> material3.texture();
            case LEFT -> material2.texture();
            case SINGLE -> material.texture();
        };
    }
}

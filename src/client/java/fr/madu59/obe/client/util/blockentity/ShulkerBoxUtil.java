package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShulkerBoxUtil {
    public static ResourceLocation getShulkerBoxMaterial(BlockState state){
        ShulkerBoxBlock block = (ShulkerBoxBlock) state.getBlock();
        
        DyeColor color = block.getColor();
        if (color == null) {
           return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture();
        } else {
            return Sheets.getShulkerBoxMaterial(color).texture();
        }
    }
}

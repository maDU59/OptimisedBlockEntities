package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShulkerBoxUtil {
    public static Identifier getShulkerBoxMaterial(BlockState state){
        ShulkerBoxBlock block = (ShulkerBoxBlock) state.getBlock();
        
        DyeColor color = block.getColor();
        if (color == null) {
           return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture();
        } else {
            return Sheets.getShulkerBoxSprite(color).texture();
        }
    }

    public static ModelLayerLocation getShulkerBoxModelLayerLocation(BlockState state){
        return ModelLayers.SHULKER_BOX;
    }
}

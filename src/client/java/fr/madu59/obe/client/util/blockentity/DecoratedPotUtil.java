package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class DecoratedPotUtil {
    public static ResourceLocation getDecoratedPotMaterial(BlockState state){
        return Sheets.DECORATED_POT_BASE.texture();
    }
}

package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public class BannerUtil {
    public static Identifier getBannerMaterial(BlockState state){
        return Sheets.BANNER_BASE.texture();
    }
}

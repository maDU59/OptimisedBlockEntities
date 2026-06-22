package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BannerUtil {
    public static Identifier getBannerMaterial(BlockState state){
        return Sheets.BANNER_BASE.texture();
    }

    public static ModelLayerLocation getBannerModelLayerLocation(BlockState state){
        if(state.getBlock() instanceof WallBannerBlock)  return ModelLayers.WALL_BANNER;
        else return  ModelLayers.STANDING_BANNER;
    }
}

package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public class DecoratedPotUtil {
    public static Identifier getDecoratedPotMaterial(BlockState state){
        return Sheets.DECORATED_POT_BASE.texture();
    }

    public static ModelLayerLocation getDecoratedPotModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_BASE;
    }

    public static ModelLayerLocation getDecoratedPotSideModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_SIDES;
    }
}

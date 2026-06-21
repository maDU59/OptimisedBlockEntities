package fr.madu59.obe.util.blockentity;

import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class BellUtil {
    public static ResourceLocation getBellMaterial(BlockState state){
        return BellRenderer.BELL_RESOURCE_LOCATION.texture();
    }
}

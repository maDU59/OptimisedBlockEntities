package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public class BellUtil {
    public static Identifier getBellMaterial(BlockState state){
        return BellRenderer.BELL_TEXTURE.texture();
    }
}

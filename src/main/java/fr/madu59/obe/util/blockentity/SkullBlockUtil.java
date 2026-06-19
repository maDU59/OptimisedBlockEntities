package fr.madu59.obe.util.blockentity;

import fr.madu59.obe.util.ResourceUtil;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockUtil {
    public static ResourceLocation getSkullBlockMaterial(BlockState state) {
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        ResourceLocation id = SkullBlockRenderer.SKIN_BY_TYPE.get(type);
        return ResourceUtil.entityTextureFormatter(id);
    }
}

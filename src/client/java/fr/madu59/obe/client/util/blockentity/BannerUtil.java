package fr.madu59.obe.client.util.blockentity;

import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public class BannerUtil {
    public static Identifier getBannerMaterial(BlockState state){
        return ResourceUtil.entityTextureFormatter(ModelBakery.BANNER_BASE.texture());
    }
}

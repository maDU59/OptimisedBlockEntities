package fr.madu59.obe.util.blockentity;

import fr.madu59.obe.util.ResourceUtil;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class BannerUtil {
    public static ResourceLocation getBannerMaterial(BlockState state){
        return ResourceUtil.entityTextureFormatter(ModelBakery.BANNER_BASE.texture());
    }
}

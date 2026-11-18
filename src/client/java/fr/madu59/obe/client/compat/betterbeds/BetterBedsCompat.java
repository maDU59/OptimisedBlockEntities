package fr.madu59.obe.client.compat.betterbeds;

import fr.madu59.obe.client.api.registry.RegistryApi;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BetterBedsCompat {
    public static void init(){
        RegistryApi.registerModelBuilder(BlockEntityType.BED, (state, model) -> model);
    }
}

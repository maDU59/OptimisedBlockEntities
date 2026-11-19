package fr.madu59.obe.client.compat.biomeswevegone;

import fr.madu59.obe.client.api.registry.RegistryApi;
import net.potionstudios.biomeswevegone.world.level.block.entities.BWGBlockEntityType;

public class BiomesWeveGoneCompat {
    public static void init() {
        RegistryApi.registerBlockEntityType("sign", BWGBlockEntityType.SIGNS.get());
        RegistryApi.registerBlockEntityType("hanging_sign", BWGBlockEntityType.HANGING_SIGNS.get());
    }
}

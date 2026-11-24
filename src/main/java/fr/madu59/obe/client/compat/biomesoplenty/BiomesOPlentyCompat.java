package fr.madu59.obe.client.compat.biomesoplenty;

import fr.madu59.obe.client.registry.Registry;

public class BiomesOPlentyCompat {
    public static void init() {
        Registry.tryRegisterBlockEntityType("sign", "biomesoplenty", "sign");
        Registry.tryRegisterBlockEntityType("hanging_sign", "biomesoplenty", "hanging_sign");
    }
}

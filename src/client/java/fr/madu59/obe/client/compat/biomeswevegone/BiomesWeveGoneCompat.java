package fr.madu59.obe.client.compat.biomeswevegone;

import fr.madu59.obe.client.registry.Registry;

public class BiomesWeveGoneCompat {
    public static void init() {
        Registry.tryRegisterBlockEntityType("sign", "biomeswevegone", "sign");
        Registry.tryRegisterBlockEntityType("hanging_sign", "biomeswevegone", "hanging_sign");
    }
}

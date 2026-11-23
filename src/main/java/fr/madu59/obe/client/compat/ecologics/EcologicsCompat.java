package fr.madu59.obe.client.compat.ecologics;

import fr.madu59.obe.client.registry.Registry;

public class EcologicsCompat {
    public static void init() {
        Registry.tryRegisterBlockEntityType("sign", "ecologics", "sign");
        Registry.tryRegisterBlockEntityType("hanging_sign", "ecologics", "hanging_sign");
    }
}

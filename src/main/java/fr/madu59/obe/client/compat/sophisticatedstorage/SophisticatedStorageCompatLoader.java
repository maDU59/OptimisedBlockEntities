package fr.madu59.obe.client.compat.sophisticatedstorage;

/**
 * Lazy classloading boundary. ModCompat touches this class only after both
 * optional mods are known to be present.
 */
public final class SophisticatedStorageCompatLoader {
    private SophisticatedStorageCompatLoader() {}

    public static void init() {
        SophisticatedStorageCompat.init();
    }
}

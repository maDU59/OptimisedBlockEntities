package fr.madu59.obe.client.compat.clayworks;

import fr.madu59.obe.client.config.SettingsManager;

public class ClayworksCompat {
    public static void init() {
        SettingsManager.OPTIMISED_DECORATED_POTS.setValue(false);
    }
}

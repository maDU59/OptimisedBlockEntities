package fr.madu59.obe.client.config.configscreen.oneconfig;

import org.polyfrost.oneconfig.api.toggle.v1.ModToggle;

import fr.madu59.obe.client.config.SettingsManager;

public class OBEToggle implements ModToggle {

    @Override
    public boolean isEnabled() {
        return SettingsManager.MOD_TOGGLE.getValue();
    }

    @Override
    public void setEnabled(boolean enabled) {
        SettingsManager.MOD_TOGGLE.setValue(enabled);
        SettingsManager.saveSettings();
    }
    
}

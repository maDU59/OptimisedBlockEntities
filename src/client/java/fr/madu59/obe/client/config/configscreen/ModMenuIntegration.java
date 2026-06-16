package fr.madu59.obe.client.config.configscreen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
        @Override
        public ConfigScreenFactory<OptimisedBlockEntitiesConfigScreen> getModConfigScreenFactory() {
                return OptimisedBlockEntitiesConfigScreen::new;
        }
}
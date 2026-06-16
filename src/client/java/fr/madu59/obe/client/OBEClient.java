package fr.madu59.obe.client;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.config.configscreen.OptimisedBlockEntitiesConfigScreen;
import net.fabricmc.api.ClientModInitializer;

public class OBEClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		OptimisedBlockEntitiesConfigScreen.registerCommand();
	}

	public static void debug(String debugInfo) {
		OBE.LOGGER.info(debugInfo);
	}
}
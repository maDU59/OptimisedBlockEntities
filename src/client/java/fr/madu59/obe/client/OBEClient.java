package fr.madu59.obe.client;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.configscreen.OptimisedBlockEntitiesConfigScreen;
import fr.madu59.obe.client.platform.PlatformHelper;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.resources.loader.SkullPackLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class OBEClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Registry.init();
		ModCompat.init();
		OptimisedBlockEntitiesConfigScreen.registerCommand();
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SkullPackLoader());
		PlatformHelper.registerPlatformEvents();
	}

	public static void debug(String debugInfo) {
		OBE.LOGGER.info(debugInfo);
	}
}
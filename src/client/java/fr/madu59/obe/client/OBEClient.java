package fr.madu59.obe.client;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.configscreen.OptimisedBlockEntitiesConfigScreen;
import fr.madu59.obe.client.platform.PlatformHelper;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.resources.loader.SkullPackLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public class OBEClient implements ClientModInitializer {

	public static final ModelLayerLocation EMPTY_MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.tryBuild(OBE.MOD_ID, "empty_model_layer"), "empty_model_layer");

	@Override
	public void onInitializeClient() {
		Registry.init();
		ModCompat.init();
		OptimisedBlockEntitiesConfigScreen.registerCommand();
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(Identifier.tryParse("obe:skulls"), new SkullPackLoader());
		PlatformHelper.registerPlatformEvents();
	}

	public static void debug(String debugInfo) {
		OBE.LOGGER.info(debugInfo);
	}
}
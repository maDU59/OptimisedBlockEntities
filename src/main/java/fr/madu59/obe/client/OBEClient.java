package fr.madu59.obe.client;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.configscreen.OptimisedBlockEntitiesConfigScreen;
import fr.madu59.obe.client.platform.PlatformHelper;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.resources.loader.SkullPackLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = OBE.MOD_ID, dist = Dist.CLIENT)
public class OBEClient {

	public OBEClient(ModContainer container, IEventBus bus){
        NeoForge.EVENT_BUS.register(OptimisedBlockEntitiesConfigScreen.class);
        container.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> {
            return new OptimisedBlockEntitiesConfigScreen(parent);
        });
        Registry.init();
		bus.addListener(this::onLoadComplete);
		PlatformHelper.registerPlatformEvents(bus);
		bus.addListener(this::onRegisterClientReloadListeners);
	}

	private void onLoadComplete(FMLLoadCompleteEvent event) {
		ModCompat.init();
	}

	public static void debug(String debugInfo) {
		OBE.LOGGER.info(debugInfo);
	}

	public void onRegisterClientReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(Identifier.tryParse("obe:skulls"), new SkullPackLoader());
    }
}
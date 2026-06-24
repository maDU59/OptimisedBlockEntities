package fr.madu59.obe;

import fr.madu59.obe.config.configscreen.OptimisedBlockEntitiesConfigScreen;
import fr.madu59.obe.registry.Registry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
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
    }

	public static void debug(String debugInfo) {
		OBE.LOGGER.info(debugInfo);
	}
}
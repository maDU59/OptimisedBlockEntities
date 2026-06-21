package fr.madu59.obe;

import fr.madu59.obe.config.configscreen.OptimisedBlockEntitiesConfigScreen;
import fr.madu59.obe.registry.Registry;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@EventBusSubscriber(modid = OBE.MOD_ID, value = Dist.CLIENT)
public class OBEClient {


	public OBEClient(IEventBus bus){
        MinecraftForge.EVENT_BUS.register(OptimisedBlockEntitiesConfigScreen.class);
        ModLoadingContext.get().registerExtensionPoint(
			ConfigScreenHandler.ConfigScreenFactory.class, 
			() -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> (
				new OptimisedBlockEntitiesConfigScreen(parent))
			)
		);
		Registry.init();
    }

	public static void debug(String debugInfo) {
		OBE.LOGGER.info(debugInfo);
	}
}
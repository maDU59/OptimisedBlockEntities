package fr.madu59.obe.client.platform;

import java.nio.file.Path;

import fr.madu59.obe.client.compat.ModCompat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

public class PlatformHelper {

    public static String getPlatformName(){
        return "NeoForge";
    }
    
    public static boolean isModLoaded(String modId){
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;
    }

    public static Path getConfigDir(){
        return FMLPaths.CONFIGDIR.get();
    }

    public static String getModName(String modId){
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId).getMods().get(0).getDisplayName();
    }

    public static void registerPlatformEvents(IEventBus bus){
        NeoForge.EVENT_BUS.addListener(PlatformHelper::onWorldLoad);
    }

    public static void onWorldLoad(LevelEvent.Load event){
        ModCompat.onWorldLoad();
    }
}
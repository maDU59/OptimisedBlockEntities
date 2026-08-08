package fr.madu59.obe.client.platform;

import java.nio.file.Path;

import fr.madu59.obe.client.compat.ModCompat;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.loader.api.FabricLoader;

public class PlatformHelper {

    public static String getPlatformName(){
        return "Fabric";
    }
    
    public static boolean isModLoaded(String modId){
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static Path getConfigDir(){
        return FabricLoader.getInstance().getConfigDir();
    }

    public static String getModName(String modId){
        return FabricLoader.getInstance().getModContainer(modId).get().getMetadata().getName();
    }

    public static void registerPlatformEvents(){
        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((minecraft, level) -> ModCompat.onWorldLoad());
    }
}

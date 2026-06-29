package fr.madu59.obe.client.platform;

import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

public class PlatformHelper {

    public static String getPlatformName(){
        return "Neoforge";
    }
    
    public static boolean isModLoaded(String modId){
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    public static Path getConfigDir(){
        return FMLPaths.CONFIGDIR.get();
    }

    public static String getModName(String modId){
        return FMLLoader.getLoadingModList().getModFileById(modId).getMods().get(0).getDisplayName();
    }
}
package fr.madu59.obe.client.platform;

import java.nio.file.Path;

import fr.madu59.obe.client.renderer.entity.MeshableEntityTracker;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
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
        ClientEntityEvents.ENTITY_LOAD.register((entity, clientLevel) -> {
            MeshableEntityTracker.registerMeshableEntity(entity, entity.blockPosition());
        });
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, clientLevel) -> {
            MeshableEntityTracker.deregisterMeshableEntity(entity, entity.blockPosition());
        });
    }
}

package fr.madu59.obe.client.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.madu59.obe.OBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

public class Registry {
    private static Map<String, Set<BlockEntityType<?>>> supportedBeTypes = new HashMap<>();

    public static void init(){
        register("chest", BlockEntityTypes.CHEST, BlockEntityTypes.ENDER_CHEST, BlockEntityTypes.TRAPPED_CHEST);
        register("bell", BlockEntityTypes.BELL);
        register("skull", BlockEntityTypes.SKULL);
        register("banner", BlockEntityTypes.BANNER);
        register("shulker_box", BlockEntityTypes.SHULKER_BOX);
        register("decorated_pot", BlockEntityTypes.DECORATED_POT);
        register("copper_golem_statue", BlockEntityTypes.COPPER_GOLEM_STATUE);
    }

    private static void register(String group, BlockEntityType<?> ... types){
        Collections.addAll(
            supportedBeTypes.computeIfAbsent(group, k -> new HashSet<>()), 
            types
        );
    }

    public static void registerGroup(String group){
        if(supportedBeTypes.containsKey(group)){
            OBE.LOGGER.warn("An external mod tried to register an already-existing group (" + group + "), this may cause issues and is probably due to to an incompatibility between 2 mods");
        }
        else{
            supportedBeTypes.put(group, new HashSet<>());
        }
    }

    public static void addBlockEntityTypeInGroup(String group, BlockEntityType<?> ... types){
        if(!supportedBeTypes.containsKey(group)){
            OBE.LOGGER.error("An external mod tried registering a block entity type in a non existing group: " + group);
        }
        else{
            Collections.addAll(
                supportedBeTypes.get(group), 
                types
            );
        }
    }

    public static boolean isSupported(String group, BlockEntityType<?> type){
        if(!supportedBeTypes.containsKey(group)){
            OBE.LOGGER.warn("An external mod tried accessing a non existing group: " + group);
            return false;
        }
        else{
            return supportedBeTypes.get(group).contains(type);
        }
    }
}

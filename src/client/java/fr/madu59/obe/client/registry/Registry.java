package fr.madu59.obe.client.registry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import fr.madu59.obe.OBE;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class Registry {
    private static Map<String, Set<BlockEntityType<?>>> supportedBeTypes = new ConcurrentHashMap<>();
    private static Map<BlockState, BlockEntityType<?>> beTypeCache = new ConcurrentHashMap<>();

    public static void init(){
        register("chest", BlockEntityType.CHEST, BlockEntityType.ENDER_CHEST, BlockEntityType.TRAPPED_CHEST);
        register("bell", BlockEntityType.BELL);
        register("skull", BlockEntityType.SKULL);
        register("banner", BlockEntityType.BANNER);
        register("shulker_box", BlockEntityType.SHULKER_BOX);
        register("decorated_pot", BlockEntityType.DECORATED_POT);
        register("copper_golem_statue", BlockEntityType.COPPER_GOLEM_STATUE);
        register("sign", BlockEntityType.SIGN);
        register("hanging_sign", BlockEntityType.HANGING_SIGN);
        register("bed", BlockEntityType.BED);
        MaterialGetter.init();
        TransformationGetter.init();
        ModelLayerLocationGetter.init();
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

    public static boolean isSupported(String group, BlockState state){
        if(!state.hasBlockEntity()) return false;
        if(!supportedBeTypes.containsKey(group)){
            OBE.LOGGER.warn("An external mod tried accessing a non existing group: " + group);
            return false;
        }
        else{
            for(BlockEntityType<?> type : supportedBeTypes.get(group)){
                if(type.isValid(state)){
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean hasGroup(String group){
       return supportedBeTypes.containsKey(group);
    }

    public static String getGroup(BlockState state){
        if(!state.hasBlockEntity()) return null;
        BlockEntityType<?> beType = getBlockEntityType(state);
        return getGroup(beType);
    }

    public static String getGroup(BlockEntityType<?> beType){
        for(Entry<String,Set<BlockEntityType<?>>> entry : supportedBeTypes.entrySet()){
            if(entry.getValue().contains(beType)) return entry.getKey();
        }
        return null;
    }

    public static BlockEntityType<?> getBlockEntityType(BlockState state){
        if(!state.hasBlockEntity()) return null;
        return beTypeCache.computeIfAbsent(state, (key) -> {
            for(Set<BlockEntityType<?>> set : supportedBeTypes.values())
                for(BlockEntityType<?> type : set){
                    if(type.isValid(state)){
                        return type;
                    }
                }
            return null;
        });
    }
}

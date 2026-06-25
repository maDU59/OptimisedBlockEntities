package fr.madu59.obe.client.registry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import fr.madu59.obe.OBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class Registry {
    private static Map<String, Set<BlockEntityType<?>>> supportedBeTypes = new ConcurrentHashMap<>();
    private static Map<Block, BlockEntityType<?>> beTypeCache = new ConcurrentHashMap<>();
    private static Map<Block, String> blockGroupCache = new ConcurrentHashMap<>();
    private static Map<BlockEntityType<?>, String> beTypeGroupCache = new ConcurrentHashMap<>();

    private static final String noneGroupKey = "OBE_NONE";
    private static final BlockEntityType<?> noneBlockEntityType = BlockEntityType.ENCHANTING_TABLE;

    public static void init(){
        register("chest", BlockEntityType.CHEST, BlockEntityType.ENDER_CHEST, BlockEntityType.TRAPPED_CHEST);
        register("bell", BlockEntityType.BELL);
        register("skull", BlockEntityType.SKULL);
        register("banner", BlockEntityType.BANNER);
        register("shulker_box", BlockEntityType.SHULKER_BOX);
        register("decorated_pot", BlockEntityType.DECORATED_POT);
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
            return supportedBeTypes.get(group).contains(getBlockEntityType(state));
        }
    }

    public static boolean hasGroup(String group){
       return supportedBeTypes.containsKey(group);
    }

    public static String getGroup(BlockState state){
        if(!state.hasBlockEntity()) return null;
        String group = blockGroupCache.computeIfAbsent(state.getBlock(), (key) -> {
            BlockEntityType<?> beType = getBlockEntityType(state);
            return getGroup(beType);
        });
        return group.equals(noneGroupKey)? null : group;
    }

    public static String getGroup(BlockEntityType<?> beType){
        String group = beTypeGroupCache.computeIfAbsent(beType, (key) -> {
            for(Entry<String,Set<BlockEntityType<?>>> entry : supportedBeTypes.entrySet()){
                if(entry.getValue().contains(beType)) return entry.getKey();
            }
            return noneGroupKey;
        });
        return group.equals(noneGroupKey)? null : group;
    }

    public static BlockEntityType<?> getBlockEntityType(BlockState state){
        if(!state.hasBlockEntity()) return null;
        BlockEntityType<?> beType = beTypeCache.computeIfAbsent(state.getBlock(), (key) -> {
            for(Set<BlockEntityType<?>> set : supportedBeTypes.values())
                for(BlockEntityType<?> type : set){
                    if(type.isValid(state)){
                        return type;
                    }
                }
            return noneBlockEntityType;
        });
        return beType == noneBlockEntityType? null : beType;
    }
}

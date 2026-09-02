package fr.madu59.obe.client.registry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import fr.madu59.obe.OBE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class Registry {
    private static Map<String, Set<BlockEntityType<?>>> supportedBeTypes = new ConcurrentHashMap<>();
    private static Map<Block, Optional<BlockEntityType<?>>> beTypeCache = new ConcurrentHashMap<>();
    private static Map<Block, String> blockGroupCache = new ConcurrentHashMap<>();
    private static Map<BlockEntityType<?>, String> beTypeGroupCache = new ConcurrentHashMap<>();

    private static final String noneGroupKey = "OBE_NONE";

    private static boolean isInit = false;

    public static void init(){
        if(isInit) return;
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
        SpecialModelGetter.init();
        isInit = true;
    }

    private static void register(String group, BlockEntityType<?> ... types){
        Collections.addAll(
            supportedBeTypes.computeIfAbsent(group, k -> new HashSet<>()), 
            types
        );
    }

    public static void registerGroup(String group){
        if(!isInit) init();
        if(supportedBeTypes.containsKey(group)){
            OBE.LOGGER.warn("An external mod tried to register an already-existing group (" + group + "), this may cause issues and is probably due to to an incompatibility between 2 mods");
        }
        else{
            supportedBeTypes.put(group, new HashSet<>());
        }
    }

    public static void addBlockEntityTypeInGroup(String group, BlockEntityType<?> ... types){
        if(!isInit) init();
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
        if(!isInit) init();
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
        if(!isInit) init();
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
            return getGroupInternal(beType);
        });
        return group.equals(noneGroupKey)? null : group;
    }

    public static String getGroup(BlockEntityType<?> beType){
        String group = getGroupInternal(beType);
        return group.equals(noneGroupKey)? null : group;
    }

    private static String getGroupInternal(BlockEntityType<?> beType){
        if(beType == null) return noneGroupKey;
        return beTypeGroupCache.computeIfAbsent(beType, (key) -> {
            for(Entry<String,Set<BlockEntityType<?>>> entry : supportedBeTypes.entrySet()){
                if(entry.getValue().contains(beType)) return entry.getKey();
            }
            return noneGroupKey;
        });
    }

    public static BlockEntityType<?> getBlockEntityType(BlockState state){
        if(!state.hasBlockEntity()) return null;
        Block block = state.getBlock();

        Optional<BlockEntityType<?>> cached = beTypeCache.get(block);
        if(cached != null){
            return cached.orElse(null);
        }

        BlockEntityType<?> match = null;
        for(Set<BlockEntityType<?>> set : supportedBeTypes.values()){
            for(BlockEntityType<?> type : set){
                if(type.isValid(state)){
                    if(match != null){
                        // This tries to fix #69 (and #30 and # 13) where a block is considered as valid in a BlockEntityType where it shouldn't. EnderScape is causing issues
                        OBE.LOGGER.warn("Inconsistent BlockEntityType match detected for block " + block + " (" + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(match).toString() + "), skipping cache for this query.");
                        return null;
                    }
                    match = type;
                }
            }
        }

        beTypeCache.put(block, Optional.ofNullable(match));
        return match;
    }
}

package fr.madu59.obe.client.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.util.blockentity.BannerUtil;
import fr.madu59.obe.client.util.blockentity.BedUtil;
import fr.madu59.obe.client.util.blockentity.BellUtil;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.CopperGolemStatueUtil;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import fr.madu59.obe.client.util.blockentity.HangingSignUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import fr.madu59.obe.client.util.blockentity.SignUtil;
import fr.madu59.obe.client.util.blockentity.SkullBlockUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MaterialGetter {
    private final static Map<BlockEntityType<?>, Function<BlockState, Identifier>> materialProviders = new ConcurrentHashMap<>();
    private final static Map<Block, Function<BlockState, Identifier>> perBlockMaterialProviders = new ConcurrentHashMap<>();
    private final static Map<String, Function<BlockState, Identifier>> defaultMaterialProviders = new ConcurrentHashMap<>();

    public static void init(){
        registerDefault("chest", ChestUtil::getMaterial);
        registerDefault("skull", SkullBlockUtil::getMaterial);
        registerDefault("bell", BellUtil::getMaterial);
        registerDefault("banner", BannerUtil::getMaterial);
        registerDefault("shulker_box", ShulkerBoxUtil::getMaterial);
        registerDefault("decorated_pot", DecoratedPotUtil::getMaterial);
        registerDefault("copper_golem_statue", CopperGolemStatueUtil::getMaterial);
        registerDefault("sign", SignUtil::getMaterial);
        registerDefault("hanging_sign", HangingSignUtil::getMaterial);
        registerDefault("bed", BedUtil::getMaterial);
    }

    public static void registerDefault(String group, Function<BlockState, Identifier> getter){
        if(!Registry.hasGroup(group)){
            OBE.LOGGER.error("An external mod tried registering a default material getter in a non existing group: " + group);
        }
        else{
            defaultMaterialProviders.put(group, getter);
        }
    }

    public static void register(BlockEntityType<?> beType, Function<BlockState, Identifier> getter){
        materialProviders.put(beType, getter);
    }

    public static void register(Block block, Function<BlockState, Identifier> getter){
        perBlockMaterialProviders.put(block, getter);
    }

    public static Identifier getMaterial(BlockState state){
        return getMaterial(state, null);
    }

    public static Identifier getMaterial(BlockState state, String group){
        if(!state.hasBlockEntity()) return null;

        Block block = state.getBlock();
        Function<BlockState, Identifier> provider = perBlockMaterialProviders.get(block);
        if (provider != null) return provider.apply(state);

        BlockEntityType<?> beType = Registry.getBlockEntityType(state);
        if (beType == null) return null;
        provider = materialProviders.get(beType);
        if (provider != null) return provider.apply(state);

        if (group == null) group = Registry.getGroup(beType);
        if (group != null) provider = defaultMaterialProviders.get(group);
        if (provider != null) return provider.apply(state);
        
        return null;
    }
}

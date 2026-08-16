package fr.madu59.obe.client.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.util.blockentity.BannerUtil;
import fr.madu59.obe.client.util.blockentity.BedUtil;
import fr.madu59.obe.client.util.blockentity.BellUtil;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import fr.madu59.obe.client.util.blockentity.HangingSignUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import fr.madu59.obe.client.util.blockentity.SignUtil;
import fr.madu59.obe.client.util.blockentity.SkullBlockUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ModelLayerLocationGetter {
    private static Map<BlockEntityType<?>, Function<BlockState, ModelLayerLocation>> modelLayerLocationsGetterProvider = new ConcurrentHashMap<>();
    private static Map<Block, Function<BlockState, ModelLayerLocation>> perBlockModelLayerLocationsGetterProvider = new ConcurrentHashMap<>();
    private static Map<String, Function<BlockState, ModelLayerLocation>> defaultModelLayerLocationsGetterProvider = new ConcurrentHashMap<>();

    public static void init(){
        registerDefault("chest", ChestUtil::getModelLayerLocation);
        registerDefault("skull", SkullBlockUtil::getModelLayerLocation);
        registerDefault("bell", BellUtil::getModelLayerLocation);
        registerDefault("banner", BannerUtil::getModelLayerLocation);
        registerDefault("shulker_box", ShulkerBoxUtil::getModelLayerLocation);
        registerDefault("decorated_pot", DecoratedPotUtil::getModelLayerLocation);
        registerDefault("sign", SignUtil::getModelLayerLocation);
        registerDefault("hanging_sign", HangingSignUtil::getModelLayerLocation);
        registerDefault("bed", BedUtil::getModelLayerLocation);
    }

    public static void registerDefault(String group, Function<BlockState, ModelLayerLocation> getter){
        if(!Registry.hasGroup(group)){
            OBE.LOGGER.error("An external mod tried registering a default modelLayerLocation getter in a non existing group: " + group);
        }
        else{
            defaultModelLayerLocationsGetterProvider.put(group, getter);
        }
    }

    public static void register(BlockEntityType<?> beType, Function<BlockState, ModelLayerLocation> getter){
        modelLayerLocationsGetterProvider.put(beType, getter);
    }

    public static void register(Block block, Function<BlockState, ModelLayerLocation> getter){
        perBlockModelLayerLocationsGetterProvider.put(block, getter);
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return getModelLayerLocation(state, null);
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state, String group){
        if(!state.hasBlockEntity()) return null;

        Block block = state.getBlock();
        Function<BlockState, ModelLayerLocation> provider = perBlockModelLayerLocationsGetterProvider.get(block);
        if (provider != null) return provider.apply(state);

        BlockEntityType<?> beType = Registry.getBlockEntityType(state);
        if (beType == null) return null;
        provider = modelLayerLocationsGetterProvider.get(beType);
        if (provider != null) return provider.apply(state);

        if (group == null) group = Registry.getGroup(beType);
        if (group != null) provider = defaultModelLayerLocationsGetterProvider.get(group);
        if (provider != null) return provider.apply(state);

        return null;
    }
}

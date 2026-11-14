package fr.madu59.obe.client.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ModelGetter {
    private final static Map<BlockEntityType<?>, ModelBuilder> modelBuilders = new ConcurrentHashMap<>();
    private final static Map<Block, ModelBuilder> perBlockModelBuilders = new ConcurrentHashMap<>();
    private final static Map<String, ModelBuilder> defaultModelBuilders = new ConcurrentHashMap<>();

    public static void init(){
        registerDefault("decorated_pot", DecoratedPotUtil::getModel);
    }

    public static void registerDefault(String group, ModelBuilder builder){
        if(!Registry.hasGroup(group)){
            OBE.LOGGER.error("An external mod tried registering a default model builder in a non existing group: " + group);
        }
        else{
            defaultModelBuilders.put(group, builder);
        }
    }

    public static void register(BlockEntityType<?> beType, ModelBuilder builder){
        modelBuilders.put(beType, builder);
    }

    public static void register(Block block, ModelBuilder builder){
        perBlockModelBuilders.put(block, builder);
    }

    public static BlockStateModel getModel(BlockState state, BlockStateModel originalModel){
        return getModel(state, originalModel, null);
    }

    public static BlockStateModel getModel(BlockState state, BlockStateModel originalModel, String group){
        if(!state.hasBlockEntity()) return null;

        Block block = state.getBlock();
        ModelBuilder builder = perBlockModelBuilders.get(block);
        if (builder != null) return builder.build(state, originalModel);

        BlockEntityType<?> beType = Registry.getBlockEntityType(state);
        if (beType == null) return null;
        builder = modelBuilders.get(beType);
        if (builder != null) return builder.build(state, originalModel);

        if (group == null) group = Registry.getGroup(beType);
        if (group != null) builder = defaultModelBuilders.get(group);
        if (builder != null) return builder.build(state, originalModel);

        return null;
    }

    @FunctionalInterface
    public interface ModelBuilder {
        @Nullable
        BlockStateModel build(BlockState state, BlockStateModel originalModel);
    }
}

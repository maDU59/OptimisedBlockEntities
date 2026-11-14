package fr.madu59.obe.client.registry;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpecialModelBuilderGetter {

    private final static Map<BlockEntityType<?>, SpecialModelBuilder> specialModelBuilders = new ConcurrentHashMap<>();
    private final static Map<Block, SpecialModelBuilder> perBlockSpecialModelBuilders = new ConcurrentHashMap<>();
    private final static Map<String, SpecialModelBuilder> defaultSpecialModelBuilders = new ConcurrentHashMap<>();

    public static void init(){

    }

    public static void registerDefault(String group, SpecialModelBuilder getter){
        if(!Registry.hasGroup(group)){
            OBE.LOGGER.error("An external mod tried registering a default special model builder in a non existing group: " + group);
        }
        else{
            defaultSpecialModelBuilders.put(group, getter);
        }
    }

    public static void register(BlockEntityType<?> beType, SpecialModelBuilder getter){
        specialModelBuilders.put(beType, getter);
    }

    public static void register(Block block, SpecialModelBuilder getter){
        perBlockSpecialModelBuilders.put(block, getter);
    }

    public static SpecialModelBuilder getSpecialModelProvider(BlockState state){
        return getSpecialModelProvider(state, null);
    }

    public static SpecialModelBuilder getSpecialModelProvider(BlockState state, String group){
        if(!state.hasBlockEntity()) return null;

        Block block = state.getBlock();
        SpecialModelBuilder provider = perBlockSpecialModelBuilders.get(block);
        if (provider != null) return provider;

        BlockEntityType<?> beType = Registry.getBlockEntityType(state);
        if (beType == null) return null;
        provider = specialModelBuilders.get(beType);
        if (provider != null) return provider;

        if (group == null) group = Registry.getGroup(beType);
        if (group != null) provider = defaultSpecialModelBuilders.get(group);
        if (provider != null) return provider;
        
        return null;
    }

    public interface SpecialModelBuilder{
        @Nullable
        BlockStateModel build(BlockState state, BlockEntity entity, BlockStateModel originalModel);

        Object getCacheKey(BlockEntity entity);
    }
}

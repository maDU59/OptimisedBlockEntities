package fr.madu59.obe.client.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BackportUtil {
    public static <T extends Comparable<T>> T getValueOrElse(BlockState state, Property<T> property, T fallback){
        return state.hasProperty(property) ? state.getValue(property) : fallback;
    }
}

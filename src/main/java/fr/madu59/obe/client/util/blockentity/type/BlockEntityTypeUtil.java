package fr.madu59.obe.client.util.blockentity.type;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class BlockEntityTypeUtil {
    public static @Nullable BlockEntityType<?> getBlockEntityType(String namespace, String path){
        return getBlockEntityType(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public static @Nullable BlockEntityType<?> getBlockEntityType(ResourceLocation id){
        BlockEntityType<?> beType = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(id);
        return beType;
    }
}

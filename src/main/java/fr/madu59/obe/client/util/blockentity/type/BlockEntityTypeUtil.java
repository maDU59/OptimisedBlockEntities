package fr.madu59.obe.client.util.blockentity.type;

import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class BlockEntityTypeUtil {
    public static @Nullable BlockEntityType<?> getBlockEntityType(String namespace, String path){
        return getBlockEntityType(Identifier.fromNamespaceAndPath(namespace, path));
    }

    public static @Nullable BlockEntityType<?> getBlockEntityType(Identifier id){
        Optional<Reference<BlockEntityType<?>>> optional = BuiltInRegistries.BLOCK_ENTITY_TYPE.get(id);
        if(optional.isEmpty()) return null;
        return optional.get().value();
    }
}

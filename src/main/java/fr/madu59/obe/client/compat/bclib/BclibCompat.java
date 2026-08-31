package fr.madu59.obe.client.compat.bclib;

import java.util.List;

import fr.madu59.obe.client.registry.MaterialGetter;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.properties.ChestType;

public class BclibCompat {

    private static List<String> BCLIB_MODS = List.of("bclib","betternether","betterend");
    
    public static void init() {

        TagKey<Block> tag = TagKey.create(
            Registries.BLOCK, 
            Identifier.tryBuild("c", "chests")
        );

        Iterable<Holder<Block>> chestTagHolders = BuiltInRegistries.BLOCK.getTagOrEmpty(tag);

        for (Holder<Block> blockHolder : chestTagHolders) {
            Block block = blockHolder.value();
            
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);

            if (BCLIB_MODS.contains(blockId.getNamespace())) {
                MaterialGetter.register(block, (state) -> {
                    ChestType type = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);

                    String texturePath = blockId.getPath();

                    if(type == ChestType.LEFT) texturePath += "_left";
                    else if(type == ChestType.RIGHT) texturePath += "_right";
                    return Identifier.tryBuild(blockId.getNamespace(), "entity/chest/" + texturePath);
                });
            }
        }
    }
}

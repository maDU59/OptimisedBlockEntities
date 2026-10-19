package fr.madu59.obe.client.compat.morechestvariants;

import java.util.Calendar;

import dev.lieonlion.mcv.MoreChestVariants;
import dev.lieonlion.mcv.block.MoreChestBlock;
import dev.lieonlion.mcv.block.MoreTrappedChestBlock;
import dev.lieonlion.mcv.init.FabricMoreChestVariantsBlocks;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class MoreChestVariantsCompat {
    public static void init() {
        Registry.addBlockEntityTypeInGroup("chest", FabricMoreChestVariantsBlocks.MORE_CHEST_BLOCK_ENTITY, FabricMoreChestVariantsBlocks.MORE_TRAPPED_CHEST_BLOCK_ENTITY);

        for(Block block : FabricMoreChestVariantsBlocks.more_chest) {
            MaterialGetter.register(block, MoreChestVariantsCompat::getMaterial);
        }
        for(Block block : FabricMoreChestVariantsBlocks.more_trapped_chest) {
            MaterialGetter.register(block, MoreChestVariantsCompat::getMaterial);
        }
    }

    public static Identifier getMaterial(BlockState state) {
        if (!ChestUtil.isXmas() && state.getBlock() instanceof MoreChestBlock moreChestBlock) {
            ChestType type = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);
            if (lolmcv$starwars() && MoreChestVariants.CONFIG.displayStarWarsTextures()) {
                return lolmcv$chooseMaterial(type, "starwars");
            } else if (state.getBlock() instanceof MoreTrappedChestBlock) {
                return lolmcv$chooseMaterial(type, "trapped/" + moreChestBlock.woodType);
            }
            return lolmcv$chooseMaterial(type, moreChestBlock.woodType);
        }
        return ChestUtil.getMaterial(state);
    }

    private static boolean lolmcv$starwars() {
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(2) + 1 == 5 && calendar.get(5) >= 3 && calendar.get(5) <= 5);
    }


    private static Identifier lolmcv$getChestPath(String path) {
        return Identifier.fromNamespaceAndPath("lolmcv", "entity/chest/" + path);
    }

    private static Identifier lolmcv$chooseMaterial(ChestType type, String path) {
        return switch (type) {
            case LEFT -> lolmcv$getChestPath(path + "_left");
            case RIGHT -> lolmcv$getChestPath(path + "_right");
            default -> lolmcv$getChestPath(path);
        };
    }
}

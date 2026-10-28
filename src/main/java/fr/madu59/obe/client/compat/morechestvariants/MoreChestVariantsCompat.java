package fr.madu59.obe.client.compat.morechestvariants;

import java.util.Calendar;

import fr.madu59.obe.client.util.BackportUtil;
import io.github.lieonlion.mcv.block.MoreChestBlock;
import io.github.lieonlion.mcv.block.MoreTrappedChestBlock;
import io.github.lieonlion.mcv.init.McvBlockInit;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MoreChestVariantsCompat {
    public static void init() {
        Registry.addBlockEntityTypeInGroup("chest", McvBlockInit.MORE_CHEST_BLOCK_ENTITY.get(), McvBlockInit.MORE_TRAPPED_CHEST_BLOCK_ENTITY.get());

        MaterialGetter.register(McvBlockInit.MORE_CHEST_BLOCK_ENTITY.get(), MoreChestVariantsCompat::getMaterial);
        MaterialGetter.register(McvBlockInit.MORE_TRAPPED_CHEST_BLOCK_ENTITY.get(), MoreChestVariantsCompat::getMaterial);
    }

    public static ResourceLocation getMaterial(BlockState state) {
        if (!ChestUtil.isXmas() && state.getBlock() instanceof MoreChestBlock moreChestBlock) {
            ChestType type = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);
            if (lolmcv$starwars()) {
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


    private static ResourceLocation lolmcv$getChestPath(String path) {
        return ResourceLocation.fromNamespaceAndPath("lolmcv", "entity/chest/" + path);
    }

    private static ResourceLocation lolmcv$chooseMaterial(ChestType type, String path) {
        return switch (type) {
            case LEFT -> lolmcv$getChestPath(path + "_left");
            case RIGHT -> lolmcv$getChestPath(path + "_right");
            default -> lolmcv$getChestPath(path);
        };
    }
}

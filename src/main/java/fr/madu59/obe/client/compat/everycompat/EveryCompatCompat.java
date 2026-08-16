package fr.madu59.obe.client.compat.everycompat;

import fr.madu59.obe.client.util.BackportUtil;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.common_classes.CompatTrappedChestBlock;
import net.mehvahdjukaar.every_compat.misc.HardcodedBlockType;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class EveryCompatCompat {

    public static ResourceLocation getChestMaterial(BlockState state){
        WoodType woodType = (WoodType)WoodTypeRegistry.INSTANCE.getBlockTypeOf(state.getBlock());
        woodType = woodType == null ? VanillaWoodTypes.OAK : woodType;
        boolean isTrapped = state.getBlock() instanceof CompatTrappedChestBlock;
        ChestType chestType = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);

        if (HardcodedBlockType.isKnownVanillaWood(woodType)) return null;
        String shortenedId = Utils.getID(state.getBlock()).getPath().split("/")[0];
        String path = "entity/chest/" + shortenedId + "/" + woodType.getAppendableId() + "_chest";
        String trapped_path = "entity/chest/" + shortenedId + "/" + woodType.getAppendableId() + "_trapped_chest";

        if (isTrapped) {
            return switch (chestType) {
                case LEFT -> EveryCompat.res(path + "_left");
                case RIGHT -> EveryCompat.res(path + "_right");
                default -> EveryCompat.res(path);
            };
        } else {
            if (ChestUtil.isXmas && shortenedId.equals("abnww")) {
                return switch (chestType) {
                    case LEFT -> Sheets.CHEST_XMAS_LOCATION_LEFT.texture();
                    case RIGHT -> Sheets.CHEST_XMAS_LOCATION_RIGHT.texture();
                    default -> Sheets.CHEST_XMAS_LOCATION.texture();
                };
            } else {
                return switch (chestType) {
                    case LEFT -> EveryCompat.res(trapped_path + "_left");
                case RIGHT -> EveryCompat.res(trapped_path + "_right");
                default -> EveryCompat.res(trapped_path);
                };
            }
        }
    }
}

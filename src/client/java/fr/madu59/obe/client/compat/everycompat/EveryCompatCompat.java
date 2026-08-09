package fr.madu59.obe.client.compat.everycompat;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.util.BackportUtil;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.common_classes.CompatChestBlockEntity;
import net.mehvahdjukaar.every_compat.common_classes.CompatChestBlockRenderer;
import net.mehvahdjukaar.every_compat.common_classes.CompatTrappedChestBlock;
import net.mehvahdjukaar.every_compat.misc.HardcodedBlockType;
import net.mehvahdjukaar.every_compat.modules.lieonlion.MoreChestVariantsModule;
import net.mehvahdjukaar.every_compat.modules.variants.VariantVanillaBlocksModule;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class EveryCompatCompat {

    public static void init(){
        // Not future proof, a way to get all the BlockEntityTypes would be better imo
        EveryCompat.forAllModules(m -> {
            if(m instanceof MoreChestVariantsModule module){
                Registry.addBlockEntityTypeInGroup("chest", module.chests.getTile(CompatChestBlockEntity.class));
            }
            else if(m instanceof VariantVanillaBlocksModule module){
                Registry.addBlockEntityTypeInGroup("chest", module.chests.getTile(CompatChestBlockEntity.class));
            }
            // Neoforge only
            // else if(m instanceof QuarkModule module){
            //     Registry.addBlockEntityTypeInGroup("chest", module.chests.getTile(CompatChestBlockEntity.class));
            // }
            // else if(m instanceof WoodWorksModule module){
            //     Registry.addBlockEntityTypeInGroup("chest", module.chests.getTile(CompatChestBlockEntity.class));
            // }
        });
    }

    public static ResourceLocation getChestMaterial(BlockState state, BlockEntity be){

        if(be instanceof CompatChestBlockEntity compatChestBe){
            WoodType woodType = (WoodType)WoodTypeRegistry.INSTANCE.getBlockTypeOf(state.getBlock());
            woodType = woodType == null ? VanillaWoodTypes.OAK : woodType;
            boolean isTrapped = state.getBlock() instanceof CompatTrappedChestBlock;
            ChestType chestType = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);

            // Would be much better if there was a way to do getShortenedId(state.getBlock()), would also remove the need for the block entity which means it could go through the "fast" path
            BlockEntityRenderer<?> beRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(compatChestBe);
            if(beRenderer instanceof CompatChestBlockRenderer compatChestBeRenderer){
                String shortenedId = compatChestBeRenderer.shortenedId;

                if (HardcodedBlockType.isKnownVanillaWood(woodType)) return null;
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
        return null;
    }
}

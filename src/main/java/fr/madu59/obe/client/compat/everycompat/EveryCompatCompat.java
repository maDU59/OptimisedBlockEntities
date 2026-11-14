package fr.madu59.obe.client.compat.everycompat;

import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.mehvahdjukaar.every_compat.EveryCompat;
import net.mehvahdjukaar.every_compat.common_classes.CompatTrappedChestBlock;
import net.mehvahdjukaar.every_compat.misc.HardcodedBlockType;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class EveryCompatCompat {

    public static final Identifier CHEST_SHEET = Identifier.withDefaultNamespace("textures/atlas/chest.png");

    public static Identifier getChestMaterial(BlockState state){
        WoodType woodType  = (WoodType)WoodTypeRegistry.INSTANCE.getBlockTypeOf(state.getBlock());
        woodType  = woodType  == null ? VanillaWoodTypes.OAK : woodType ;
        boolean isTrapped = state.getBlock() instanceof CompatTrappedChestBlock;
        ChestType chestType = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);

        if (HardcodedBlockType.isKnownVanillaWood(woodType)) return null;
        String shortenedId = Utils.getID(state.getBlock()).getPath().split("/")[0];
        String path = "entity/chest/" + shortenedId + "/" + woodType.getAppendableId() + "_chest";
        String trapped_path = "entity/chest/" + shortenedId + "/" + woodType.getAppendableId() + "_trapped_chest";

        if (isTrapped) {
            return chestSprites(trapped_path).select(chestType).texture();
        } else {
            return ChestUtil.isXmas() && shortenedId.equals("abnww") ? Sheets.chooseSprite(ChestRenderState.ChestMaterialType.CHRISTMAS, chestType).texture() : chestSprites(path).select(chestType).texture();
        }
    }

    private static MultiblockChestResources<SpriteId> chestSprites(String path) {
        return new MultiblockChestResources(new SpriteId(CHEST_SHEET, EveryCompat.res(path)), new SpriteId(CHEST_SHEET, EveryCompat.res(path + "_left")), new SpriteId(CHEST_SHEET, EveryCompat.res(path + "_right")));
    }
}
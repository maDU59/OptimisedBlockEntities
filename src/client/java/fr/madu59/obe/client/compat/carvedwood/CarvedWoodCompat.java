package fr.madu59.obe.client.compat.carvedwood;

import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.util.BackportUtil;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.im_maker.carved_wood.CarvedWood;
import net.im_maker.carved_wood.client.renderer.CWModelLayers;
import net.im_maker.carved_wood.common.block.CWChestBlock;
import net.im_maker.carved_wood.common.block.CWTrappedChestBlock;
import net.im_maker.carved_wood.common.item.CWChestBlockItem;
import net.im_maker.carved_wood.common.registers.CWBlockEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class CarvedWoodCompat {

    public static void init(){
        Registry.addBlockEntityTypeInGroup("chest", CWBlockEntities.CHEST.get(), CWBlockEntities.TRAPPED_CHEST.get());
        MaterialGetter.register(CWBlockEntities.CHEST.get(), CarvedWoodCompat::getChestMaterial);
        MaterialGetter.register(CWBlockEntities.TRAPPED_CHEST.get(), CarvedWoodCompat::getChestMaterial);
        ModelLayerLocationGetter.register(CWBlockEntities.CHEST.get(), CarvedWoodCompat::getChestModelLayerLocation);
        ModelLayerLocationGetter.register(CWBlockEntities.TRAPPED_CHEST.get(), CarvedWoodCompat::getChestModelLayerLocation);
    }

    public static ModelLayerLocation getChestModelLayerLocation(BlockState state){
        ChestType chestType = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);
        return switch (chestType) {
            case RIGHT -> CWModelLayers.DOUBLE_CHEST_RIGHT;
            case LEFT -> CWModelLayers.DOUBLE_CHEST_LEFT;
            case SINGLE -> CWModelLayers.CHEST;
        };
    }

    public static ResourceLocation getChestMaterial(BlockState state){
        Block block = state.getBlock();
        Item item = block.asItem();
        String chestName = item instanceof CWChestBlockItem ?
        ((CWChestBlockItem) item).getChestName() :
        block instanceof CWTrappedChestBlock ?
                ((CWTrappedChestBlock) block).getChestName()
                : ((CWChestBlock) block).getChestName();

        ChestType chestType = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);

        Material CHEST_TRAP_LOCATION = chestMaterial(chestName + "/trapped");
        Material CHEST_TRAP_LOCATION_LEFT = chestMaterial(chestName + "/trapped_left");
        Material CHEST_TRAP_LOCATION_RIGHT = chestMaterial( chestName + "/trapped_right");
        Material CHEST_LOCATION = chestMaterial(chestName + "/normal");
        Material CHEST_LOCATION_LEFT = chestMaterial(chestName + "/normal_left");
        Material CHEST_LOCATION_RIGHT = chestMaterial(chestName + "/normal_right");
        if (ChestUtil.isXmas) {
            return ChestUtil.chooseMaterial(chestType, Sheets.CHEST_XMAS_LOCATION, Sheets.CHEST_XMAS_LOCATION_LEFT, Sheets.CHEST_XMAS_LOCATION_RIGHT);
        } else {
            return (block instanceof CWTrappedChestBlock || block instanceof TrappedChestBlock) ? ChestUtil.chooseMaterial(chestType, CHEST_TRAP_LOCATION, CHEST_TRAP_LOCATION_LEFT, CHEST_TRAP_LOCATION_RIGHT) : ChestUtil.chooseMaterial(chestType, CHEST_LOCATION, CHEST_LOCATION_LEFT, CHEST_LOCATION_RIGHT);
        }
    }

    private static Material chestMaterial(String pChestName) {
        return new Material(Sheets.CHEST_SHEET, CarvedWood.newRes(CarvedWood.MOD_ID, "entity/chest/" + pChestName));
    }
}

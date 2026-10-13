package fr.madu59.obe.client.compat.carvedwood;

import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.im_maker.carved_wood.client.renderer.CWModelLayers;
import net.im_maker.carved_wood.client.renderer.ChestSheets;
import net.im_maker.carved_wood.common.block.CWChestBlock;
import net.im_maker.carved_wood.common.block.CWTrappedChestBlock;
import net.im_maker.carved_wood.common.item.ChestBlockItem;
import net.im_maker.carved_wood.common.registers.CWBlockEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
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
        ChestType chestType = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);
        return switch (chestType) {
            case RIGHT -> CWModelLayers.DOUBLE_CHEST_RIGHT;
            case LEFT -> CWModelLayers.DOUBLE_CHEST_LEFT;
            case SINGLE -> CWModelLayers.CHEST;
        };
    }

    public static Identifier getChestMaterial(BlockState state){
        Block block = state.getBlock();
        Item item = block.asItem();
        String chestName = item instanceof ChestBlockItem ?
        ((ChestBlockItem) item).getChestName() :
        block instanceof CWTrappedChestBlock ?
                ((CWTrappedChestBlock) block).getChestName()
                : ((CWChestBlock) block).getChestName();

        ChestType chestType = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);
        return ChestSheets.chooseMaterial(chestName, (block instanceof CWTrappedChestBlock || block instanceof TrappedChestBlock), chestType, ChestUtil.isXmas).texture();
    }
}
package fr.madu59.obe.client.compat.twilightforest;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.util.BackportUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import twilightforest.block.TFTrappedChestBlock;
import twilightforest.client.renderer.block.TFChestRenderer;
import twilightforest.init.TFBlockEntities;

public class TwilightForestCompat {
    public static void init() {
        try {
            Object holder = TFBlockEntities.class.getField("TF_CHEST").get(null);
            BlockEntityType<?> TF_CHEST = (BlockEntityType<?>) holder.getClass().getMethod("get").invoke(holder);
            BlockEntityType<?> TF_TRAPPED_CHEST = (BlockEntityType<?>) holder.getClass().getMethod("get").invoke(holder);

            RegistryApi.registerBlockEntityType("chest", TF_CHEST, TF_TRAPPED_CHEST);
            RegistryApi.registerMaterialProvider(TF_CHEST, TwilightForestCompat::getMaterial);
            RegistryApi.registerMaterialProvider(TF_TRAPPED_CHEST, TwilightForestCompat::getMaterial);

            for(Block block : TFChestRenderer.MATERIALS.keySet()){
                Registry.registerBlock(block, block instanceof TFTrappedChestBlock? TF_TRAPPED_CHEST : TF_CHEST);
            }
        } catch (Exception e) {
            OBE.LOGGER.warn("Failed to register Twilight Forest compat, skipping. This is likely due to a version mismatch: {}", e);
        }
    }

    public static ResourceLocation getMaterial(BlockState state) {
        ChestType type = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);
        return TFChestRenderer.MATERIALS.get(state.getBlock()).get(type).texture();
    }
}
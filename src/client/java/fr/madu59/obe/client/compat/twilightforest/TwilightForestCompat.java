package fr.madu59.obe.client.compat.twilightforest;

import fr.madu59.obe.client.api.registry.RegistryApi;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import twilightforest.client.renderer.block.TFChestRenderer;
import twilightforest.init.TFBlockEntities;

public class TwilightForestCompat {
    public static void init() {
        RegistryApi.registerBlockEntityType("chest", TFBlockEntities.TF_CHEST, TFBlockEntities.TF_TRAPPED_CHEST);
        RegistryApi.registerMaterialProvider(TFBlockEntities.TF_CHEST, TwilightForestCompat::getMaterial);
        RegistryApi.registerMaterialProvider(TFBlockEntities.TF_TRAPPED_CHEST, TwilightForestCompat::getMaterial);
    }

    public static Identifier getMaterial(BlockState state) {
        ChestType type = state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE);
        return TFChestRenderer.MATERIALS.get(state.getBlock()).get(type).texture();
    }
}

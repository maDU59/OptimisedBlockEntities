package fr.madu59.obe.client.compat.twilightforest;

import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.util.BackportUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import twilightforest.client.renderer.tileentity.TwilightChestRenderer;
import twilightforest.init.TFBlockEntities;

public class TwilightForestCompat {
    public static void init() {
        RegistryApi.registerBlockEntityType("chest", TFBlockEntities.TF_CHEST.get());
        RegistryApi.registerMaterialProvider(TFBlockEntities.TF_CHEST.get(), TwilightForestCompat::getMaterial);
    }

    public static ResourceLocation getMaterial(BlockState state) {
        ChestType type = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);
        return TwilightChestRenderer.MATERIALS.get(state.getBlock()).get(type).texture();
    }
}
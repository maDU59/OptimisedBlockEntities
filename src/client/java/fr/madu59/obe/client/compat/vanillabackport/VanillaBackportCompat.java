package fr.madu59.obe.client.compat.vanillabackport;

import com.blackgear.vanillabackport.common.level.blocks.CopperChestBlock;
import com.blackgear.vanillabackport.common.registries.blocks.ModBlockEntities;
import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.util.BackportUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class VanillaBackportCompat {
    public static void init() {
        RegistryApi.registerBlockEntityType("chest", ModBlockEntities.COPPER_CHEST.get());

        RegistryApi.registerMaterialProvider(ModBlockEntities.COPPER_CHEST.get(), VanillaBackportCompat::getMaterial);
    }

    public static ResourceLocation getMaterial(BlockState state) {
        if(state.getBlock() instanceof CopperChestBlock chest){
            ChestType type = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);
            return getChestMaterialType(chest, type);
        }
        else return null;
    }

    private static ResourceLocation getChestMaterialType(CopperChestBlock chest, ChestType type) {
        return switch (chest.getState()) {
            case EXPOSED -> getChestType(type, "copper_exposed");
            case WEATHERED -> getChestType(type, "copper_weathered");
            case OXIDIZED -> getChestType(type, "copper_oxidized");
            default -> getChestType(type, "copper");
        };
    }

    private static ResourceLocation getChestType(ChestType type, String chest) {
        return switch (type) {
            case LEFT -> chestMaterial(chest + "_left");
            case RIGHT -> chestMaterial(chest + "_right");
            default -> chestMaterial(chest);
        };
    }

    private static ResourceLocation chestMaterial(String chestName) {
        return new ResourceLocation("entity/chest/" + chestName);
    }
}

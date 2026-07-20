package fr.madu59.obe.client.compat.blueprint;

import com.teamabnormals.blueprint.client.BlueprintChestMaterials;
import com.teamabnormals.blueprint.client.BlueprintChestMaterials.ChestMaterials;
import com.teamabnormals.blueprint.core.api.IChestBlock;
import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;

import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.util.BackportUtil;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class BlueprintCompat {
    public static void init(){
        Registry.addBlockEntityTypeInGroup("chest", BlueprintBlockEntityTypes.CHEST.get(), BlueprintBlockEntityTypes.TRAPPED_CHEST.get());
        
        MaterialGetter.register(BlueprintBlockEntityTypes.CHEST.get(), BlueprintCompat::getChestMaterial);
        MaterialGetter.register(BlueprintBlockEntityTypes.TRAPPED_CHEST.get(), BlueprintCompat::getChestMaterial);
    }

    public static ResourceLocation getChestMaterial(BlockState state) {
        ChestType chestType = BackportUtil.getValueOrElse(state, ChestBlock.TYPE, ChestType.SINGLE);
        if (ChestUtil.isXmas) {
            return switch (chestType) {
                case SINGLE -> Sheets.CHEST_XMAS_LOCATION.texture();
                case LEFT -> Sheets.CHEST_XMAS_LOCATION_LEFT.texture();
                case RIGHT -> Sheets.CHEST_XMAS_LOCATION_RIGHT.texture();
            };
        } else {
            Block block = state.getBlock();
            ChestMaterials chestMaterials = BlueprintChestMaterials.getMaterials(((IChestBlock) block).getChestMaterialsName());
            return switch (chestType) {
                case SINGLE -> chestMaterials != null ? chestMaterials.singleMaterial().texture() : Sheets.CHEST_LOCATION.texture();
                case LEFT -> chestMaterials != null ? chestMaterials.leftMaterial().texture() : Sheets.CHEST_LOCATION_LEFT.texture();
                case RIGHT -> chestMaterials != null ? chestMaterials.rightMaterial().texture() : Sheets.CHEST_LOCATION_RIGHT.texture();
            };
        }
    }
}
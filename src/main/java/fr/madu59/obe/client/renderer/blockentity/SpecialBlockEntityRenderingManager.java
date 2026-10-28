package fr.madu59.obe.client.renderer.blockentity;

import java.util.List;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.sign.ext.SignTextExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SpecialBlockEntityRenderingManager {
    public static boolean shouldSkipRendering(BlockEntity be) {
        if(!SettingsManager.MOD_TOGGLE.getValue()) return false;
        switch(((BlockEntityExt)be).obe$specialKind()){
            case 1 -> { // Sign
                return SettingsManager.OPTIMISED_SIGNS.getValue() && ((BlockEntityExt)be).obe$isSupported() && isEmpty((SignBlockEntity)be);
            }
            case 2 -> { // Beacon
                return SettingsManager.OPTIMISED_BEACONS.getValue() && ((BeaconBlockEntity)be).getBeamSections().isEmpty();
            }
            case 3 -> { // Campfire
                return SettingsManager.OPTIMISED_CAMPFIRES.getValue() && isContainerEmpty(((CampfireBlockEntity)be).getItems());
            }
            case 4 -> { // Shelf
                // return SettingsManager.OPTIMISED_SHELVES.getValue() && isContainerEmpty(((ShelfBlockEntity)be).getItems());
                return false;
            }
            case 5 -> { // Spawner
                SpawnerBlockEntity spawnerBe = (SpawnerBlockEntity)be;
                return SettingsManager.OPTIMISED_SPAWNERS.getValue() && spawnerBe.getSpawner().getOrCreateNextSpawnData(spawnerBe.getLevel(), spawnerBe.getLevel().getRandom(), spawnerBe.getBlockPos()).getEntityToSpawn().getString("id").isEmpty();
            }
            case 6 -> { // Vault
                return SettingsManager.OPTIMISED_VAULTS.getValue() && ((VaultBlockEntity)be).getSharedData().getDisplayItem().isEmpty();
            }
            case 7 -> { // Brushable
                BrushableBlockEntity brushableBe = (BrushableBlockEntity)be;
                return SettingsManager.OPTIMISED_BRUSHABLES.getValue() && (brushableBe.getHitDirection() == null || brushableBe.getItem().isEmpty() || brushableBe.getBlockState().getValue(BlockStateProperties.DUSTED) <= 0);
            }
            default -> {
                return false;
            }
        }
    }

    private static boolean isContainerEmpty(List<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) != null && !items.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(SignBlockEntity be){
        LocalPlayer player = Minecraft.getInstance().player;
        return !hasMessage(be.getText(true), player) && !hasMessage(be.getText(false), player);
    }

    private static boolean hasMessage(SignText text, LocalPlayer player){
        return text != null && ((SignTextExt) text).obe$hasMessage(player);
    }
}

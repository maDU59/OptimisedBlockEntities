package fr.madu59.obe.client.renderer.blockentity;

import java.util.List;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.sign.ext.SignTextExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SpecialBlockEntityRenderingManager {
    public static boolean shouldSkipRendering(BlockEntity be) {
        if(!SettingsManager.MOD_TOGGLE.getValue()) return false;
        else if(be instanceof SignBlockEntity signBe){
            return SettingsManager.OPTIMISED_SIGNS.getValue() && isEmpty(signBe);
        }
        else if(be instanceof BeaconBlockEntity beaconBe){
            return SettingsManager.OPTIMISED_BEACONS.getValue() && beaconBe.getBeamSections().isEmpty();
        }
        else if(be instanceof CampfireBlockEntity campfireBe){
            return SettingsManager.OPTIMISED_CAMPFIRES.getValue() && isContainerEmpty(campfireBe.getItems());
        }
        else if(be instanceof ShelfBlockEntity shelfBe){
            return SettingsManager.OPTIMISED_SHELVES.getValue() && isContainerEmpty(shelfBe.getItems());
        }
        else if(be instanceof SpawnerBlockEntity spawnerBe){
            return SettingsManager.OPTIMISED_SPAWNERS.getValue() && spawnerBe.getSpawner().getOrCreateNextSpawnData(spawnerBe.getLevel(), spawnerBe.getLevel().getRandom(), spawnerBe.getBlockPos()).getEntityToSpawn().getString("id").isEmpty();
        }
        else if(be instanceof VaultBlockEntity vaultBe){
            return SettingsManager.OPTIMISED_VAULTS.getValue() && vaultBe.getSharedData().getDisplayItem().isEmpty();
        }
        else if(be instanceof BrushableBlockEntity brushableBe){
            return SettingsManager.OPTIMISED_BRUSHABLES.getValue() && (brushableBe.getHitDirection() == null || brushableBe.getItem().isEmpty() || brushableBe.getBlockState().getValue(BlockStateProperties.DUSTED) <= 0);
        }
        return false;
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

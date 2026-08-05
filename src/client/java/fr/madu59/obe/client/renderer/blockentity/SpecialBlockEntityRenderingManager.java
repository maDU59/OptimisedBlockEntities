package fr.madu59.obe.client.renderer.blockentity;

import java.util.List;

import fr.madu59.obe.client.config.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

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
        return (be.getText(true) == null || !be.getText(true).hasMessage(player)) && (be.getText(false) == null || !be.getText(false).hasMessage(player));
    }
}

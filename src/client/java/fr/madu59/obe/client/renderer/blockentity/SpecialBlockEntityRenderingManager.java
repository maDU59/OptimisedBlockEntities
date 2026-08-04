package fr.madu59.obe.client.renderer.blockentity;

import fr.madu59.obe.client.config.SettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignTextSlot;

public class SpecialBlockEntityRenderingManager {
    public static boolean shouldSkipRendering(BlockEntity be) {
        if(!SettingsManager.MOD_TOGGLE.getValue()) return false;
        else if(be instanceof SignBlockEntity signBe && SettingsManager.OPTIMISED_SIGNS.getValue()){
            return isEmpty(signBe);
        }
        else if(be instanceof BeaconBlockEntity beaconBe && SettingsManager.OPTIMISED_BEACONS.getValue()){
            return beaconBe.getBeamSections().isEmpty();
        }
        else if(be instanceof CampfireBlockEntity campfireBe && SettingsManager.OPTIMISED_CAMPFIRES.getValue()){
            for(ItemStack itemStack : campfireBe.getItems()){
                if(itemStack != null && itemStack != ItemStack.EMPTY) return false;
            }
            return true;
        }
        else if(be instanceof ShelfBlockEntity shelfBe && SettingsManager.OPTIMISED_SHELVES.getValue()){
            for(ItemStack itemStack : shelfBe.getItems()){
                if(itemStack != null && itemStack != ItemStack.EMPTY) return false;
            }
            return true;
        }
        return false;
    }

    private static boolean isEmpty(SignBlockEntity be){
        boolean shouldFilter = Minecraft.getInstance().player.isTextFilteringEnabled();
        return (be.getText(SignTextSlot.FRONT) == null || !be.getText(SignTextSlot.FRONT).hasMessage(shouldFilter)) && (be.getText(SignTextSlot.BACK) == null || !be.getText(SignTextSlot.BACK).hasMessage(shouldFilter));
    }
}

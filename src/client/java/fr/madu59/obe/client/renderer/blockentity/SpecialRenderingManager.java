package fr.madu59.obe.client.renderer.blockentity;

import java.util.List;

import fr.madu59.obe.client.config.SettingsManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SpecialRenderingManager {
    public static boolean shouldSkipRendering(BlockEntity be) {
        if(!SettingsManager.MOD_TOGGLE.getValue()) return false;
        else if(be instanceof SignBlockEntity signBe && SettingsManager.OPTIMISED_SIGNS.getValue()){
            return !hastext(signBe);
        }
        else if(be instanceof BeaconBlockEntity beaconBe && SettingsManager.OPTIMISED_BEACONS.getValue()){
            return beaconBe.getBeamSections().isEmpty();
        }
        else if(be instanceof CampfireBlockEntity campfireBe && SettingsManager.OPTIMISED_CAMPFIRES.getValue()){
            List<ItemStack> list = campfireBe.getItems();
            if(list == null) return true;
            for(ItemStack itemStack : list){
                if(itemStack != null && itemStack != ItemStack.EMPTY) return false;
            }
            return true;
        }
        else if(be instanceof ShelfBlockEntity shelfBe && SettingsManager.OPTIMISED_SHELVES.getValue()){
            List<ItemStack> list = shelfBe.getItems();
            if(list == null) return true;
            for(ItemStack itemStack : list){
                if(itemStack != null && itemStack != ItemStack.EMPTY) return false;
            }
            return true;
        }
        return false;
    }

    private static boolean hastext(SignBlockEntity be){
        if(be.getText(true) == null || be.getText(false) == null) return false;
        for(int i = 0; i < 4; i++){
            if(!be.getText(true).getMessages(false)[i].getString().isEmpty()) return true;
            if(!be.getText(false).getMessages(false)[i].getString().isEmpty()) return true;
        }
        return false;
    }
}

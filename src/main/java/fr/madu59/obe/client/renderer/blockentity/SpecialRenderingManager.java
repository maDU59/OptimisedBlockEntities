package fr.madu59.obe.client.renderer.blockentity;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;

public class SpecialRenderingManager {
    public static boolean shouldSkipRendering(BlockEntity be) {
        if(!SettingsManager.MOD_TOGGLE.getValue()) return false;
        else if(be instanceof SignBlockEntity signBe && SettingsManager.OPTIMISED_SIGNS.getValue() && ((BlockEntityExt)be).isSupportedBlockEntity()){
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
        return false;
    }

    private static boolean isEmpty(SignBlockEntity be){
        if(be.getText(true) == null || be.getText(false) == null) return false;
        Component[] frontMessages = be.getText(true).getMessages(false);
        Component[] backMessages = be.getText(false).getMessages(false);
        for(int i = 0; i < 4; i++){
            if(!frontMessages[i].getString().isEmpty()) return false;
            if(!backMessages[i].getString().isEmpty()) return false;
        }
        return true;
    }
}

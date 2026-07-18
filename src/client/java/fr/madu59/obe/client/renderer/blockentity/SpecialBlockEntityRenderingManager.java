package fr.madu59.obe.client.renderer.blockentity;

import java.util.List;

import fr.madu59.obe.client.config.SettingsManager;
import net.minecraft.network.chat.Component;
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
        if(be.getText(SignTextSlot.FRONT) == null || be.getText(SignTextSlot.BACK) == null) return false;
        List<Component> frontMessages = be.getText(SignTextSlot.FRONT).getMessages(false);
        List<Component> backMessages = be.getText(SignTextSlot.BACK).getMessages(false);
        for(Component text: frontMessages){
            if(!text.getString().isEmpty()) return false;
        }
        for(Component text: backMessages){
            if(!text.getString().isEmpty()) return false;
        }
        return true;
    }
}

package fr.madu59.obe.client.mixin.blockentity.campfire;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin{

    @Shadow
    private NonNullList<ItemStack> items;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRenderingAndUpdate(obe$isEmptyList(items));
    }

    @Inject(method = "cookTick", at = @At("TAIL"))
    private static void obe$tick(CallbackInfo ci, @Local CampfireBlockEntity be) {
        
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRenderingAndUpdate(obe$isEmptyList(be.items));
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void obe$load(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRenderingAndUpdate(obe$isEmptyList(items));
    }

    @Inject(method = "placeFood", at = @At("TAIL"))
    private void obe$placeFood(CallbackInfoReturnable<Boolean> ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRenderingAndUpdate(obe$isEmptyList(items));
    }

    @Inject(method = "applyImplicitComponents", at = @At("TAIL"))
    private void obe$applyComponents(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRenderingAndUpdate(obe$isEmptyList(items));
    }

    @Unique
    private static boolean obe$isEmptyList(List<ItemStack> list){
        if(!SettingsManager.OPTIMISED_CAMPFIRES.getValue() || !SettingsManager.MOD_TOGGLE.getValue()) return false;
        if(list == null) return true;
        for(ItemStack itemStack : list){
            if(itemStack != null && itemStack != ItemStack.EMPTY) return false;
        }
        return true;
    }
}

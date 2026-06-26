package fr.madu59.obe.client.mixin.blockentity.shelf;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;

@Mixin(ShelfBlockEntity.class)
public abstract class ShelfBlockEntityMixin{

    @Shadow
    private NonNullList<ItemStack> items;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRendering(obe$isEmptyList(items));
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void obe$load(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRendering(obe$isEmptyList(items));
    }

    @Inject(method = "applyImplicitComponents", at = @At("TAIL"))
    private void obe$applyComponents(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRendering(obe$isEmptyList(items));
    }

    @Unique
    private static boolean obe$isEmptyList(List<ItemStack> list){
        if(!SettingsManager.OPTIMISED_SHELVES.getValue() || !SettingsManager.MOD_TOGGLE.getValue()) return false;
        if(list == null) return true;
        for(ItemStack itemStack : list){
            if(itemStack != null && itemStack != ItemStack.EMPTY) return false;
        }
        return true;
    }
}

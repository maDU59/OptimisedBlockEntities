package fr.madu59.obe.client.mixin.blockentity.beacon;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity.BeaconBeamSection;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin{

    @Shadow
    private List<BeaconBeamSection> beamSections;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRendering(obe$isEmptyList(this.beamSections));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private static void obe$tick(CallbackInfo ci, @Local BeaconBlockEntity be) {
        
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRendering(obe$isEmptyList(be.getBeamSections()));
    }

    @Unique
    private static boolean obe$isEmptyList(List<BeaconBeamSection> list){
        if(!SettingsManager.OPTIMISED_BEACONS.getValue() || !SettingsManager.MOD_TOGGLE.getValue()) return false;
        if(list == null) return true;
        return list.isEmpty();
    }
}

package fr.madu59.obe.client.mixin.blockentity.renderers.bed;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.world.level.block.entity.BedBlockEntity;

@Mixin(BedRenderer.class)
public abstract class BedRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(CallbackInfo ci, @Local BedBlockEntity be){
        if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_BEDS.getValue(), be)) ci.cancel();
    }
}
package fr.madu59.obe.client.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.SpecialBlockEntityRenderingManager;
import fr.madu59.obe.client.renderer.entity.ext.EntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.level.block.entity.BlockEntity;

@Pseudo
@Mixin(value = SodiumWorldRenderer.class, remap = false)
public class SodiumWorldRendererMixin {

    @Inject(method = "extractBlockEntity", at = @At("HEAD"), cancellable = true)
    public void obe$preventUselessExtraction(CallbackInfo ci, @Local BlockEntity be){
        if(SpecialBlockEntityRenderingManager.shouldSkipRendering(be)){
            ci.cancel();
        }
    }

    @Inject(
        method = "isEntityVisible",
        at = @At("HEAD"),
        cancellable = true
    )
    private <T extends Entity> void obe$shouldRenderEntity(CallbackInfoReturnable<Boolean> cir, @Local T entity) {
        if(entity instanceof EntityExt ext && ext.isSupported()){
            boolean modToggle = SettingsManager.MOD_TOGGLE.getValue();
            if(entity instanceof Cushion) ext.isEnabled(SettingsManager.OPTIMISED_CUSHIONS.getValue() && modToggle);
            if(ext.isEnabled() && ext.renderMode() == RenderMode.TERRAIN && !entity.shouldShowName()){
                cir.setReturnValue(false);
            }
        }
    }
}

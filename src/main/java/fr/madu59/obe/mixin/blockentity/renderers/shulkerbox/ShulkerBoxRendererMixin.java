package fr.madu59.obe.mixin.blockentity.renderers.shulkerbox;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.config.SettingsManager;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

@Mixin(ShulkerBoxRenderer.class)
public abstract class ShulkerBoxRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(CallbackInfo ci, @Local ShulkerBoxBlockEntity be){
        if(!RenderModeManager.shouldRenderEntity(be) && SettingsManager.OPTIMISED_SHULKER_BOXES.getValue()) ci.cancel();
        RenderModeManager.updateOnRender(be);
    }
}

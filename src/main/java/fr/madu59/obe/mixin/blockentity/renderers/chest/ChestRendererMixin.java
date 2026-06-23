package fr.madu59.obe.mixin.blockentity.renderers.chest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.config.SettingsManager;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;

@Mixin(ChestRenderer.class)
public abstract class ChestRendererMixin<T extends BlockEntity & LidBlockEntity> {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(CallbackInfo ci, @Local T be){
        if(!RenderModeManager.shouldRenderEntity(be) && SettingsManager.OPTIMISED_CHESTS.getValue()) ci.cancel();
        RenderModeManager.updateOnRender(be);
    }
}

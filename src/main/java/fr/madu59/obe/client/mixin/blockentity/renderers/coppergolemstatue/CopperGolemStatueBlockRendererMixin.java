package fr.madu59.obe.client.mixin.blockentity.renderers.coppergolemstatue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.blockentity.CopperGolemStatueBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.CopperGolemStatueRenderState;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;

@Mixin(CopperGolemStatueBlockRenderer.class)
public abstract class CopperGolemStatueBlockRendererMixin {
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(CallbackInfo ci, @Local CopperGolemStatueRenderState state){
        if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_COPPER_GOLEMS.getValue(), state)) ci.cancel();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void obe$cancelExtract(CallbackInfo ci, @Local CopperGolemStatueRenderState state, @Local CopperGolemStatueBlockEntity be){
        ((BlockEntityRenderStateExt)state).blockEntity(be);
         if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_COPPER_GOLEMS.getValue(), be)) ci.cancel();
    }
}

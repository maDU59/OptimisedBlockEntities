package fr.madu59.obe.client.mixin.blockentity.renderers.decoratedpot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.renderer.blockentity.state.DecoratedPotRenderState;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;

@Mixin(DecoratedPotRenderer.class)
public class DecoratedPotRendererMixin {
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(CallbackInfo ci, @Local DecoratedPotRenderState state){
        if(!RenderModeManager.shouldRenderEntity(state)) ci.cancel();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void obe$cancelExtract(CallbackInfo ci, @Local DecoratedPotRenderState state, @Local DecoratedPotBlockEntity be){
        ((BlockEntityRenderStateExt)state).blockEntity(be);
        if(!RenderModeManager.shouldRenderEntity(be)) ci.cancel();
    }
}

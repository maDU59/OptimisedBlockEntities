package fr.madu59.obe.client.mixin.blockentity.renderers.shulkerbox;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

@Mixin(ShulkerBoxRenderer.class)
public class ShulkerBoxRendererMixin {
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(CallbackInfo ci, @Local ShulkerBoxRenderState state){
        if(!RenderModeManager.shouldRenderEntity(state)) ci.cancel();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void obe$cancelExtract(CallbackInfo ci, @Local ShulkerBoxRenderState state, @Local ShulkerBoxBlockEntity be){
        ((BlockEntityRenderStateExt)state).blockEntity(be);
        if(!RenderModeManager.shouldRenderEntity(be)) ci.cancel();
    }
}

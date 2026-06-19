package fr.madu59.obe.client.mixin.blockentity.renderers.bell;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.world.level.block.entity.BellBlockEntity;

@Mixin(BellRenderer.class)
public abstract class BellRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(CallbackInfo ci, @Local BellBlockEntity be){
        if(!RenderModeManager.shouldRenderEntity(be)) ci.cancel();
    }
}

package fr.madu59.obe.mixin.blockentity.renderers.skull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

@Mixin(SkullBlockRenderer.class)
public abstract class SkullBlockRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(CallbackInfo ci, @Local SkullBlockEntity be){
        if(!RenderModeManager.shouldRenderEntity(be)) ci.cancel();
    }
}

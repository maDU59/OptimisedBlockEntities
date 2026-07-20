package fr.madu59.obe.client.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.renderer.blockentity.SpecialBlockEntityRenderingManager;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

@Pseudo
@Mixin(value = SodiumWorldRenderer.class, remap = false)
public class SodiumWorldRendererMixin {

    @Inject(method = "renderBlockEntity", at = @At("HEAD"), cancellable = true)
    private static void obe$preventUselessExtraction(CallbackInfo ci, @Local BlockEntity be){
        if(SpecialBlockEntityRenderingManager.shouldSkipRendering(be)){
            ci.cancel();
        }
    }
}

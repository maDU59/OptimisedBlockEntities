package fr.madu59.obe.client.mixin.blockentity.renderers.bed;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.state.BedRenderState;
import net.minecraft.world.level.block.entity.BedBlockEntity;

@Mixin(BedRenderer.class)
public abstract class BedRendererMixin {
    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    public void obe$cancelSubmit(CallbackInfo ci, @Local BedRenderState state){
        if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_BEDS.getValue(), state)) ci.cancel();
        RenderModeManager.updateOnRender(state);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void obe$cancelExtract(CallbackInfo ci, @Local BedRenderState state, @Local BedBlockEntity be){
        ((BlockEntityRenderStateExt)state).blockEntity(be);
        if(!RenderModeManager.shouldRenderEntity(!SettingsManager.OPTIMISED_BEDS.getValue(), be)) ci.cancel();
    }
}
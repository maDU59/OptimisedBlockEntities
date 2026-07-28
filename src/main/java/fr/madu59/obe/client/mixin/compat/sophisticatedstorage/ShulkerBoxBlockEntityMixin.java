package fr.madu59.obe.client.mixin.compat.sophisticatedstorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedAppearanceExt;
import fr.madu59.obe.client.compat.sophisticatedstorage.shulker.SophisticatedShulkerRuntime;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlockEntity;

/** Keeps the original BER present for overlays while OBE owns the static shell. */
@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin implements SophisticatedAppearanceExt {
    @Unique private Object obe$appearanceFingerprint;

    @Override public Object obe$appearanceFingerprint() { return obe$appearanceFingerprint; }
    @Override public void obe$appearanceFingerprint(Object fingerprint) { obe$appearanceFingerprint = fingerprint; }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$registerSophisticatedShulker(BlockPos pos, BlockState state, CallbackInfo ci) {
        BlockEntityExt ext = (BlockEntityExt) this;
        ext.isSupported(true);
        ext.hasSpecialRenderer(true);
        ext.renderBoth(true);
    }

    @Inject(method = "updateAnimation", at = @At("TAIL"))
    private void obe$updateAnimationMode(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        ShulkerBoxBlockEntity shulker = (ShulkerBoxBlockEntity) (Object) this;
        SophisticatedShulkerRuntime.updateAnimationMode(shulker);
    }

}

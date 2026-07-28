package fr.madu59.obe.client.mixin.compat.sophisticatedstorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.compat.sophisticatedstorage.chest.SophisticatedChestRuntime;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedAppearanceExt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlockEntity;

/** Keeps the original BER present for overlays while OBE owns the static shell. */
@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin implements SophisticatedAppearanceExt {
    @Unique private Object obe$appearanceFingerprint;

    @Override public Object obe$appearanceFingerprint() { return obe$appearanceFingerprint; }
    @Override public void obe$appearanceFingerprint(Object fingerprint) { obe$appearanceFingerprint = fingerprint; }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$registerSophisticatedChest(BlockPos pos, BlockState state, CallbackInfo ci) {
        BlockEntityExt ext = (BlockEntityExt) this;
        ext.isSupported(true);
        ext.hasSpecialRenderer(true);
        ext.renderBoth(true);
    }

    @Inject(method = "lidAnimateTick", at = @At("TAIL"))
    private static void obe$updateClosedMode(ChestBlockEntity chest, CallbackInfo ci) {
        SophisticatedChestRuntime.updateAnimationMode(chest);
    }

    @Inject(method = "setChanged", at = @At("TAIL"))
    private void obe$invalidateStaticAppearance(CallbackInfo ci) {
        SophisticatedChestRuntime.appearanceMayHaveChanged((ChestBlockEntity) (Object) this);
    }
}

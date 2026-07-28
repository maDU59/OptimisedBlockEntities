package fr.madu59.obe.client.mixin.compat.sophisticatedstorage;

import fr.madu59.obe.client.compat.sophisticatedstorage.shulker.SophisticatedShulkerRuntime;
import fr.madu59.obe.client.compat.sophisticatedstorage.chest.SophisticatedChestRuntime;
import fr.madu59.obe.compat.SophisticatedStorageRendererCompatibility;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Watches exact shared mutation/sync paths, but acts only on in-scope chests and shulkers. */
@Mixin(StorageBlockEntity.class)
public abstract class StorageBlockEntityMixin {
    @Inject(method = "setChanged", at = @At("TAIL"))
    private void obe$invalidateShulkerAppearance(CallbackInfo ci) {
        if (SophisticatedStorageRendererCompatibility.shulkerSupported()
                && (Object) this instanceof ShulkerBoxBlockEntity shulker) {
            SophisticatedShulkerRuntime.appearanceMayHaveChanged(shulker);
        }
    }

    /** Client synchronization bypasses setChanged, so inspect the completed exact-jar packet load too. */
    @Inject(method = "onDataPacket", at = @At("TAIL"))
    private void obe$invalidateSynchronizedAppearance(Connection connection,
            ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries, CallbackInfo ci) {
        if (SophisticatedStorageRendererCompatibility.chestSupported()
                && (Object) this instanceof ChestBlockEntity chest) {
            SophisticatedChestRuntime.appearanceMayHaveChanged(chest);
        } else if (SophisticatedStorageRendererCompatibility.shulkerSupported()
                && (Object) this instanceof ShulkerBoxBlockEntity shulker) {
            SophisticatedShulkerRuntime.appearanceMayHaveChanged(shulker);
        }
    }
}

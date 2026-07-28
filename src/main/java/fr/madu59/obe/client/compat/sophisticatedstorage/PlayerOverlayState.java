package fr.madu59.obe.client.compat.sophisticatedstorage;

import org.jetbrains.annotations.Nullable;

import net.p3pp3rf1y.sophisticatedstorage.item.StorageToolItem;

/** Immutable held-item facts; deliberately contains no player reference. */
public record PlayerOverlayState(
        boolean showsUpgrades,
        boolean showsHiddenTiers,
        boolean showsLockPreview,
        boolean showsDisabledUpgrades,
        @Nullable StorageToolItem.Mode storageToolMode
) {
    public static final PlayerOverlayState NONE = new PlayerOverlayState(false, false, false, false, null);
}

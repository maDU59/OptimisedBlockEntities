package fr.madu59.obe.client.compat.sophisticatedstorage;

import java.util.List;

/** Minecraft-free dynamic-visual decisions mirroring the pinned renderer. */
public final class SophisticatedDynamicRenderDecisions {
    private SophisticatedDynamicRenderDecisions() {
    }

    public static DynamicRenderReason chestNeedsDynamicRenderer(
            boolean terrainReady,
            boolean mainChest,
            boolean packed,
            boolean tierVisible,
            boolean locked,
            boolean lockVisible,
            boolean displayItemPresent,
            boolean upgradesVisible,
            List<UpgradeVisualState> upgrades,
            PlayerOverlayState player) {
        if (!terrainReady) {
            return DynamicRenderReason.TERRAIN_NOT_READY;
        }
        if (player == null) {
            return DynamicRenderReason.UNKNOWN;
        }
        if (!tierVisible && player.showsHiddenTiers()) {
            return DynamicRenderReason.HIDDEN_TIER;
        }
        if (packed || !mainChest) {
            return DynamicRenderReason.SKIP;
        }
        if (displayItemPresent) {
            return DynamicRenderReason.DISPLAY_ITEM;
        }
        DynamicRenderReason upgradeReason = upgradeReason(upgradesVisible, upgrades, player);
        if (upgradeReason != DynamicRenderReason.SKIP) {
            return upgradeReason;
        }
        if (locked && (lockVisible || player.showsLockPreview())) {
            return DynamicRenderReason.LOCK;
        }
        return DynamicRenderReason.SKIP;
    }

    public static DynamicRenderReason shulkerNeedsDynamicRenderer(
            boolean terrainReady,
            boolean tierVisible,
            boolean locked,
            boolean lockVisible,
            boolean displayItemPresent,
            boolean upgradesVisible,
            List<UpgradeVisualState> upgrades,
            PlayerOverlayState player) {
        if (!terrainReady) {
            return DynamicRenderReason.TERRAIN_NOT_READY;
        }
        if (player == null) {
            return DynamicRenderReason.UNKNOWN;
        }
        if (!tierVisible && player.showsHiddenTiers()) {
            return DynamicRenderReason.HIDDEN_TIER;
        }
        if (displayItemPresent) {
            return DynamicRenderReason.DISPLAY_ITEM;
        }
        DynamicRenderReason upgradeReason = upgradeReason(upgradesVisible, upgrades, player);
        if (upgradeReason != DynamicRenderReason.SKIP) {
            return upgradeReason;
        }
        if (locked && (lockVisible || player.showsLockPreview())) {
            return DynamicRenderReason.LOCK;
        }
        return DynamicRenderReason.SKIP;
    }

    private static DynamicRenderReason upgradeReason(boolean upgradesVisible,
            List<UpgradeVisualState> upgrades, PlayerOverlayState player) {
        if (upgrades == null) {
            return DynamicRenderReason.UNKNOWN;
        }
        for (UpgradeVisualState upgrade : upgrades) {
            if (upgrade == null) {
                return DynamicRenderReason.UNKNOWN;
            }
        }
        if (!upgradesVisible && !player.showsUpgrades()) {
            return DynamicRenderReason.SKIP;
        }
        for (UpgradeVisualState upgrade : upgrades) {
            if (upgrade.stackPresent() || player.showsUpgrades()) {
                return DynamicRenderReason.UPGRADES;
            }
        }
        return DynamicRenderReason.SKIP;
    }
}

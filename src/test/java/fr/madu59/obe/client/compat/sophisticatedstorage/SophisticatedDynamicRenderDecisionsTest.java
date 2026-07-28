package fr.madu59.obe.client.compat.sophisticatedstorage;

import static fr.madu59.obe.client.compat.sophisticatedstorage.DynamicRenderReason.DISPLAY_ITEM;
import static fr.madu59.obe.client.compat.sophisticatedstorage.DynamicRenderReason.HIDDEN_TIER;
import static fr.madu59.obe.client.compat.sophisticatedstorage.DynamicRenderReason.LOCK;
import static fr.madu59.obe.client.compat.sophisticatedstorage.DynamicRenderReason.SKIP;
import static fr.madu59.obe.client.compat.sophisticatedstorage.DynamicRenderReason.TERRAIN_NOT_READY;
import static fr.madu59.obe.client.compat.sophisticatedstorage.DynamicRenderReason.UNKNOWN;
import static fr.madu59.obe.client.compat.sophisticatedstorage.DynamicRenderReason.UPGRADES;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class SophisticatedDynamicRenderDecisionsTest {
    private static final PlayerOverlayState NONE = PlayerOverlayState.NONE;
    private static final List<UpgradeVisualState> NO_SLOTS = List.of();
    private static final List<UpgradeVisualState> EMPTY_SLOT = List.of(UpgradeVisualState.EMPTY);
    private static final List<UpgradeVisualState> FILLED_SLOT = List.of(UpgradeVisualState.PRESENT);

    @Test void closedCommittedOrdinaryMainChestSkips() { assertChest(SKIP, true, true, false, true, false, false, false, false, NO_SLOTS, NONE); }
    @Test void terrainNotCommittedRenders() { assertChest(TERRAIN_NOT_READY, false, true, false, true, false, false, false, false, NO_SLOTS, NONE); }
    @Test void entityModeRenders() { assertChest(TERRAIN_NOT_READY, false, true, false, true, false, false, false, false, NO_SLOTS, NONE); }
    @Test void fallbackPendingRenders() { assertChest(TERRAIN_NOT_READY, false, true, false, true, false, false, false, false, NO_SLOTS, NONE); }
    @Test void openingChestRendersThroughReadiness() { assertChest(TERRAIN_NOT_READY, false, true, false, true, false, false, false, false, NO_SLOTS, NONE); }
    @Test void frontDisplayItemRenders() { assertChest(DISPLAY_ITEM, true, true, false, true, false, false, true, false, NO_SLOTS, NONE); }
    @Test void sideDisplayItemRenders() { assertChest(DISPLAY_ITEM, true, true, false, true, false, false, true, false, NO_SLOTS, NONE); }
    @Test void hiddenTierWithoutHeldPreviewSkips() { assertChest(SKIP, true, true, false, false, false, false, false, false, NO_SLOTS, NONE); }
    @Test void hiddenTierWithTierToolRenders() { assertChest(HIDDEN_TIER, true, true, false, false, false, false, false, false, NO_SLOTS, overlay(false, true, false, false)); }
    @Test void hiddenTierWithTierUpgradeRenders() { assertChest(HIDDEN_TIER, true, true, false, false, false, false, false, false, NO_SLOTS, overlay(false, true, false, false)); }
    @Test void visibleLockRenders() { assertChest(LOCK, true, true, false, true, true, true, false, false, NO_SLOTS, NONE); }
    @Test void hiddenLockWithoutToolSkips() { assertChest(SKIP, true, true, false, true, true, false, false, false, NO_SLOTS, NONE); }
    @Test void hiddenLockWithToolRenders() { assertChest(LOCK, true, true, false, true, true, false, false, false, NO_SLOTS, overlay(false, false, true, false)); }
    @Test void unlockedWithLockToolSkips() { assertChest(SKIP, true, true, false, true, false, false, false, false, NO_SLOTS, overlay(false, false, true, false)); }
    @Test void visibleNonEmptyUpgradeRenders() { assertChest(UPGRADES, true, true, false, true, false, false, false, true, FILLED_SLOT, NONE); }
    @Test void persistentUpgradeDisplayWithOnlyEmptyEntriesSkips() { assertChest(SKIP, true, true, false, true, false, false, false, true, EMPTY_SLOT, NONE); }
    @Test void storageToolWithAvailableEmptySlotRenders() { assertChest(UPGRADES, true, true, false, true, false, false, false, false, EMPTY_SLOT, overlay(true, false, false, false)); }
    @Test void disabledUpgradePreviewRenders() { assertChest(UPGRADES, true, true, false, true, false, false, false, false, EMPTY_SLOT, overlay(true, false, false, true)); }
    @Test void packedChestIgnoresDisplayItem() { assertChest(SKIP, true, true, true, true, false, false, true, false, NO_SLOTS, NONE); }
    @Test void packedChestIgnoresPadlock() { assertChest(SKIP, true, true, true, true, true, true, false, false, NO_SLOTS, NONE); }
    @Test void packedChestStillRendersHiddenTierPreview() { assertChest(HIDDEN_TIER, true, true, true, false, false, false, false, false, NO_SLOTS, overlay(false, true, false, false)); }
    @Test void nonMainHalfIgnoresMainDisplayItem() { assertChest(SKIP, true, false, false, true, false, false, true, false, NO_SLOTS, NONE); }
    @Test void nonMainHalfRendersHiddenTierPreview() { assertChest(HIDDEN_TIER, true, false, false, false, false, false, false, false, NO_SLOTS, overlay(false, true, false, false)); }
    @Test void unknownChestRenderInfoRenders() { assertChest(UNKNOWN, true, true, false, true, false, false, false, false, null, NONE); }

    @Test void closedCommittedOrdinaryShulkerSkips() { assertShulker(SKIP, true, true, false, false, false, false, NO_SLOTS, NONE); }
    @Test void uncommittedShulkerRenders() { assertShulker(TERRAIN_NOT_READY, false, true, false, false, false, false, NO_SLOTS, NONE); }
    @Test void openingShulkerRenders() { assertShulker(TERRAIN_NOT_READY, false, true, false, false, false, false, NO_SLOTS, NONE); }
    @Test void shulkerDisplayItemRenders() { assertShulker(DISPLAY_ITEM, true, true, false, false, true, false, NO_SLOTS, NONE); }
    @Test void hiddenShulkerTierWithoutHeldPreviewSkips() { assertShulker(SKIP, true, false, false, false, false, false, NO_SLOTS, NONE); }
    @Test void hiddenShulkerTierWithHeldPreviewRenders() { assertShulker(HIDDEN_TIER, true, false, false, false, false, false, NO_SLOTS, overlay(false, true, false, false)); }
    @Test void visibleShulkerLockRenders() { assertShulker(LOCK, true, true, true, true, false, false, NO_SLOTS, NONE); }
    @Test void hiddenShulkerLockWithoutToolSkips() { assertShulker(SKIP, true, true, true, false, false, false, NO_SLOTS, NONE); }
    @Test void hiddenShulkerLockWithToolRenders() { assertShulker(LOCK, true, true, true, false, false, false, NO_SLOTS, overlay(false, false, true, false)); }
    @Test void unlockedShulkerWithLockToolSkips() { assertShulker(SKIP, true, true, false, false, false, false, NO_SLOTS, overlay(false, false, true, false)); }
    @Test void shulkerFilledUpgradeRenders() { assertShulker(UPGRADES, true, true, false, false, false, true, FILLED_SLOT, NONE); }
    @Test void shulkerPersistentEmptyUpgradeSkips() { assertShulker(SKIP, true, true, false, false, false, true, EMPTY_SLOT, NONE); }
    @Test void shulkerHeldUpgradePreviewRendersEmptySlot() { assertShulker(UPGRADES, true, true, false, false, false, false, EMPTY_SLOT, overlay(true, false, false, false)); }
    @Test void unknownShulkerRenderInfoRenders() { assertShulker(UNKNOWN, true, true, false, false, false, false, null, NONE); }

    private static PlayerOverlayState overlay(boolean upgrades, boolean tiers, boolean locks, boolean disabled) {
        return new PlayerOverlayState(upgrades, tiers, locks, disabled, null);
    }

    private static void assertChest(DynamicRenderReason expected, boolean terrainReady, boolean mainChest,
            boolean packed, boolean tierVisible, boolean locked, boolean lockVisible,
            boolean displayItemPresent, boolean upgradesVisible, List<UpgradeVisualState> upgrades,
            PlayerOverlayState player) {
        assertEquals(expected, SophisticatedDynamicRenderDecisions.chestNeedsDynamicRenderer(
                terrainReady, mainChest, packed, tierVisible, locked, lockVisible,
                displayItemPresent, upgradesVisible, upgrades, player));
    }

    private static void assertShulker(DynamicRenderReason expected, boolean terrainReady,
            boolean tierVisible, boolean locked, boolean lockVisible, boolean displayItemPresent,
            boolean upgradesVisible, List<UpgradeVisualState> upgrades, PlayerOverlayState player) {
        assertEquals(expected, SophisticatedDynamicRenderDecisions.shulkerNeedsDynamicRenderer(
                terrainReady, tierVisible, locked, lockVisible, displayItemPresent,
                upgradesVisible, upgrades, player));
    }
}

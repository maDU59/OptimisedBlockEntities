package fr.madu59.obe.client.compat.sophisticatedstorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import net.minecraft.client.player.LocalPlayer;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageToolItem.Mode;

class SophisticatedPlayerRenderStateTest {
    private static final SophisticatedPlayerRenderState.HeldItemState EMPTY =
            new SophisticatedPlayerRenderState.HeldItemState(false, null, false, false);

    @Test
    void refreshesAtMostOncePerGameTick() {
        AtomicInteger classifications = new AtomicInteger();
        SophisticatedPlayerRenderState cache = new SophisticatedPlayerRenderState();

        cache.currentForTick(10L, true, () -> {
            classifications.incrementAndGet();
            return PlayerOverlayState.NONE;
        });
        cache.currentForTick(10L, true, () -> {
            classifications.incrementAndGet();
            return PlayerOverlayState.NONE;
        });
        cache.currentForTick(11L, true, () -> {
            classifications.incrementAndGet();
            return PlayerOverlayState.NONE;
        });

        assertEquals(2, classifications.get());
    }

    @Test
    void mainAndOffHandFactsAreBothIncluded() {
        PlayerOverlayState main = SophisticatedPlayerRenderState.fromHands(
                tool(Mode.LOCK_DISPLAY), EMPTY);
        PlayerOverlayState off = SophisticatedPlayerRenderState.fromHands(
                EMPTY, tool(Mode.TIER_DISPLAY));

        assertTrue(main.showsUpgrades());
        assertTrue(main.showsLockPreview());
        assertTrue(off.showsUpgrades());
        assertTrue(off.showsHiddenTiers());
    }

    @Test
    void everyStorageToolModeMatchesPinnedRenderer() {
        for (Mode mode : Mode.values()) {
            PlayerOverlayState state = SophisticatedPlayerRenderState.fromHands(tool(mode), EMPTY);

            assertTrue(state.showsUpgrades(), "any storage tool previews empty upgrade slots: " + mode);
            assertEquals(mode == Mode.TIER_DISPLAY, state.showsHiddenTiers(), mode.toString());
            assertEquals(mode == Mode.LOCK_DISPLAY || mode == Mode.LOCK, state.showsLockPreview(), mode.toString());
            assertEquals(mode == Mode.UPGRADES_DISPLAY, state.showsDisabledUpgrades(), mode.toString());
            assertEquals(mode, state.storageToolMode());
        }
    }

    @Test
    void taggedUpgradeShowsUpgradeSlots() {
        var upgrade = new SophisticatedPlayerRenderState.HeldItemState(false, null, true, false);

        PlayerOverlayState state = SophisticatedPlayerRenderState.fromHands(EMPTY, upgrade);

        assertTrue(state.showsUpgrades());
        assertFalse(state.showsHiddenTiers());
    }

    @Test
    void tierUpgradeShowsHiddenTierButNotUpgradeSlots() {
        var tierUpgrade = new SophisticatedPlayerRenderState.HeldItemState(false, null, false, true);

        PlayerOverlayState state = SophisticatedPlayerRenderState.fromHands(tierUpgrade, EMPTY);

        assertFalse(state.showsUpgrades());
        assertTrue(state.showsHiddenTiers());
    }

    @Test
    void mainHandStorageToolModeWinsOverOffHand() {
        PlayerOverlayState state = SophisticatedPlayerRenderState.fromHands(
                tool(Mode.LINK), tool(Mode.TIER_DISPLAY));

        assertEquals(Mode.LINK, state.storageToolMode());
        assertFalse(state.showsHiddenTiers());
    }

    @Test
    void nullLevelAndNullPlayerUseAllFalseSnapshot() {
        SophisticatedPlayerRenderState cache = new SophisticatedPlayerRenderState();

        assertEquals(PlayerOverlayState.NONE, cache.currentForTick(null, true, () -> {
            throw new AssertionError("snapshot must not run");
        }));
        assertEquals(PlayerOverlayState.NONE, cache.currentForTick(1L, false, () -> {
            throw new AssertionError("snapshot must not run");
        }));
    }

    @Test
    void snapshotContainsNoPlayerReference() {
        assertTrue(PlayerOverlayState.class.isRecord());
        assertTrue(Arrays.stream(PlayerOverlayState.class.getRecordComponents())
                .noneMatch(component -> LocalPlayer.class.isAssignableFrom(component.getType())));
        assertNull(PlayerOverlayState.NONE.storageToolMode());
    }

    private static SophisticatedPlayerRenderState.HeldItemState tool(Mode mode) {
        return new SophisticatedPlayerRenderState.HeldItemState(true, mode, false, false);
    }
}

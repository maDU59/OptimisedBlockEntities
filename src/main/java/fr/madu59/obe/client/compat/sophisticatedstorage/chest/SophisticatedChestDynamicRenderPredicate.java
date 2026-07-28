package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import java.util.List;

import fr.madu59.obe.client.api.render.DynamicBlockEntityRenderPredicate;
import fr.madu59.obe.client.compat.sophisticatedstorage.DynamicRenderReason;
import fr.madu59.obe.client.compat.sophisticatedstorage.PlayerOverlayState;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedDynamicRenderDecisions;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedDynamicRenderControl;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedDynamicRenderTracking;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedPlayerRenderState;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedRenderContext;
import fr.madu59.obe.client.compat.sophisticatedstorage.UpgradeVisualState;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlockEntity;

/** Exact 1.5.70.1941 chest dynamic-visual extraction. */
public final class SophisticatedChestDynamicRenderPredicate
        implements DynamicBlockEntityRenderPredicate<ChestBlockEntity> {
    public static final SophisticatedChestDynamicRenderPredicate INSTANCE =
            new SophisticatedChestDynamicRenderPredicate();

    private SophisticatedChestDynamicRenderPredicate() {
    }

    @Override
    public boolean shouldRenderDynamic(ChestBlockEntity chest) {
        try {
            if (!SophisticatedDynamicRenderControl.isWholeRendererSkipEnabled()) {
                return track(DynamicRenderReason.BENCHMARK_BYPASS);
            }
            boolean terrainReady = SophisticatedRenderContext.hasUsableCommittedTerrain(chest)
                    && SophisticatedChestRuntime.isFullyClosed(chest);
            if (!terrainReady) {
                return track(DynamicRenderReason.TERRAIN_NOT_READY);
            }

            PlayerOverlayState player = SophisticatedPlayerRenderState.current();
            boolean tierVisible = chest.shouldShowTier();
            if (!tierVisible && player.showsHiddenTiers()) {
                return track(SophisticatedDynamicRenderDecisions.chestNeedsDynamicRenderer(
                        true, chest.isMainChest(), chest.isPacked(), false,
                        chest.isLocked(), chest.shouldShowLock(), false,
                        false, List.of(), player));
            }

            var mainWrapper = chest.getMainStorageWrapper();
            if (mainWrapper == null || mainWrapper.getRenderInfo() == null) {
                return track(DynamicRenderReason.UNKNOWN);
            }
            var mainRenderInfo = mainWrapper.getRenderInfo();
            var itemDisplayInfo = mainRenderInfo.getItemDisplayRenderInfo();
            if (itemDisplayInfo == null || itemDisplayInfo.getDisplayItem() == null) {
                return track(DynamicRenderReason.UNKNOWN);
            }
            boolean displayItemPresent = itemDisplayInfo.getDisplayItem()
                    .map(displayItem -> displayItem != null && displayItem.getItem() != null
                            && !displayItem.getItem().isEmpty())
                    .orElse(false);

            boolean mainChest = chest.isMainChest();
            boolean packed = chest.isPacked();
            boolean upgradesVisible = false;
            List<UpgradeVisualState> upgrades = List.of();
            if (mainChest && !packed) {
                upgradesVisible = chest.shouldShowUpgrades();
                if (upgradesVisible || player.showsUpgrades()) {
                    var wrapper = chest.getStorageWrapper();
                    if (wrapper == null || wrapper.getRenderInfo() == null
                            || wrapper.getRenderInfo().getUpgradeItems() == null) {
                        return track(DynamicRenderReason.UNKNOWN);
                    }
                    upgrades = wrapper.getRenderInfo().getUpgradeItems().stream()
                            .map(stack -> {
                                if (stack == null) {
                                    throw new IllegalStateException("null pinned upgrade render entry");
                                }
                                return stack.isEmpty() ? UpgradeVisualState.EMPTY : UpgradeVisualState.PRESENT;
                            })
                            .toList();
                }
            }

            return track(SophisticatedDynamicRenderDecisions.chestNeedsDynamicRenderer(
                    true, mainChest, packed, tierVisible, chest.isLocked(), chest.shouldShowLock(),
                    displayItemPresent, upgradesVisible, upgrades, player));
        } catch (Exception ignored) {
            return track(DynamicRenderReason.UNKNOWN);
        }
    }

    private static boolean track(DynamicRenderReason reason) {
        return SophisticatedDynamicRenderTracking.chestDecision(reason);
    }
}

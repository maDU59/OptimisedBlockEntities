package fr.madu59.obe.client.compat.sophisticatedstorage.shulker;

import fr.madu59.obe.client.api.render.DynamicBlockEntityRenderPredicate;
import fr.madu59.obe.client.compat.sophisticatedstorage.DynamicRenderReason;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedDynamicRenderDecisions;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedDynamicRenderControl;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedDynamicRenderTracking;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedPlayerRenderState;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedRenderContext;
import fr.madu59.obe.client.compat.sophisticatedstorage.UpgradeVisualState;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlockEntity;

/** Exact 1.5.70.1941 shulker dynamic-visual extraction. */
public final class SophisticatedShulkerDynamicRenderPredicate
        implements DynamicBlockEntityRenderPredicate<ShulkerBoxBlockEntity> {
    public static final SophisticatedShulkerDynamicRenderPredicate INSTANCE =
            new SophisticatedShulkerDynamicRenderPredicate();

    private SophisticatedShulkerDynamicRenderPredicate() {
    }

    @Override
    public boolean shouldRenderDynamic(ShulkerBoxBlockEntity shulker) {
        try {
            if (!SophisticatedDynamicRenderControl.isWholeRendererSkipEnabled()) {
                return track(DynamicRenderReason.BENCHMARK_BYPASS);
            }
            boolean terrainReady = SophisticatedRenderContext.hasUsableCommittedTerrain(shulker)
                    && SophisticatedShulkerRuntime.isFullyClosed(shulker);
            if (!terrainReady) {
                return track(DynamicRenderReason.TERRAIN_NOT_READY);
            }

            var player = SophisticatedPlayerRenderState.current();
            boolean tierVisible = shulker.shouldShowTier();
            if (!tierVisible && player.showsHiddenTiers()) {
                return track(DynamicRenderReason.HIDDEN_TIER);
            }

            var wrapper = shulker.getStorageWrapper();
            if (wrapper == null || wrapper.getRenderInfo() == null) {
                return track(DynamicRenderReason.UNKNOWN);
            }
            var renderInfo = wrapper.getRenderInfo();
            var itemDisplayInfo = renderInfo.getItemDisplayRenderInfo();
            if (itemDisplayInfo == null || itemDisplayInfo.getDisplayItem() == null) {
                return track(DynamicRenderReason.UNKNOWN);
            }
            boolean displayItemPresent = itemDisplayInfo.getDisplayItem()
                    .map(displayItem -> displayItem != null && displayItem.getItem() != null
                            && !displayItem.getItem().isEmpty())
                    .orElse(false);
            boolean upgradesVisible = shulker.shouldShowUpgrades();
            var upgrades = java.util.List.<UpgradeVisualState>of();
            if (upgradesVisible || player.showsUpgrades()) {
                if (renderInfo.getUpgradeItems() == null) {
                    return track(DynamicRenderReason.UNKNOWN);
                }
                upgrades = renderInfo.getUpgradeItems().stream()
                        .map(stack -> {
                            if (stack == null) {
                                throw new IllegalStateException("null pinned upgrade render entry");
                            }
                            return stack.isEmpty() ? UpgradeVisualState.EMPTY : UpgradeVisualState.PRESENT;
                        })
                        .toList();
            }

            return track(SophisticatedDynamicRenderDecisions.shulkerNeedsDynamicRenderer(
                    true, tierVisible, shulker.isLocked(), shulker.shouldShowLock(), displayItemPresent,
                    upgradesVisible, upgrades, player));
        } catch (Exception ignored) {
            return track(DynamicRenderReason.UNKNOWN);
        }
    }

    private static boolean track(DynamicRenderReason reason) {
        return SophisticatedDynamicRenderTracking.shulkerDecision(reason);
    }
}

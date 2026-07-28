package fr.madu59.obe.client.compat.sophisticatedstorage;

import java.util.concurrent.atomic.AtomicLong;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.resources.SpecialBakedModelCache;

/** Low-noise counters for validating fallback and cache behavior in live runs. */
public final class SophisticatedStorageDiagnostics {
    private static final AtomicLong RESOLVED = new AtomicLong();
    private static final AtomicLong BAKED = new AtomicLong();
    private static final AtomicLong FALLBACKS = new AtomicLong();
    private static final AtomicLong MISSING_SPRITES = new AtomicLong();
    private static final AtomicLong SUPPRESSED_SHELL_CALLS = new AtomicLong();
    private static final AtomicLong PASSTHROUGH_SHELL_CALLS = new AtomicLong();
    private static final AtomicLong COMPLETED_DYNAMIC_RENDERERS = new AtomicLong();
    private static final AtomicLong WHOLE_CHEST_RENDERERS_SKIPPED = new AtomicLong();
    private static final AtomicLong WHOLE_CHEST_RENDERERS_EXECUTED = new AtomicLong();
    private static final AtomicLong WHOLE_SHULKER_RENDERERS_SKIPPED = new AtomicLong();
    private static final AtomicLong WHOLE_SHULKER_RENDERERS_EXECUTED = new AtomicLong();
    private static final AtomicLong TERRAIN_NOT_READY_EXECUTIONS = new AtomicLong();
    private static final AtomicLong DISPLAY_ITEM_EXECUTIONS = new AtomicLong();
    private static final AtomicLong UPGRADE_EXECUTIONS = new AtomicLong();
    private static final AtomicLong PADLOCK_EXECUTIONS = new AtomicLong();
    private static final AtomicLong HIDDEN_TIER_EXECUTIONS = new AtomicLong();
    private static final AtomicLong PREDICATE_FAILURES = new AtomicLong();
    private static final AtomicLong DISCOVERED_ATLAS_SPRITES = new AtomicLong();
    private static final AtomicLong MISSING_ATLAS_RESOURCES = new AtomicLong();

    private SophisticatedStorageDiagnostics() {
    }

    public static void resolved() { RESOLVED.incrementAndGet(); }
    public static void baked() { BAKED.incrementAndGet(); }
    public static void fallback() { FALLBACKS.incrementAndGet(); }
    public static void missingSprite() { MISSING_SPRITES.incrementAndGet(); }
    public static void suppressedShellCall() { SUPPRESSED_SHELL_CALLS.incrementAndGet(); }
    public static void passthroughShellCall() { PASSTHROUGH_SHELL_CALLS.incrementAndGet(); }
    public static void completedDynamicRenderer() { COMPLETED_DYNAMIC_RENDERERS.incrementAndGet(); }
    public static void wholeChestRendererSkipped() { WHOLE_CHEST_RENDERERS_SKIPPED.incrementAndGet(); }
    public static void wholeShulkerRendererSkipped() { WHOLE_SHULKER_RENDERERS_SKIPPED.incrementAndGet(); }
    public static void wholeChestRendererExecuted(DynamicRenderReason reason) {
        WHOLE_CHEST_RENDERERS_EXECUTED.incrementAndGet();
        recordExecutionReason(reason);
    }
    public static void wholeShulkerRendererExecuted(DynamicRenderReason reason) {
        WHOLE_SHULKER_RENDERERS_EXECUTED.incrementAndGet();
        recordExecutionReason(reason);
    }
    public static void discoveredAtlasSprite() { DISCOVERED_ATLAS_SPRITES.incrementAndGet(); }
    public static void missingAtlasResource() { MISSING_ATLAS_RESOURCES.incrementAndGet(); }

    public static Snapshot snapshot() {
        return new Snapshot(
                RESOLVED.get(), BAKED.get(), FALLBACKS.get(), MISSING_SPRITES.get(),
                SUPPRESSED_SHELL_CALLS.get(), PASSTHROUGH_SHELL_CALLS.get(), COMPLETED_DYNAMIC_RENDERERS.get(),
                WHOLE_CHEST_RENDERERS_SKIPPED.get(), WHOLE_CHEST_RENDERERS_EXECUTED.get(),
                WHOLE_SHULKER_RENDERERS_SKIPPED.get(), WHOLE_SHULKER_RENDERERS_EXECUTED.get(),
                TERRAIN_NOT_READY_EXECUTIONS.get(), DISPLAY_ITEM_EXECUTIONS.get(), UPGRADE_EXECUTIONS.get(),
                PADLOCK_EXECUTIONS.get(), HIDDEN_TIER_EXECUTIONS.get(), PREDICATE_FAILURES.get(),
                DISCOVERED_ATLAS_SPRITES.get(),
                MISSING_ATLAS_RESOURCES.get(), SpecialBakedModelCache.stats());
    }

    public static void logSnapshot(String reason) {
        Snapshot snapshot = snapshot();
        OBE.LOGGER.info(
                "Sophisticated Storage diagnostics ({}): resolved={}, baked={}, fallbacks={}, "
                        + "missingSprites={}, suppressedShellCalls={}, passthroughShellCalls={}, "
                        + "completedDynamicRenderers={}, chestSkipped={}, chestExecuted={}, "
                        + "shulkerSkipped={}, shulkerExecuted={}, terrainNotReady={}, displayItem={}, "
                        + "upgrades={}, padlock={}, hiddenTier={}, predicateFailures={}, "
                        + "atlasDiscovered={}, atlasMissing={}, cache={}",
                reason, snapshot.resolved(), snapshot.baked(), snapshot.fallbacks(), snapshot.missingSprites(),
                snapshot.suppressedShellCalls(), snapshot.passthroughShellCalls(), snapshot.completedDynamicRenderers(),
                snapshot.wholeChestRenderersSkipped(), snapshot.wholeChestRenderersExecuted(),
                snapshot.wholeShulkerRenderersSkipped(), snapshot.wholeShulkerRenderersExecuted(),
                snapshot.terrainNotReadyExecutions(), snapshot.displayItemExecutions(), snapshot.upgradeExecutions(),
                snapshot.padlockExecutions(), snapshot.hiddenTierExecutions(), snapshot.predicateFailures(),
                snapshot.atlasDiscovered(), snapshot.atlasMissing(), snapshot.cache());
    }

    private static void recordExecutionReason(DynamicRenderReason reason) {
        switch (reason == null ? DynamicRenderReason.UNKNOWN : reason) {
            case TERRAIN_NOT_READY -> TERRAIN_NOT_READY_EXECUTIONS.incrementAndGet();
            case DISPLAY_ITEM -> DISPLAY_ITEM_EXECUTIONS.incrementAndGet();
            case UPGRADES -> UPGRADE_EXECUTIONS.incrementAndGet();
            case LOCK -> PADLOCK_EXECUTIONS.incrementAndGet();
            case HIDDEN_TIER -> HIDDEN_TIER_EXECUTIONS.incrementAndGet();
            case UNKNOWN, SKIP -> PREDICATE_FAILURES.incrementAndGet();
            case BENCHMARK_BYPASS -> { }
        }
    }

    public record Snapshot(
            long resolved,
            long baked,
            long fallbacks,
            long missingSprites,
            long suppressedShellCalls,
            long passthroughShellCalls,
            long completedDynamicRenderers,
            long wholeChestRenderersSkipped,
            long wholeChestRenderersExecuted,
            long wholeShulkerRenderersSkipped,
            long wholeShulkerRenderersExecuted,
            long terrainNotReadyExecutions,
            long displayItemExecutions,
            long upgradeExecutions,
            long padlockExecutions,
            long hiddenTierExecutions,
            long predicateFailures,
            long atlasDiscovered,
            long atlasMissing,
            fr.madu59.obe.client.resources.BoundedModelCache.Stats cache
    ) {
        public long potentialChestRenderers() {
            return wholeChestRenderersSkipped + wholeChestRenderersExecuted;
        }

        public long potentialShulkerRenderers() {
            return wholeShulkerRenderersSkipped + wholeShulkerRenderersExecuted;
        }
    }
}

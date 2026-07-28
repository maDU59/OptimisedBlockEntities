package fr.madu59.obe.client.compat.sophisticatedstorage;

/** One primary reason for a Sophisticated original-renderer decision. */
public enum DynamicRenderReason {
    SKIP,
    TERRAIN_NOT_READY,
    DISPLAY_ITEM,
    UPGRADES,
    LOCK,
    HIDDEN_TIER,
    UNKNOWN,
    /** Opt-in live harness only: preserves the previous shell-only rung. */
    BENCHMARK_BYPASS
}

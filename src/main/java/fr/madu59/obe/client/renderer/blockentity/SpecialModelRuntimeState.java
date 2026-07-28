package fr.madu59.obe.client.renderer.blockentity;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

/** Per-block-entity handoff state; values are immutable appearance keys only. */
public final class SpecialModelRuntimeState {
    private @Nullable Object preparedAppearance;
    private long preparedGeneration = -1;
    private @Nullable Object committedAppearance;
    private long committedGeneration = -1;
    private @Nullable Object failedAppearance;
    private long failedGeneration = -1;
    private @Nullable String failureReason;
    private boolean fallbackPending;

    public synchronized void prepareTerrain(Object appearance, long generation) {
        preparedAppearance = Objects.requireNonNull(appearance, "appearance");
        preparedGeneration = generation;
        if (Objects.equals(failedAppearance, appearance) && failedGeneration == generation) {
            failedAppearance = null;
            failedGeneration = -1;
            failureReason = null;
            fallbackPending = false;
        }
    }

    public synchronized void commitPreparedTerrain() {
        if (preparedAppearance == null) {
            return;
        }
        committedAppearance = preparedAppearance;
        committedGeneration = preparedGeneration;
        preparedAppearance = null;
        preparedGeneration = -1;
        fallbackPending = false;
    }

    public synchronized void fail(Object appearance, long generation, String reason) {
        failedAppearance = appearance;
        failedGeneration = generation;
        failureReason = reason;
        preparedAppearance = null;
        preparedGeneration = -1;
        fallbackPending = true;
    }

    public synchronized boolean canAttempt(Object appearance, long generation) {
        return !Objects.equals(failedAppearance, appearance) || failedGeneration != generation;
    }

    public synchronized void resetFailure() {
        failedAppearance = null;
        failedGeneration = -1;
        failureReason = null;
        fallbackPending = false;
    }

    public synchronized void commitEntity() {
        preparedAppearance = null;
        preparedGeneration = -1;
        committedAppearance = null;
        committedGeneration = -1;
        fallbackPending = false;
    }

    /** Installs the full BER after a failed terrain compile without releasing its retry hold. */
    public synchronized void commitFallbackEntity() {
        preparedAppearance = null;
        preparedGeneration = -1;
        committedAppearance = null;
        committedGeneration = -1;
        fallbackPending = true;
    }

    public synchronized void clearForReload() {
        preparedAppearance = null;
        preparedGeneration = -1;
        committedAppearance = null;
        committedGeneration = -1;
        failedAppearance = null;
        failedGeneration = -1;
        failureReason = null;
        fallbackPending = false;
    }

    public synchronized boolean hasPreparedTerrain() { return preparedAppearance != null; }
    public synchronized boolean hasCommittedTerrain() { return committedAppearance != null; }
    public synchronized @Nullable Object committedAppearance() { return committedAppearance; }
    public synchronized long committedGeneration() { return committedGeneration; }
    public synchronized boolean fallbackPending() { return fallbackPending; }
    public synchronized long failedGeneration() { return failedGeneration; }
    public synchronized @Nullable String failureReason() { return failureReason; }
}

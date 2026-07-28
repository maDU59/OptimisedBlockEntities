package fr.madu59.obe.client.compat.sophisticatedstorage;

/** Render-thread handoff containing only an enum, never a world object. */
public final class SophisticatedDynamicRenderTracking {
    private static final ThreadLocal<DynamicRenderReason> CURRENT_REASON = new ThreadLocal<>();

    private SophisticatedDynamicRenderTracking() {
    }

    public static boolean chestDecision(DynamicRenderReason reason) {
        if (reason == DynamicRenderReason.SKIP) {
            SophisticatedStorageDiagnostics.wholeChestRendererSkipped();
            return false;
        }
        CURRENT_REASON.set(reason == null ? DynamicRenderReason.UNKNOWN : reason);
        return true;
    }

    public static boolean shulkerDecision(DynamicRenderReason reason) {
        if (reason == DynamicRenderReason.SKIP) {
            SophisticatedStorageDiagnostics.wholeShulkerRendererSkipped();
            return false;
        }
        CURRENT_REASON.set(reason == null ? DynamicRenderReason.UNKNOWN : reason);
        return true;
    }

    public static void chestRendererExecuting() {
        SophisticatedStorageDiagnostics.wholeChestRendererExecuted(consumeReason());
    }

    public static void shulkerRendererExecuting() {
        SophisticatedStorageDiagnostics.wholeShulkerRendererExecuted(consumeReason());
    }

    private static DynamicRenderReason consumeReason() {
        DynamicRenderReason reason = CURRENT_REASON.get();
        CURRENT_REASON.remove();
        return reason == null ? DynamicRenderReason.UNKNOWN : reason;
    }
}

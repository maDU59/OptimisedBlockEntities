package fr.madu59.obe.client.compat.sophisticatedstorage;

/** Package-scoped live-validation switch; production defaults permanently on. */
public final class SophisticatedDynamicRenderControl {
    private static volatile boolean wholeRendererSkipEnabled = true;

    private SophisticatedDynamicRenderControl() {
    }

    public static boolean isWholeRendererSkipEnabled() {
        return wholeRendererSkipEnabled;
    }

    static void setWholeRendererSkipEnabledForValidation(boolean enabled) {
        if (!Boolean.getBoolean("obe.liveValidation") && !isRunningUnitTests()) {
            return;
        }
        wholeRendererSkipEnabled = enabled;
    }

    private static boolean isRunningUnitTests() {
        return SophisticatedDynamicRenderControl.class.desiredAssertionStatus();
    }
}

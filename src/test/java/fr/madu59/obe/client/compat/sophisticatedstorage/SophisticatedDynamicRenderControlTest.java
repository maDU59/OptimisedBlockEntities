package fr.madu59.obe.client.compat.sophisticatedstorage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SophisticatedDynamicRenderControlTest {
    @Test
    void liveBenchmarkOverrideCanTemporarilyRestoreShellOnlyBehavior() {
        SophisticatedDynamicRenderControl.setWholeRendererSkipEnabledForValidation(true);
        assertTrue(SophisticatedDynamicRenderControl.isWholeRendererSkipEnabled());

        SophisticatedDynamicRenderControl.setWholeRendererSkipEnabledForValidation(false);
        assertFalse(SophisticatedDynamicRenderControl.isWholeRendererSkipEnabled());

        SophisticatedDynamicRenderControl.setWholeRendererSkipEnabledForValidation(true);
    }
}

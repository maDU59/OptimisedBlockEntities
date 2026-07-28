package fr.madu59.obe.client.compat.sophisticatedstorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SophisticatedStorageDiagnosticsDynamicTest {
    @Test
    void wholeRendererCountersAndPrimaryReasonsAreIndependent() {
        var before = SophisticatedStorageDiagnostics.snapshot();

        SophisticatedStorageDiagnostics.wholeChestRendererSkipped();
        SophisticatedStorageDiagnostics.wholeShulkerRendererSkipped();
        SophisticatedStorageDiagnostics.wholeChestRendererExecuted(DynamicRenderReason.DISPLAY_ITEM);
        SophisticatedStorageDiagnostics.wholeChestRendererExecuted(DynamicRenderReason.UPGRADES);
        SophisticatedStorageDiagnostics.wholeShulkerRendererExecuted(DynamicRenderReason.LOCK);
        SophisticatedStorageDiagnostics.wholeShulkerRendererExecuted(DynamicRenderReason.HIDDEN_TIER);
        SophisticatedStorageDiagnostics.wholeChestRendererExecuted(DynamicRenderReason.TERRAIN_NOT_READY);
        SophisticatedStorageDiagnostics.wholeShulkerRendererExecuted(DynamicRenderReason.UNKNOWN);

        var after = SophisticatedStorageDiagnostics.snapshot();
        assertEquals(before.wholeChestRenderersSkipped() + 1, after.wholeChestRenderersSkipped());
        assertEquals(before.wholeShulkerRenderersSkipped() + 1, after.wholeShulkerRenderersSkipped());
        assertEquals(before.wholeChestRenderersExecuted() + 3, after.wholeChestRenderersExecuted());
        assertEquals(before.wholeShulkerRenderersExecuted() + 3, after.wholeShulkerRenderersExecuted());
        assertEquals(before.displayItemExecutions() + 1, after.displayItemExecutions());
        assertEquals(before.upgradeExecutions() + 1, after.upgradeExecutions());
        assertEquals(before.padlockExecutions() + 1, after.padlockExecutions());
        assertEquals(before.hiddenTierExecutions() + 1, after.hiddenTierExecutions());
        assertEquals(before.terrainNotReadyExecutions() + 1, after.terrainNotReadyExecutions());
        assertEquals(before.predicateFailures() + 1, after.predicateFailures());
        assertEquals(after.wholeChestRenderersSkipped() + after.wholeChestRenderersExecuted(),
                after.potentialChestRenderers());
        assertEquals(after.wholeShulkerRenderersSkipped() + after.wholeShulkerRenderersExecuted(),
                after.potentialShulkerRenderers());
    }
}

package fr.madu59.obe.client.renderer.blockentity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SpecialModelRuntimeStateTest {
    @Test
    void commitsTheExactPreparedAppearance() {
        SpecialModelRuntimeState state = new SpecialModelRuntimeState();

        state.prepareTerrain("appearance", 4);
        assertTrue(state.hasPreparedTerrain());
        assertFalse(state.hasCommittedTerrain());

        state.commitPreparedTerrain();
        assertTrue(state.hasCommittedTerrain());
        assertEquals("appearance", state.committedAppearance());
        assertEquals(4, state.committedGeneration());
    }

    @Test
    void failureKeepsAnInstalledTerrainShellUntilEntityCommit() {
        SpecialModelRuntimeState state = new SpecialModelRuntimeState();
        state.prepareTerrain("old", 1);
        state.commitPreparedTerrain();

        state.fail("new", 1, "missing material");
        assertTrue(state.fallbackPending());
        assertTrue(state.hasCommittedTerrain());
        assertEquals("old", state.committedAppearance());

        state.commitEntity();
        assertFalse(state.fallbackPending());
        assertFalse(state.hasCommittedTerrain());
        assertNull(state.committedAppearance());
    }

    @Test
    void fallbackEntityCommitRemovesShellButRetainsFailureHold() {
        SpecialModelRuntimeState state = new SpecialModelRuntimeState();
        state.prepareTerrain("old", 1);
        state.commitPreparedTerrain();
        state.fail("new", 1, "missing material");

        state.commitFallbackEntity();

        assertTrue(state.fallbackPending());
        assertFalse(state.hasCommittedTerrain());
        assertFalse(state.canAttempt("new", 1));
    }

    @Test
    void aNewAppearanceOrGenerationCanRetry() {
        SpecialModelRuntimeState state = new SpecialModelRuntimeState();
        state.fail("bad", 2, "failure");

        assertFalse(state.canAttempt("bad", 2));
        assertTrue(state.canAttempt("different", 2));
        assertTrue(state.canAttempt("bad", 3));

        state.clearForReload();
        assertTrue(state.canAttempt("bad", 2));
    }

    @Test
    void appearanceChangeExplicitlyReleasesFailureHold() {
        SpecialModelRuntimeState state = new SpecialModelRuntimeState();
        state.fail("bad", 2, "failure");

        state.resetFailure();

        assertFalse(state.fallbackPending());
        assertTrue(state.canAttempt("bad", 2));
        assertNull(state.failureReason());
    }
}

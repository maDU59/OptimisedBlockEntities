package fr.madu59.obe.client.renderer.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import fr.madu59.obe.client.renderer.blockentity.SpecialModelRuntimeState;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;

class RenderModeManagerTest {
    @Test
    void terrainToTerrainRemeshCommitsTheNewAppearance() {
        BlockEntityExt ext = mock(BlockEntityExt.class);
        SpecialModelRuntimeState state = new SpecialModelRuntimeState();
        state.prepareTerrain("old", 1);
        state.commitPreparedTerrain();
        state.prepareTerrain("new", 1);
        when(ext.renderModeDelayed()).thenReturn(RenderMode.TERRAIN);
        when(ext.specialModelState()).thenReturn(state);

        RenderModeManager.commitCompiledState(ext, RenderMode.TERRAIN);

        verify(ext).commitRenderMode(RenderMode.TERRAIN);
        assertEquals("new", state.committedAppearance());
    }

    @Test
    void failedTerrainCompilationCommitsFallbackWhenThatSectionInstalls() {
        BlockEntityExt ext = mock(BlockEntityExt.class);
        SpecialModelRuntimeState state = new SpecialModelRuntimeState();
        state.prepareTerrain("old", 1);
        state.commitPreparedTerrain();
        state.fail("new", 1, "missing sprite");
        when(ext.renderModeDelayed()).thenReturn(RenderMode.ENTITY);
        when(ext.specialModelState()).thenReturn(state);

        RenderModeManager.commitCompiledState(ext, RenderMode.TERRAIN);

        verify(ext).commitRenderMode(RenderMode.ENTITY);
        assertEquals(true, state.fallbackPending());
        assertEquals(false, state.hasCommittedTerrain());
    }
}

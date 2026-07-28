package fr.madu59.obe.client.compat.sophisticatedstorage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.junit.jupiter.api.Test;

import fr.madu59.obe.client.renderer.blockentity.SpecialModelRuntimeState;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.SpecialBakedModelCache;
import net.minecraft.world.level.block.entity.BlockEntity;

class SophisticatedRenderContextTest {
    @Test
    void suppressionUsesCommittedActiveModeNotDelayedRequest() {
        BlockEntityExt ext = mock(BlockEntityExt.class);
        SpecialModelRuntimeState state = new SpecialModelRuntimeState();
        state.prepareTerrain("appearance", SpecialBakedModelCache.generation());
        state.commitPreparedTerrain();
        when(ext.isSupported()).thenReturn(true);
        when(ext.isEnabled()).thenReturn(true);
        when(ext.forceEntity()).thenReturn(false);
        when(ext.specialModelState()).thenReturn(state);
        when(ext.renderMode()).thenReturn(RenderMode.TERRAIN);
        when(ext.renderModeDelayed()).thenReturn(RenderMode.ENTITY);

        assertTrue(SophisticatedRenderContext.shouldSuppress(ext));

        when(ext.renderMode()).thenReturn(RenderMode.ENTITY);
        when(ext.renderModeDelayed()).thenReturn(RenderMode.TERRAIN);
        assertFalse(SophisticatedRenderContext.shouldSuppress(ext));
    }

    @Test
    void missingCommittedShellNeverSuppressesOriginalRenderer() {
        BlockEntityExt ext = mock(BlockEntityExt.class);
        when(ext.isSupported()).thenReturn(true);
        when(ext.isEnabled()).thenReturn(true);
        when(ext.renderMode()).thenReturn(RenderMode.TERRAIN);
        when(ext.specialModelState()).thenReturn(new SpecialModelRuntimeState());

        assertFalse(SophisticatedRenderContext.shouldSuppress(ext));
    }

    @Test
    void usableTerrainRequiresEveryCommittedSafetyCondition() {
        long generation = SpecialBakedModelCache.generation();
        BlockEntity blockEntity = mock(BlockEntity.class, withSettings().extraInterfaces(BlockEntityExt.class));
        BlockEntityExt ext = (BlockEntityExt) blockEntity;
        SpecialModelRuntimeState state = committedState(generation);
        when(ext.isSupported()).thenReturn(true);
        when(ext.isEnabled()).thenReturn(true);
        when(ext.forceEntity()).thenReturn(false);
        when(ext.renderMode()).thenReturn(RenderMode.TERRAIN);
        when(ext.specialModelState()).thenReturn(state);

        assertTrue(SophisticatedRenderContext.hasUsableCommittedTerrain(blockEntity));

        when(ext.renderMode()).thenReturn(RenderMode.ENTITY);
        assertFalse(SophisticatedRenderContext.hasUsableCommittedTerrain(blockEntity));
        when(ext.renderMode()).thenReturn(RenderMode.TERRAIN);

        when(ext.forceEntity()).thenReturn(true);
        assertFalse(SophisticatedRenderContext.hasUsableCommittedTerrain(blockEntity));
        when(ext.forceEntity()).thenReturn(false);

        when(ext.isEnabled()).thenReturn(false);
        assertFalse(SophisticatedRenderContext.hasUsableCommittedTerrain(blockEntity));
        when(ext.isEnabled()).thenReturn(true);

        when(ext.isSupported()).thenReturn(false);
        assertFalse(SophisticatedRenderContext.hasUsableCommittedTerrain(blockEntity));
    }

    @Test
    void fallbackOrCurrentGenerationFailureKeepsRendererActive() {
        long generation = SpecialBakedModelCache.generation();
        BlockEntity blockEntity = mock(BlockEntity.class, withSettings().extraInterfaces(BlockEntityExt.class));
        BlockEntityExt ext = (BlockEntityExt) blockEntity;
        SpecialModelRuntimeState state = committedState(generation);
        when(ext.isSupported()).thenReturn(true);
        when(ext.isEnabled()).thenReturn(true);
        when(ext.forceEntity()).thenReturn(false);
        when(ext.renderMode()).thenReturn(RenderMode.TERRAIN);
        when(ext.specialModelState()).thenReturn(state);

        state.fail("appearance", generation, "test fallback");

        assertTrue(state.fallbackPending());
        assertFalse(SophisticatedRenderContext.hasUsableCommittedTerrain(blockEntity));
    }

    @Test
    void staleGenerationAndMissingExtensionKeepRendererActive() {
        long generation = SpecialBakedModelCache.generation();
        BlockEntity blockEntity = mock(BlockEntity.class, withSettings().extraInterfaces(BlockEntityExt.class));
        BlockEntityExt ext = (BlockEntityExt) blockEntity;
        when(ext.isSupported()).thenReturn(true);
        when(ext.isEnabled()).thenReturn(true);
        when(ext.forceEntity()).thenReturn(false);
        when(ext.renderMode()).thenReturn(RenderMode.TERRAIN);
        when(ext.specialModelState()).thenReturn(committedState(generation - 1));

        assertFalse(SophisticatedRenderContext.hasUsableCommittedTerrain(blockEntity));
        assertFalse(SophisticatedRenderContext.hasUsableCommittedTerrain(mock(BlockEntity.class)));
    }

    private static SpecialModelRuntimeState committedState(long generation) {
        SpecialModelRuntimeState state = new SpecialModelRuntimeState();
        state.prepareTerrain("appearance", generation);
        state.commitPreparedTerrain();
        return state;
    }
}

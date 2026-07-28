package fr.madu59.obe.client.compat.sophisticatedstorage;

import org.jetbrains.annotations.Nullable;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.SpecialBakedModelCache;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Scoped bridge used by narrow shell-only renderer injections. */
public final class SophisticatedRenderContext {
    private static final ThreadLocal<BlockEntity> CURRENT = new ThreadLocal<>();

    private SophisticatedRenderContext() {
    }

    public static void enter(BlockEntity blockEntity) {
        CURRENT.set(blockEntity);
    }

    public static void exit() {
        CURRENT.remove();
    }

    public static boolean suppressStaticShell() {
        BlockEntity blockEntity = CURRENT.get();
        if (blockEntity == null) {
            return false;
        }
        boolean suppress = hasUsableCommittedTerrain(blockEntity);
        if (suppress) {
            SophisticatedStorageDiagnostics.suppressedShellCall();
        } else {
            SophisticatedStorageDiagnostics.passthroughShellCall();
        }
        return suppress;
    }

    /** Side-effect-free precondition for skipping any part of the original BER. */
    public static boolean hasUsableCommittedTerrain(BlockEntity blockEntity) {
        return blockEntity instanceof BlockEntityExt ext && hasUsableCommittedTerrain(ext);
    }

    static boolean shouldSuppress(@Nullable BlockEntityExt ext) {
        return hasUsableCommittedTerrain(ext);
    }

    static boolean hasUsableCommittedTerrain(@Nullable BlockEntityExt ext) {
        if (ext == null || !ext.isSupported() || !ext.isEnabled() || ext.forceEntity()
                || ext.renderMode() != RenderMode.TERRAIN) {
            return false;
        }
        long generation = SpecialBakedModelCache.generation();
        var state = ext.specialModelState();
        return state != null
                && state.hasCommittedTerrain()
                && state.committedGeneration() == generation
                && !state.fallbackPending()
                && state.failedGeneration() != generation;
    }
}

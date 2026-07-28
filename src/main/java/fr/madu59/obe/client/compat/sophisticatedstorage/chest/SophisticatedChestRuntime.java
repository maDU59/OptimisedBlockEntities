package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import java.util.function.Consumer;

import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.resources.SpecialBakedModelCache;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedAppearanceExt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;

/** Coordinates mode changes and remeshes for both loaded halves of a chest. */
public final class SophisticatedChestRuntime {
    private static final float CLOSED_EPSILON = 1.0E-4F;

    private SophisticatedChestRuntime() {
    }

    public static void updateAnimationMode(ChestBlockEntity chest) {
        BlockEntityExt ext = (BlockEntityExt) chest;
        boolean heldByFailure = ext.specialModelState().fallbackPending()
                && ext.specialModelState().failedGeneration() == SpecialBakedModelCache.generation();
        RenderMode requested = isFullyClosed(chest) && !heldByFailure ? RenderMode.TERRAIN : RenderMode.ENTITY;
        forEachLoadedHalf(chest, half -> RenderModeManager.setRenderModeDelayed(half, requested, half.getBlockPos()));
    }

    public static void invalidateAppearance(ChestBlockEntity chest) {
        forEachLoadedHalf(chest, half -> RenderModeManager.setDirty(half.getBlockPos()));
    }

    public static void appearanceMayHaveChanged(ChestBlockEntity chest) {
        Level level = chest.getLevel();
        if (level == null || !level.isClientSide()) {
            return;
        }
        forEachLoadedHalf(chest, half -> {
            ChestAppearanceFingerprint current = ChestAppearanceFingerprint.capture(half);
            SophisticatedAppearanceExt tracked = (SophisticatedAppearanceExt) half;
            if (!current.equals(tracked.obe$appearanceFingerprint())) {
                tracked.obe$appearanceFingerprint(current);
                ((BlockEntityExt) half).specialModelState().resetFailure();
                updateAnimationMode(half);
                RenderModeManager.setDirty(half.getBlockPos());
            }
        });
    }

    static boolean isFullyClosed(ChestBlockEntity chest) {
        return chest.getOpenNess(0.0F) <= CLOSED_EPSILON && chest.getOpenNess(1.0F) <= CLOSED_EPSILON;
    }

    private static void forEachLoadedHalf(ChestBlockEntity chest, Consumer<ChestBlockEntity> action) {
        action.accept(chest);
        Level level = chest.getLevel();
        if (level == null) {
            return;
        }
        BlockState state = chest.getBlockState();
        if (!state.hasProperty(ChestBlock.TYPE) || state.getValue(ChestBlock.TYPE) == ChestType.SINGLE) {
            return;
        }
        BlockPos otherPos = chest.getBlockPos().relative(ChestBlock.getConnectedDirection(state));
        if (!level.hasChunkAt(otherPos)) {
            return;
        }
        level.getBlockEntity(otherPos, ModBlocks.CHEST_BLOCK_ENTITY_TYPE.get())
                .filter(other -> other != chest)
                .ifPresent(action);
    }
}

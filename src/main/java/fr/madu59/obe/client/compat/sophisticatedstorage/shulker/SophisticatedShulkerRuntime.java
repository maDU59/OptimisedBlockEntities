package fr.madu59.obe.client.compat.sophisticatedstorage.shulker;

import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedAppearanceExt;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.SpecialBakedModelCache;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlockEntity;

public final class SophisticatedShulkerRuntime {
    private SophisticatedShulkerRuntime() {
    }

    public static void updateAnimationMode(ShulkerBoxBlockEntity shulker) {
        BlockEntityExt ext = (BlockEntityExt) shulker;
        boolean heldByFailure = ext.specialModelState().fallbackPending()
                && ext.specialModelState().failedGeneration() == SpecialBakedModelCache.generation();
        boolean closed = isFullyClosed(shulker);
        RenderModeManager.setRenderModeDelayed(
                shulker, closed && !heldByFailure ? RenderMode.TERRAIN : RenderMode.ENTITY, shulker.getBlockPos());
    }

    static boolean isFullyClosed(ShulkerBoxBlockEntity shulker) {
        return shulker.isClosed()
                && shulker.getProgress(0.0F) <= 1.0E-4F
                && shulker.getProgress(1.0F) <= 1.0E-4F;
    }

    public static void appearanceMayHaveChanged(ShulkerBoxBlockEntity shulker) {
        Level level = shulker.getLevel();
        if (level == null || !level.isClientSide()) {
            return;
        }
        ShulkerAppearanceFingerprint current = ShulkerAppearanceFingerprint.capture(shulker);
        SophisticatedAppearanceExt tracked = (SophisticatedAppearanceExt) shulker;
        if (!current.equals(tracked.obe$appearanceFingerprint())) {
            tracked.obe$appearanceFingerprint(current);
            ((BlockEntityExt) shulker).specialModelState().resetFailure();
            updateAnimationMode(shulker);
            RenderModeManager.setDirty(shulker.getBlockPos());
        }
    }
}

package fr.madu59.obe.client.compat.sophisticatedstorage;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeItemBase;
import net.p3pp3rf1y.sophisticatedstorage.init.ModItems;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageTierUpgradeItem;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageToolItem;

/** Shared once-per-game-tick mirror of the pinned StorageRenderer hand cache. */
public final class SophisticatedPlayerRenderState {
    private static final SophisticatedPlayerRenderState INSTANCE =
            new SophisticatedPlayerRenderState();

    private long lastGameTime = Long.MIN_VALUE;
    private PlayerOverlayState snapshot = PlayerOverlayState.NONE;

    SophisticatedPlayerRenderState() {
    }

    public static PlayerOverlayState current() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return INSTANCE.currentForTick(level == null ? null : level.getGameTime(), false,
                    () -> PlayerOverlayState.NONE);
        }
        return INSTANCE.currentForTick(
                level == null ? null : level.getGameTime(),
                true,
                () -> classifyHands(player.getMainHandItem(), player.getOffhandItem()));
    }

    PlayerOverlayState currentForTick(@Nullable Long gameTime, boolean playerPresent,
            Supplier<PlayerOverlayState> snapshotFactory) {
        if (gameTime == null) {
            lastGameTime = Long.MIN_VALUE;
            snapshot = PlayerOverlayState.NONE;
            return snapshot;
        }
        if (!playerPresent) {
            lastGameTime = gameTime;
            snapshot = PlayerOverlayState.NONE;
            return snapshot;
        }
        if (gameTime != lastGameTime) {
            snapshot = snapshotFactory.get();
            lastGameTime = gameTime;
        }
        return snapshot;
    }

    static PlayerOverlayState fromHands(HeldItemState mainHand, HeldItemState offHand) {
        HeldItemState selectedTool = mainHand.storageTool() ? mainHand
                : offHand.storageTool() ? offHand : null;
        StorageToolItem.Mode mode = selectedTool == null ? null : selectedTool.storageToolMode();
        boolean holdsStorageTool = selectedTool != null;
        boolean showsUpgrades = holdsStorageTool || mainHand.storageUpgrade() || offHand.storageUpgrade();
        boolean showsHiddenTiers = mode == StorageToolItem.Mode.TIER_DISPLAY
                || mainHand.storageTierUpgrade() || offHand.storageTierUpgrade();
        boolean showsLockPreview = mode == StorageToolItem.Mode.LOCK_DISPLAY
                || mode == StorageToolItem.Mode.LOCK;
        boolean showsDisabledUpgrades = mode == StorageToolItem.Mode.UPGRADES_DISPLAY;
        return new PlayerOverlayState(
                showsUpgrades, showsHiddenTiers, showsLockPreview, showsDisabledUpgrades, mode);
    }

    private static PlayerOverlayState classifyHands(ItemStack mainHand, ItemStack offHand) {
        return fromHands(classify(mainHand), classify(offHand));
    }

    private static HeldItemState classify(ItemStack stack) {
        boolean storageTool = stack.getItem() == ModItems.STORAGE_TOOL.get();
        StorageToolItem.Mode mode = storageTool ? StorageToolItem.getMode(stack) : null;
        boolean storageUpgrade = stack.getItem() instanceof UpgradeItemBase
                && stack.is(ModItems.STORAGE_UPGRADE_TAG);
        boolean storageTierUpgrade = stack.getItem() instanceof StorageTierUpgradeItem;
        return new HeldItemState(storageTool, mode, storageUpgrade, storageTierUpgrade);
    }

    record HeldItemState(
            boolean storageTool,
            @Nullable StorageToolItem.Mode storageToolMode,
            boolean storageUpgrade,
            boolean storageTierUpgrade
    ) {
    }
}

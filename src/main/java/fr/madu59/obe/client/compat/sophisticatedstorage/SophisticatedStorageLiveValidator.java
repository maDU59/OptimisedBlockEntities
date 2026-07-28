package fr.madu59.obe.client.compat.sophisticatedstorage;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.compat.sophisticatedstorage.chest.SophisticatedChestRuntime;
import fr.madu59.obe.client.compat.sophisticatedstorage.shulker.SophisticatedShulkerRuntime;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.renderer.blockentity.DynamicBlockEntityRenderManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.SpecialBakedModelCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.p3pp3rf1y.sophisticatedcore.renderdata.DisplaySide;
import net.p3pp3rf1y.sophisticatedcore.settings.itemdisplay.ItemDisplaySettingsCategory;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.WoodStorageBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.init.ModDataComponents;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedstorage.init.ModItems;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageToolItem;

/**
 * Opt-in development harness for the pinned live compatibility matrix. It is
 * unreachable in production unless the explicit {@code obe.liveValidation}
 * JVM property is supplied.
 */
public final class SophisticatedStorageLiveValidator {
    private static final int Y = 90;
    private static final List<BlockPos> CHESTS = grid(4, 7);
    private static final List<BlockPos> SHULKERS = grid(10, 13);
    private static final List<BlockPos> BARRELS = List.of(new BlockPos(-5, Y, 16), new BlockPos(-3, Y, 16));
    private static final List<BlockPos> PERFORMANCE_CHESTS = performanceGrid(20, 20);
    private static final BlockPos DOUBLE_LEFT = new BlockPos(15, Y, 4);
    private static final BlockPos DOUBLE_RIGHT = new BlockPos(16, Y, 4);
    private static final BlockPos INVALIDATION_CHEST = CHESTS.get(4);
    private static final Direction[] DIRECTIONS = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST
    };
    private static final WoodType[] WOODS = {
            WoodType.OAK, WoodType.ACACIA, WoodType.BIRCH, WoodType.CRIMSON, WoodType.MANGROVE, WoodType.BAMBOO
    };

    private static Phase phase = Phase.WAITING_FOR_WORLD;
    private static int phaseTicks;
    private static final AtomicBoolean serverTaskDone = new AtomicBoolean();
    private static volatile Throwable serverTaskFailure;
    private static double originalX;
    private static double originalY;
    private static double originalZ;
    private static float originalYaw;
    private static float originalPitch;
    private static GameType originalGameType;
    private static ItemStack originalMainHand = ItemStack.EMPTY;
    private static ItemStack originalOffHand = ItemStack.EMPTY;
    private static Object invalidationAppearance;
    private static Object doubleLeftAppearance;
    private static Object doubleRightAppearance;
    private static long generationBeforeReload;
    private static boolean reloadComplete;
    private static float cameraYaw;
    private static float cameraPitch = 18.0F;
    private static final ThreadMXBean THREAD_CPU = ManagementFactory.getThreadMXBean();
    private static final boolean RENDER_CPU_TIME_AVAILABLE = enableThreadCpuTime();
    private static boolean samplingPerformance;
    private static long renderWallStart;
    private static long renderCpuStart;
    private static MetricStart metricStart;
    private static final List<Long> renderWallSamples = new ArrayList<>();
    private static final List<Long> renderCpuSamples = new ArrayList<>();
    private static final List<Integer> fpsSamples = new ArrayList<>();
    private static PerformanceSample wholeSkipSample;
    private static PerformanceSample shellOnlySample;
    private static PerformanceSample originalBerSample;
    private static PerformanceSample displayedItemSample;
    private static SophisticatedStorageDiagnostics.Snapshot overlayMetricStart;
    private static long upgradePreviewGeneration;
    private static long tierPreviewGeneration;
    private static long lockPreviewGeneration;
    private static String finalResult;

    private SophisticatedStorageLiveValidator() {}

    public static void initIfRequested() {
        if (!Boolean.getBoolean("obe.liveValidation")) {
            return;
        }
        NeoForge.EVENT_BUS.addListener(SophisticatedStorageLiveValidator::onClientTick);
        NeoForge.EVENT_BUS.addListener(SophisticatedStorageLiveValidator::onFramePre);
        NeoForge.EVENT_BUS.addListener(SophisticatedStorageLiveValidator::onFramePost);
        OBE.LOGGER.info("OBE LIVE VALIDATION armed for the pinned Sophisticated Storage matrix");
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        try {
            tick(minecraft);
        } catch (Throwable failure) {
            fail(minecraft, failure);
        }
    }

    private static void onFramePre(RenderFrameEvent.Pre event) {
        if (!samplingPerformance) {
            return;
        }
        renderWallStart = System.nanoTime();
        renderCpuStart = RENDER_CPU_TIME_AVAILABLE ? THREAD_CPU.getCurrentThreadCpuTime() : -1;
    }

    private static void onFramePost(RenderFrameEvent.Post event) {
        if (!samplingPerformance || renderWallStart <= 0) {
            return;
        }
        renderWallSamples.add(System.nanoTime() - renderWallStart);
        if (renderCpuStart >= 0) {
            renderCpuSamples.add(THREAD_CPU.getCurrentThreadCpuTime() - renderCpuStart);
        }
        renderWallStart = 0;
        renderCpuStart = -1;
    }

    private static void tick(Minecraft minecraft) {
        phaseTicks++;
        if (minecraft.player != null) {
            // A paused/menu screen invalidates both the visual capture and the integrated-server timing.
            // This harness is opt-in and owns the client for its entire run, so keep gameplay focused.
            if (minecraft.screen != null) {
                minecraft.setScreen(null);
            }
            minecraft.player.setYRot(cameraYaw);
            minecraft.player.setYHeadRot(cameraYaw);
            minecraft.player.setXRot(cameraPitch);
        }

        switch (phase) {
            case WAITING_FOR_WORLD -> {
                if (minecraft.level == null || minecraft.player == null || minecraft.getSingleplayerServer() == null) {
                    return;
                }
                minecraft.setScreen(null);
                submitServerTask(minecraft, () -> setupScene(minecraft));
                advance(Phase.SETTING_UP_SCENE);
            }
            case SETTING_UP_SCENE -> {
                checkServerTask();
                if (serverTaskDone.get()) {
                    advance(Phase.WAITING_FOR_CLOSED_MATRIX);
                }
            }
            case WAITING_FOR_CLOSED_MATRIX -> {
                if (phaseTicks == 100) {
                    capture(minecraft, "obe-sophisticated-closed-matrix.png");
                }
                if (phaseTicks >= 220) {
                    validateClosedMatrix(minecraft, false);
                    // Keep the mixed visual matrix outside the 12-chunk client view while sampling.
                    // Its deliberate overlays otherwise pollute the global completed/suppression counters.
                    moveCamera(minecraft, 0.0, Y + 30.0, 282.0, 0.0F, 48.0F);
                    advance(Phase.PREPARING_PERFORMANCE);
                }
            }
            case PREPARING_PERFORMANCE -> {
                if (phaseTicks >= 240) {
                    validatePerformanceTerrain(minecraft);
                    beginPerformanceSample();
                    advance(Phase.SAMPLING_WHOLE_SKIP);
                }
            }
            case SAMPLING_WHOLE_SKIP -> {
                sampleFps(minecraft, fpsSamples);
                if (phaseTicks >= 200) {
                    wholeSkipSample = finishPerformanceSample();
                    require(wholeSkipSample.wholeSkipped() > 0, "whole-BER sample skipped no chest renderers");
                    require(wholeSkipSample.wholeExecuted() == 0,
                            "ordinary closed scene still executed chest renderers: " + wholeSkipSample);
                    require(wholeSkipSample.completedOriginal() == 0,
                            "ordinary closed scene completed original chest renderers: " + wholeSkipSample);
                    require(wholeSkipSample.suppressedShellCalls() == 0,
                            "early-skip scene still reached shell suppression: " + wholeSkipSample);
                    if (ModCompat.isSodiumLoaded()) {
                        require(wholeSkipSample.sodiumPreExtractionSkips() > 0,
                                "Sodium reported no dynamic pre-extraction skips");
                    }
                    SophisticatedDynamicRenderControl.setWholeRendererSkipEnabledForValidation(false);
                    advance(Phase.PREPARING_SHELL_ONLY);
                }
            }
            case PREPARING_SHELL_ONLY -> {
                if (phaseTicks >= 40) {
                    beginPerformanceSample();
                    advance(Phase.SAMPLING_SHELL_ONLY);
                }
            }
            case SAMPLING_SHELL_ONLY -> {
                sampleFps(minecraft, fpsSamples);
                if (phaseTicks >= 200) {
                    shellOnlySample = finishPerformanceSample();
                    require(shellOnlySample.wholeSkipped() == 0, "shell-only sample unexpectedly early-skipped");
                    require(shellOnlySample.completedOriginal() > 0, "shell-only sample completed no original renderers");
                    require(shellOnlySample.suppressedShellCalls() > 0, "shell-only sample suppressed no shell calls");
                    SettingsManager.MOD_TOGGLE.setValue(false);
                    dirtyPerformance();
                    advance(Phase.PREPARING_ORIGINAL_BER);
                }
            }
            case PREPARING_ORIGINAL_BER -> {
                if (phaseTicks == 80) {
                    capture(minecraft, "obe-sophisticated-original-ber-baseline.png");
                }
                if (phaseTicks >= 160) {
                    beginPerformanceSample();
                    advance(Phase.SAMPLING_ORIGINAL_BER);
                }
            }
            case SAMPLING_ORIGINAL_BER -> {
                sampleFps(minecraft, fpsSamples);
                if (phaseTicks >= 200) {
                    originalBerSample = finishPerformanceSample();
                    require(originalBerSample.wholeSkipped() == 0, "original BER sample unexpectedly skipped");
                    require(originalBerSample.completedOriginal() > 0, "original BER sample completed no renderers");
                    require(originalBerSample.passthroughShellCalls() > 0, "original BER sample submitted no shell calls");
                    SettingsManager.MOD_TOGGLE.setValue(true);
                    SophisticatedDynamicRenderControl.setWholeRendererSkipEnabledForValidation(true);
                    enableAndDirtyPerformance(minecraft);
                    advance(Phase.RESTORING_PERFORMANCE_TERRAIN);
                }
            }
            case RESTORING_PERFORMANCE_TERRAIN -> {
                if (phaseTicks >= 240) {
                    validatePerformanceTerrain(minecraft);
                    submitServerTask(minecraft, () -> addDisplayedItems(minecraft));
                    advance(Phase.CONFIGURING_DISPLAYED_ITEMS);
                }
            }
            case CONFIGURING_DISPLAYED_ITEMS -> {
                checkServerTask();
                if (serverTaskDone.get() && phaseTicks >= 120) {
                    beginPerformanceSample();
                    advance(Phase.SAMPLING_DISPLAYED_ITEMS);
                }
            }
            case SAMPLING_DISPLAYED_ITEMS -> {
                sampleFps(minecraft, fpsSamples);
                if (phaseTicks >= 200) {
                    displayedItemSample = finishPerformanceSample();
                    require(displayedItemSample.wholeSkipped() == 0,
                            "displayed-item scene unexpectedly skipped chest renderers");
                    require(displayedItemSample.completedOriginal() > 0,
                            "displayed-item scene completed no original renderers");
                    require(displayedItemSample.displayItemExecutions() > 0,
                            "displayed-item scene recorded no display-item execution reason");
                    require(displayedItemSample.suppressedShellCalls() > 0,
                            "displayed-item scene did not retain shell-level suppression");
                    capture(minecraft, "obe-sophisticated-performance-displayed-items.png");
                    submitServerTask(minecraft, () -> removePerformanceScene(minecraft));
                    advance(Phase.CLEANING_PERFORMANCE_SCENE);
                }
            }
            case CLEANING_PERFORMANCE_SCENE -> {
                checkServerTask();
                if (serverTaskDone.get() && phaseTicks >= 80) {
                    moveCamera(minecraft, 0.0, Y + 4.0, -11.0);
                    advance(Phase.PREPARING_TOOL_PREVIEWS);
                }
            }
            case PREPARING_TOOL_PREVIEWS -> {
                if (phaseTicks >= 120) {
                    upgradePreviewGeneration = committedGeneration(minecraft, CHESTS.get(5));
                    overlayMetricStart = SophisticatedStorageDiagnostics.snapshot();
                    submitServerTask(minecraft, () -> setStorageTool(minecraft,
                            StorageToolItem.Mode.UPGRADES_DISPLAY));
                    advance(Phase.WAITING_FOR_UPGRADE_PREVIEW);
                }
            }
            case WAITING_FOR_UPGRADE_PREVIEW -> {
                checkServerTask();
                if (serverTaskDone.get() && phaseTicks >= 20) {
                    PlayerOverlayState player = SophisticatedPlayerRenderState.current();
                    var now = SophisticatedStorageDiagnostics.snapshot();
                    require(player.showsUpgrades() && player.showsDisabledUpgrades(),
                            "upgrades-display tool mode was not visible to the per-tick cache");
                    require(now.upgradeExecutions() > overlayMetricStart.upgradeExecutions(),
                            "upgrades-display tool mode executed no dynamic overlay renderers");
                    require(committedGeneration(minecraft, CHESTS.get(5)) == upgradePreviewGeneration,
                            "upgrade preview forced a terrain remesh");
                    capture(minecraft, "obe-sophisticated-tool-upgrades-preview.png");
                    tierPreviewGeneration = committedGeneration(minecraft, CHESTS.get(2));
                    overlayMetricStart = now;
                    submitServerTask(minecraft, () -> setStorageTool(minecraft,
                            StorageToolItem.Mode.TIER_DISPLAY));
                    advance(Phase.WAITING_FOR_TIER_PREVIEW);
                }
            }
            case WAITING_FOR_TIER_PREVIEW -> {
                checkServerTask();
                if (serverTaskDone.get() && phaseTicks >= 20) {
                    PlayerOverlayState player = SophisticatedPlayerRenderState.current();
                    var now = SophisticatedStorageDiagnostics.snapshot();
                    require(player.showsHiddenTiers(),
                            "tier-display tool mode was not visible to the per-tick cache");
                    require(now.hiddenTierExecutions() > overlayMetricStart.hiddenTierExecutions(),
                            "tier-display tool mode executed no hidden-tier renderers");
                    require(committedGeneration(minecraft, CHESTS.get(2)) == tierPreviewGeneration,
                            "hidden-tier preview forced a terrain remesh");
                    capture(minecraft, "obe-sophisticated-tool-tier-preview.png");
                    lockPreviewGeneration = committedGeneration(minecraft, CHESTS.get(7));
                    overlayMetricStart = now;
                    submitServerTask(minecraft, () -> setStorageTool(minecraft,
                            StorageToolItem.Mode.LOCK_DISPLAY));
                    advance(Phase.WAITING_FOR_LOCK_PREVIEW);
                }
            }
            case WAITING_FOR_LOCK_PREVIEW -> {
                checkServerTask();
                if (serverTaskDone.get() && phaseTicks >= 20) {
                    PlayerOverlayState player = SophisticatedPlayerRenderState.current();
                    var now = SophisticatedStorageDiagnostics.snapshot();
                    require(player.showsLockPreview(),
                            "lock-display tool mode was not visible to the per-tick cache");
                    // Every storage-tool mode also requests empty upgrade slots in the pinned
                    // renderer, so UPGRADES can legitimately be the one recorded primary reason.
                    require(now.wholeChestRenderersExecuted() > overlayMetricStart.wholeChestRenderersExecuted(),
                            "lock-display tool mode executed no dynamic chest renderers");
                    require(committedGeneration(minecraft, CHESTS.get(7)) == lockPreviewGeneration,
                            "hidden-lock preview forced a terrain remesh");
                    capture(minecraft, "obe-sophisticated-tool-lock-preview.png");
                    submitServerTask(minecraft, () -> clearValidationHands(minecraft));
                    advance(Phase.WAITING_FOR_TOOL_RELEASE);
                }
            }
            case WAITING_FOR_TOOL_RELEASE -> {
                checkServerTask();
                if (serverTaskDone.get() && phaseTicks >= 20) {
                    PlayerOverlayState player = SophisticatedPlayerRenderState.current();
                    require(!player.showsUpgrades() && !player.showsDisabledUpgrades()
                                    && !player.showsHiddenTiers() && !player.showsLockPreview(),
                            "tool preview state did not clear on the next client tick");
                    require(committedGeneration(minecraft, CHESTS.get(5)) == upgradePreviewGeneration,
                            "releasing the upgrades tool forced a terrain remesh");
                    require(committedGeneration(minecraft, CHESTS.get(2)) == tierPreviewGeneration,
                            "releasing the tier tool forced a terrain remesh");
                    require(committedGeneration(minecraft, CHESTS.get(7)) == lockPreviewGeneration,
                            "releasing the lock tool forced a terrain remesh");
                    capture(minecraft, "obe-sophisticated-tool-preview-released.png");
                    moveCamera(minecraft, 15.5, Y + 4.0, -6.0);
                    advance(Phase.WAITING_FOR_DOUBLE_CHEST);
                }
            }
            case WAITING_FOR_DOUBLE_CHEST -> {
                if (phaseTicks == 100) {
                    capture(minecraft, "obe-sophisticated-cross-chunk-double.png");
                }
                if (phaseTicks >= 180) {
                    validateOptimized(minecraft, DOUBLE_LEFT);
                    validateOptimized(minecraft, DOUBLE_RIGHT);
                    doubleLeftAppearance = ext(minecraft, DOUBLE_LEFT).specialModelState().committedAppearance();
                    doubleRightAppearance = ext(minecraft, DOUBLE_RIGHT).specialModelState().committedAppearance();
                    invalidationAppearance = ext(minecraft, INVALIDATION_CHEST).specialModelState().committedAppearance();
                    submitServerTask(minecraft, () -> mutateAppearances(minecraft));
                    advance(Phase.WAITING_FOR_INVALIDATION);
                }
            }
            case WAITING_FOR_INVALIDATION -> {
                checkServerTask();
                if (serverTaskDone.get() && phaseTicks >= 180) {
                    validateDoubleAppearanceInvalidation(minecraft);
                    moveCamera(minecraft, 0.0, Y + 4.0, -11.0);
                    advance(Phase.RETURNING_TO_MATRIX);
                }
            }
            case RETURNING_TO_MATRIX -> {
                if (phaseTicks >= 140) {
                    validateSingleAppearanceInvalidation(minecraft);
                    setOpen(minecraft, true);
                    advance(Phase.WAITING_FOR_OPEN_TRANSITION);
                }
            }
            case WAITING_FOR_OPEN_TRANSITION -> {
                if (phaseTicks == 35) {
                    capture(minecraft, "obe-sophisticated-open-transition.png");
                }
                if (phaseTicks >= 100) {
                    validateEntityMode(minecraft, CHESTS.get(0));
                    validateEntityMode(minecraft, SHULKERS.get(0));
                    validateEntityMode(minecraft, DOUBLE_LEFT);
                    validateEntityMode(minecraft, DOUBLE_RIGHT);
                    setOpen(minecraft, false);
                    advance(Phase.WAITING_FOR_CLOSE_TRANSITION);
                }
            }
            case WAITING_FOR_CLOSE_TRANSITION -> {
                if (phaseTicks >= 180) {
                    validateOptimized(minecraft, CHESTS.get(0));
                    validateOptimized(minecraft, SHULKERS.get(0));
                    validateOptimized(minecraft, DOUBLE_LEFT);
                    validateOptimized(minecraft, DOUBLE_RIGHT);
                    injectFallback(minecraft);
                    advance(Phase.WAITING_FOR_FALLBACK);
                }
            }
            case WAITING_FOR_FALLBACK -> {
                if (phaseTicks >= 100) {
                    validateEntityMode(minecraft, CHESTS.get(0));
                    var ext = ext(minecraft, CHESTS.get(0));
                    require(ext.specialModelState().fallbackPending(), "injected fallback was not retained");
                    require(!SophisticatedRenderContext.shouldSuppress(ext), "fallback still suppressed the original shell");
                    ext.specialModelState().resetFailure();
                    SophisticatedChestRuntime.updateAnimationMode(chest(minecraft, CHESTS.get(0)));
                    advance(Phase.WAITING_FOR_FALLBACK_RECOVERY);
                }
            }
            case WAITING_FOR_FALLBACK_RECOVERY -> {
                if (phaseTicks >= 160) {
                    validateOptimized(minecraft, CHESTS.get(0));
                    generationBeforeReload = SpecialBakedModelCache.generation();
                    reloadComplete = false;
                    minecraft.reloadResourcePacks().whenComplete((unused, error) -> {
                        if (error != null) {
                            serverTaskFailure = error;
                        }
                        reloadComplete = true;
                    });
                    advance(Phase.RELOADING_RESOURCES);
                }
            }
            case RELOADING_RESOURCES -> {
                if (serverTaskFailure != null) {
                    throw new IllegalStateException("resource reload failed", serverTaskFailure);
                }
                if (reloadComplete) {
                    advance(Phase.WAITING_FOR_RELOAD_REBUILD);
                }
            }
            case WAITING_FOR_RELOAD_REBUILD -> {
                if (phaseTicks >= 220) {
                    require(SpecialBakedModelCache.generation() > generationBeforeReload,
                            "resource reload did not advance the arbitrary-model generation");
                    validateClosedMatrix(minecraft, true);
                    capture(minecraft, "obe-sophisticated-after-reload.png");
                    finish(minecraft);
                }
            }
            case RESTORING_PLAYER -> {
                checkServerTask();
                if (serverTaskDone.get() && phaseTicks >= 20) {
                    writeResult(minecraft, finalResult);
                    OBE.LOGGER.info(finalResult);
                    minecraft.stop();
                    advance(Phase.DONE);
                }
            }
            case DONE -> {
            }
        }
    }

    private static void setupScene(Minecraft minecraft) {
        ServerPlayer player = serverPlayer(minecraft);
        originalX = player.getX();
        originalY = player.getY();
        originalZ = player.getZ();
        originalYaw = player.getYRot();
        originalPitch = player.getXRot();
        originalGameType = player.gameMode.getGameModeForPlayer();
        originalMainHand = player.getMainHandItem().copy();
        originalOffHand = player.getOffhandItem().copy();
        clearValidationHands(minecraft);
        player.setGameMode(GameType.SPECTATOR);
        player.teleportTo(player.serverLevel(), 0.0, Y + 4.0, -11.0, 0.0F, 18.0F);

        var level = player.serverLevel();
        List<ChestBlock> chestBlocks = List.of(
                ModBlocks.CHEST.get(), ModBlocks.COPPER_CHEST.get(), ModBlocks.IRON_CHEST.get(),
                ModBlocks.GOLD_CHEST.get(), ModBlocks.DIAMOND_CHEST.get(), ModBlocks.NETHERITE_CHEST.get());
        List<ShulkerBoxBlock> shulkerBlocks = List.of(
                ModBlocks.SHULKER_BOX.get(), ModBlocks.COPPER_SHULKER_BOX.get(), ModBlocks.IRON_SHULKER_BOX.get(),
                ModBlocks.GOLD_SHULKER_BOX.get(), ModBlocks.DIAMOND_SHULKER_BOX.get(), ModBlocks.NETHERITE_SHULKER_BOX.get());

        for (int i = 0; i < CHESTS.size(); i++) {
            BlockPos pos = CHESTS.get(i);
            preparePosition(level, pos);
            ChestBlock block = chestBlocks.get(i % chestBlocks.size());
            level.setBlock(pos, block.defaultBlockState()
                    .setValue(ChestBlock.FACING, DIRECTIONS[i % DIRECTIONS.length])
                    .setValue(ChestBlock.TYPE, ChestType.SINGLE), 3);
            ChestBlockEntity chest = (ChestBlockEntity) level.getBlockEntity(pos);
            chest.setWoodType(WOODS[i % WOODS.length]);
            configureStorage(chest, i);
        }

        Direction[] shulkerDirections = Direction.values();
        for (int i = 0; i < SHULKERS.size(); i++) {
            BlockPos pos = SHULKERS.get(i);
            preparePosition(level, pos);
            ShulkerBoxBlock block = shulkerBlocks.get(i % shulkerBlocks.size());
            level.setBlock(pos, block.defaultBlockState()
                    .setValue(ShulkerBoxBlock.FACING, shulkerDirections[i % shulkerDirections.length]), 3);
            configureStorage((ShulkerBoxBlockEntity) level.getBlockEntity(pos), i + 12);
        }

        preparePosition(level, DOUBLE_LEFT);
        preparePosition(level, DOUBLE_RIGHT);
        ChestBlock doubleBlock = ModBlocks.DIAMOND_CHEST.get();
        level.setBlock(DOUBLE_RIGHT, doubleBlock.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.NORTH).setValue(ChestBlock.TYPE, ChestType.RIGHT), 2);
        level.setBlock(DOUBLE_LEFT, doubleBlock.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.NORTH).setValue(ChestBlock.TYPE, ChestType.LEFT), 2);
        ChestBlockEntity left = (ChestBlockEntity) level.getBlockEntity(DOUBLE_LEFT);
        ChestBlockEntity right = (ChestBlockEntity) level.getBlockEntity(DOUBLE_RIGHT);
        left.joinWithChest(right);
        right.getStorageWrapper().setColors(DyeColor.BLUE.getTextureDiffuseColor(), DyeColor.LIGHT_BLUE.getTextureDiffuseColor());
        level.sendBlockUpdated(DOUBLE_LEFT, left.getBlockState(), left.getBlockState(), 3);
        level.sendBlockUpdated(DOUBLE_RIGHT, right.getBlockState(), right.getBlockState(), 3);

        for (int i = 0; i < BARRELS.size(); i++) {
            BlockPos pos = BARRELS.get(i);
            preparePosition(level, pos);
            Block block = i == 0 ? ModBlocks.BARREL.get() : ModBlocks.LIMITED_BARREL_1.get();
            level.setBlock(pos, block.defaultBlockState(), 3);
        }

        ChestBlock performanceBlock = ModBlocks.CHEST.get();
        for (BlockPos pos : PERFORMANCE_CHESTS) {
            preparePosition(level, pos);
            level.setBlock(pos, performanceBlock.defaultBlockState()
                    .setValue(ChestBlock.FACING, Direction.NORTH)
                    .setValue(ChestBlock.TYPE, ChestType.SINGLE), 3);
            ChestBlockEntity chest = (ChestBlockEntity) level.getBlockEntity(pos);
            chest.setWoodType(WoodType.OAK);
            chest.setChanged();
        }
    }

    private static void preparePosition(net.minecraft.server.level.ServerLevel level, BlockPos pos) {
        level.setBlock(pos.below(), Blocks.SMOOTH_STONE.defaultBlockState(), 3);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
    }

    private static void configureStorage(StorageBlockEntity storage, int index) {
        int main = DyeColor.values()[index % DyeColor.values().length].getTextureDiffuseColor();
        int accent = DyeColor.values()[(index + 5) % DyeColor.values().length].getTextureDiffuseColor();
        if ((index & 1) == 1) {
            storage.getStorageWrapper().setColors(main, accent);
        }
        if (index % 6 == 0 || index == 11 || index == 23) {
            storage.getStorageWrapper().getInventoryHandler().setStackInSlot(0, new ItemStack(Items.DIAMOND));
            ItemDisplaySettingsCategory display = storage.getStorageWrapper().getSettingsHandler()
                    .getTypeCategory(ItemDisplaySettingsCategory.class);
            display.selectSlot(0);
            display.setDisplaySide(switch (index) {
                case 6, 18 -> DisplaySide.LEFT;
                case 11, 23 -> DisplaySide.RIGHT;
                default -> DisplaySide.FRONT;
            });
        } else if (index % 6 == 1) {
            storage.toggleLock();
            if (index >= 6) {
                storage.toggleLockVisibility();
            }
        } else if (index % 6 == 2) {
            storage.toggleTierVisiblity();
        } else if (index % 6 == 3 && storage instanceof WoodStorageBlockEntity woodStorage) {
            woodStorage.setPacked(true);
        } else if (index % 6 == 4) {
            storage.toggleUpgradesVisiblity();
        }
        storage.setChanged();
    }

    private static void addDisplayedItems(Minecraft minecraft) {
        var level = serverPlayer(minecraft).serverLevel();
        for (BlockPos pos : PERFORMANCE_CHESTS) {
            ChestBlockEntity chest = (ChestBlockEntity) level.getBlockEntity(pos);
            if (chest == null) {
                throw new IllegalStateException("missing performance chest at " + pos);
            }
            chest.getStorageWrapper().getInventoryHandler().setStackInSlot(0, new ItemStack(Items.DIAMOND));
            chest.getStorageWrapper().getSettingsHandler()
                    .getTypeCategory(ItemDisplaySettingsCategory.class).selectSlot(0);
            chest.setChanged();
            level.sendBlockUpdated(pos, chest.getBlockState(), chest.getBlockState(), 3);
        }
    }

    private static void removePerformanceScene(Minecraft minecraft) {
        var level = serverPlayer(minecraft).serverLevel();
        for (BlockPos pos : PERFORMANCE_CHESTS) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            level.setBlock(pos.below(), Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private static void mutateAppearances(Minecraft minecraft) {
        var level = serverPlayer(minecraft).serverLevel();
        ChestBlockEntity single = (ChestBlockEntity) level.getBlockEntity(INVALIDATION_CHEST);
        single.getStorageWrapper().setColors(
                DyeColor.BLACK.getTextureDiffuseColor(), DyeColor.WHITE.getTextureDiffuseColor());
        single.setWoodType(WoodType.CHERRY);
        ChestBlockEntity doubleChest = (ChestBlockEntity) level.getBlockEntity(DOUBLE_RIGHT);
        doubleChest.getMainStorageWrapper().setColors(
                DyeColor.RED.getTextureDiffuseColor(), DyeColor.YELLOW.getTextureDiffuseColor());
        level.sendBlockUpdated(INVALIDATION_CHEST, single.getBlockState(), single.getBlockState(), 3);
        level.sendBlockUpdated(DOUBLE_LEFT, level.getBlockState(DOUBLE_LEFT), level.getBlockState(DOUBLE_LEFT), 3);
        level.sendBlockUpdated(DOUBLE_RIGHT, doubleChest.getBlockState(), doubleChest.getBlockState(), 3);
    }

    private static void validateClosedMatrix(Minecraft minecraft, boolean afterReload) {
        for (BlockPos pos : CHESTS) {
            validateOptimized(minecraft, pos);
        }
        for (BlockPos pos : SHULKERS) {
            validateOptimized(minecraft, pos);
        }
        for (BlockPos pos : BARRELS) {
            BlockEntity blockEntity = requireBlockEntity(minecraft, pos);
            require(!((BlockEntityExt) blockEntity).isSupported(), "barrel entered OBE compatibility at " + pos);
        }
        var diagnostics = SophisticatedStorageDiagnostics.snapshot();
        long expectedFallbacks = afterReload ? 1 : 0;
        require(diagnostics.fallbacks() == expectedFallbacks,
                "unexpected live bake fallback count " + diagnostics.fallbacks()
                        + " (expected " + expectedFallbacks + ")");
        require(diagnostics.missingSprites() == 0, "missing live material sprites " + diagnostics.missingSprites());
        require(diagnostics.atlasMissing() == 0, "missing pre-stitch atlas resources " + diagnostics.atlasMissing());
        require(diagnostics.atlasDiscovered() > 0, "dynamic atlas source discovered no sprites");
        require(diagnostics.suppressedShellCalls() > 0, "static shell suppression never ran");
        require(diagnostics.completedDynamicRenderers() > 0, "original overlay renderer never completed");
        require(diagnostics.cache().builds() > 0 && diagnostics.cache().size() <= diagnostics.cache().maximumSize(),
                "bounded arbitrary-model cache is invalid: " + diagnostics.cache());
        SophisticatedStorageDiagnostics.logSnapshot(afterReload ? "live matrix after reload" : "live closed matrix");
    }

    private static void validateSingleAppearanceInvalidation(Minecraft minecraft) {
        validateOptimized(minecraft, INVALIDATION_CHEST);
        Object current = ext(minecraft, INVALIDATION_CHEST).specialModelState().committedAppearance();
        require(!invalidationAppearance.equals(current),
                "single-chest appearance mutation did not rebake after returning it to view; old="
                        + invalidationAppearance + ", current=" + current);
        OBE.LOGGER.info("OBE LIVE VALIDATION single-chest invalidation PASS");
    }

    private static void validateDoubleAppearanceInvalidation(Minecraft minecraft) {
        validateOptimized(minecraft, DOUBLE_LEFT);
        validateOptimized(minecraft, DOUBLE_RIGHT);
        Object currentLeft = ext(minecraft, DOUBLE_LEFT).specialModelState().committedAppearance();
        Object currentRight = ext(minecraft, DOUBLE_RIGHT).specialModelState().committedAppearance();
        var wrapper = chest(minecraft, DOUBLE_RIGHT).getMainStorageWrapper();
        require(!doubleLeftAppearance.equals(currentLeft),
                "cross-chunk double left half did not invalidate; clientColors=" + wrapper.getMainColor() + "/"
                        + wrapper.getAccentColor() + ", old=" + doubleLeftAppearance + ", current=" + currentLeft);
        require(!doubleRightAppearance.equals(currentRight),
                "cross-chunk double right half did not invalidate; clientColors=" + wrapper.getMainColor() + "/"
                        + wrapper.getAccentColor() + ", old=" + doubleRightAppearance + ", current=" + currentRight);
        OBE.LOGGER.info("OBE LIVE VALIDATION double-chest invalidation PASS: both x=15/16 halves");
    }

    private static void validateOptimized(Minecraft minecraft, BlockPos pos) {
        BlockEntityExt ext = ext(minecraft, pos);
        require(ext.isSupported(), "expected supported block entity at " + pos);
        require(ext.isEnabled(), "expected enabled block entity at " + pos);
        require(ext.renderMode() == RenderMode.TERRAIN, "active mode is not TERRAIN at " + pos + ": " + ext.renderMode());
        require(ext.specialModelState().hasCommittedTerrain(), "terrain appearance is not committed at " + pos);
        require(!ext.specialModelState().fallbackPending(), "fallback remains pending at " + pos);
        require(SophisticatedRenderContext.shouldSuppress(ext), "committed closed shell is not suppressible at " + pos);
    }

    private static void validatePerformanceTerrain(Minecraft minecraft) {
        for (BlockPos pos : PERFORMANCE_CHESTS) {
            validateOptimized(minecraft, pos);
        }
        OBE.LOGGER.info("OBE LIVE VALIDATION performance terrain ready: {} ordinary chests",
                PERFORMANCE_CHESTS.size());
    }

    private static void validateEntityMode(Minecraft minecraft, BlockPos pos) {
        BlockEntityExt ext = ext(minecraft, pos);
        require(ext.renderMode() == RenderMode.ENTITY, "active mode is not ENTITY at " + pos + ": " + ext.renderMode());
        require(!SophisticatedRenderContext.shouldSuppress(ext), "ENTITY mode suppresses the original shell at " + pos);
    }

    private static void setOpen(Minecraft minecraft, boolean open) {
        chest(minecraft, CHESTS.get(0)).setShouldBeOpen(open);
        shulker(minecraft, SHULKERS.get(0)).setShouldBeOpen(open);
        chest(minecraft, DOUBLE_RIGHT).setShouldBeOpen(open);
    }

    private static void injectFallback(Minecraft minecraft) {
        ChestBlockEntity chest = chest(minecraft, CHESTS.get(0));
        BlockEntityExt ext = (BlockEntityExt) chest;
        Object appearance = ext.specialModelState().committedAppearance();
        ext.specialModelState().fail(appearance, SpecialBakedModelCache.generation(), "live validation injected bake failure");
        SophisticatedChestRuntime.updateAnimationMode(chest);
        OBE.LOGGER.info("OBE LIVE VALIDATION injected fallback for full-BER recovery check");
    }

    private static void dirtyPerformance() {
        for (BlockPos pos : PERFORMANCE_CHESTS) {
            RenderModeManager.setDirty(pos);
        }
    }

    private static void enableAndDirtyPerformance(Minecraft minecraft) {
        for (BlockPos pos : PERFORMANCE_CHESTS) {
            BlockEntity blockEntity = minecraft.level == null ? null : minecraft.level.getBlockEntity(pos);
            if (blockEntity != null) {
                ((BlockEntityExt) blockEntity).isEnabled(true);
                RenderModeManager.setDirty(pos);
            }
        }
    }

    private static void moveCamera(Minecraft minecraft, double x, double y, double z) {
        moveCamera(minecraft, x, y, z, 0.0F, 18.0F);
    }

    private static void moveCamera(Minecraft minecraft, double x, double y, double z, float yaw, float pitch) {
        var server = minecraft.getSingleplayerServer();
        if (server == null) {
            throw new IllegalStateException("integrated server disappeared while moving validation camera");
        }
        cameraYaw = yaw;
        cameraPitch = pitch;
        server.execute(() -> serverPlayer(minecraft).teleportTo(
                serverPlayer(minecraft).serverLevel(), x, y, z, yaw, pitch));
    }

    private static void capture(Minecraft minecraft, String filename) {
        Screenshot.grab(minecraft.gameDirectory, filename, minecraft.getMainRenderTarget(),
                message -> OBE.LOGGER.info("OBE LIVE VALIDATION screenshot {}: {}", filename, message.getString()));
    }

    private static void sampleFps(Minecraft minecraft, List<Integer> samples) {
        int fps = minecraft.getFps();
        if (fps > 0) {
            samples.add(fps);
        }
    }

    private static void beginPerformanceSample() {
        renderWallSamples.clear();
        renderCpuSamples.clear();
        fpsSamples.clear();
        var snapshot = SophisticatedStorageDiagnostics.snapshot();
        metricStart = new MetricStart(snapshot,
                DynamicBlockEntityRenderManager.preExtractionSkipCount(ModBlocks.CHEST_BLOCK_ENTITY_TYPE.get()));
        renderWallStart = 0;
        renderCpuStart = -1;
        samplingPerformance = true;
    }

    private static PerformanceSample finishPerformanceSample() {
        samplingPerformance = false;
        var after = SophisticatedStorageDiagnostics.snapshot();
        var before = metricStart.diagnostics();
        long sodiumAfter = DynamicBlockEntityRenderManager.preExtractionSkipCount(
                ModBlocks.CHEST_BLOCK_ENTITY_TYPE.get());
        PerformanceSample sample = new PerformanceSample(
                after.potentialChestRenderers() - before.potentialChestRenderers(),
                after.wholeChestRenderersSkipped() - before.wholeChestRenderersSkipped(),
                after.wholeChestRenderersExecuted() - before.wholeChestRenderersExecuted(),
                after.completedDynamicRenderers() - before.completedDynamicRenderers(),
                after.suppressedShellCalls() - before.suppressedShellCalls(),
                after.passthroughShellCalls() - before.passthroughShellCalls(),
                sodiumAfter - metricStart.sodiumPreExtractionSkips(),
                after.displayItemExecutions() - before.displayItemExecutions(),
                Distribution.of(renderWallSamples), Distribution.of(renderCpuSamples), average(fpsSamples));
        OBE.LOGGER.info("OBE LIVE VALIDATION performance sample: {}", sample);
        return sample;
    }

    private static void finish(Minecraft minecraft) {
        var diagnostics = SophisticatedStorageDiagnostics.snapshot();
        finalResult = String.format(Locale.ROOT,
                "OBE LIVE VALIDATION PASS: matrixChests=%d, shulkers=%d, barrelsExcluded=%d, "
                        + "performanceChests=%d, crossChunkDouble=x15/16, cache=%s, atlasDiscovered=%d, "
                        + "atlasMissing=%d, fallbacks=%d, missingSprites=%d, toolPreviews=upgrades/tier/lock/release%n"
                        + "wholeBerSkip=%s%n"
                        + "shellOnly=%s%n"
                        + "originalBer=%s%n"
                        + "displayedItems=%s",
                CHESTS.size(), SHULKERS.size(), BARRELS.size(), PERFORMANCE_CHESTS.size(),
                diagnostics.cache(), diagnostics.atlasDiscovered(), diagnostics.atlasMissing(),
                diagnostics.fallbacks(), diagnostics.missingSprites(), wholeSkipSample, shellOnlySample,
                originalBerSample, displayedItemSample);
        submitServerTask(minecraft, () -> restorePlayer(minecraft));
        advance(Phase.RESTORING_PLAYER);
    }

    private static void fail(Minecraft minecraft, Throwable failure) {
        if (phase == Phase.DONE || phase == Phase.RESTORING_PLAYER) {
            return;
        }
        SettingsManager.MOD_TOGGLE.setValue(true);
        SophisticatedDynamicRenderControl.setWholeRendererSkipEnabledForValidation(true);
        samplingPerformance = false;
        finalResult = "OBE LIVE VALIDATION FAIL during " + phase + ": " + failure;
        OBE.LOGGER.error(finalResult, failure);
        if (minecraft.getSingleplayerServer() != null && originalGameType != null) {
            submitServerTask(minecraft, () -> restorePlayer(minecraft));
            advance(Phase.RESTORING_PLAYER);
        } else {
            writeResult(minecraft, finalResult);
            minecraft.stop();
            phase = Phase.DONE;
        }
    }

    private static void restorePlayer(Minecraft minecraft) {
        ServerPlayer player = serverPlayer(minecraft);
        player.setGameMode(originalGameType);
        player.setItemInHand(InteractionHand.MAIN_HAND, originalMainHand.copy());
        player.setItemInHand(InteractionHand.OFF_HAND, originalOffHand.copy());
        player.teleportTo(player.serverLevel(), originalX, originalY, originalZ, originalYaw, originalPitch);
    }

    private static void setStorageTool(Minecraft minecraft, StorageToolItem.Mode mode) {
        ServerPlayer player = serverPlayer(minecraft);
        ItemStack tool = new ItemStack(ModItems.STORAGE_TOOL.get());
        tool.set(ModDataComponents.TOOL_MODE.get(), mode);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
    }

    private static void clearValidationHands(Minecraft minecraft) {
        ServerPlayer player = serverPlayer(minecraft);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
    }

    private static long committedGeneration(Minecraft minecraft, BlockPos pos) {
        return ext(minecraft, pos).specialModelState().committedGeneration();
    }

    private static void writeResult(Minecraft minecraft, String result) {
        try {
            Files.writeString(minecraft.gameDirectory.toPath().resolve("obe-live-validation.txt"), result + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException error) {
            OBE.LOGGER.error("Could not write live validation result", error);
        }
    }

    private static void submitServerTask(Minecraft minecraft, Runnable task) {
        var server = minecraft.getSingleplayerServer();
        if (server == null) {
            throw new IllegalStateException("integrated server is unavailable");
        }
        serverTaskDone.set(false);
        serverTaskFailure = null;
        server.execute(() -> {
            try {
                task.run();
            } catch (Throwable failure) {
                serverTaskFailure = failure;
            } finally {
                serverTaskDone.set(true);
            }
        });
    }

    private static void checkServerTask() {
        if (serverTaskFailure != null) {
            throw new IllegalStateException("validation server task failed", serverTaskFailure);
        }
    }

    private static ServerPlayer serverPlayer(Minecraft minecraft) {
        var server = minecraft.getSingleplayerServer();
        if (server == null || minecraft.player == null) {
            throw new IllegalStateException("validation player is unavailable");
        }
        ServerPlayer player = server.getPlayerList().getPlayer(minecraft.player.getUUID());
        if (player == null) {
            throw new IllegalStateException("integrated-server validation player is unavailable");
        }
        return player;
    }

    private static ChestBlockEntity chest(Minecraft minecraft, BlockPos pos) {
        BlockEntity blockEntity = requireBlockEntity(minecraft, pos);
        if (!(blockEntity instanceof ChestBlockEntity chest)) {
            throw new IllegalStateException("expected Sophisticated chest at " + pos + ", got " + blockEntity.getClass());
        }
        return chest;
    }

    private static ShulkerBoxBlockEntity shulker(Minecraft minecraft, BlockPos pos) {
        BlockEntity blockEntity = requireBlockEntity(minecraft, pos);
        if (!(blockEntity instanceof ShulkerBoxBlockEntity shulker)) {
            throw new IllegalStateException("expected Sophisticated shulker at " + pos + ", got " + blockEntity.getClass());
        }
        return shulker;
    }

    private static BlockEntityExt ext(Minecraft minecraft, BlockPos pos) {
        return (BlockEntityExt) requireBlockEntity(minecraft, pos);
    }

    private static BlockEntity requireBlockEntity(Minecraft minecraft, BlockPos pos) {
        if (minecraft.level == null) {
            throw new IllegalStateException("client level is unavailable");
        }
        BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        if (blockEntity == null) {
            throw new IllegalStateException("missing client block entity at " + pos);
        }
        return blockEntity;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static double average(List<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    private static boolean enableThreadCpuTime() {
        try {
            if (!THREAD_CPU.isCurrentThreadCpuTimeSupported()) {
                return false;
            }
            if (!THREAD_CPU.isThreadCpuTimeEnabled()) {
                THREAD_CPU.setThreadCpuTimeEnabled(true);
            }
            return THREAD_CPU.isThreadCpuTimeEnabled();
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static List<BlockPos> grid(int firstZ, int secondZ) {
        ArrayList<BlockPos> result = new ArrayList<>(12);
        for (int z : new int[] {firstZ, secondZ}) {
            for (int x : new int[] {-5, -3, -1, 1, 3, 5}) {
                result.add(new BlockPos(x, Y, z));
            }
        }
        return List.copyOf(result);
    }

    private static List<BlockPos> performanceGrid(int width, int depth) {
        ArrayList<BlockPos> result = new ArrayList<>(width * depth);
        int firstX = -(width - 1);
        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                result.add(new BlockPos(firstX + x * 2, Y, 300 + z * 2));
            }
        }
        return List.copyOf(result);
    }

    private static void advance(Phase next) {
        phase = next;
        phaseTicks = 0;
    }

    private enum Phase {
        WAITING_FOR_WORLD,
        SETTING_UP_SCENE,
        WAITING_FOR_CLOSED_MATRIX,
        PREPARING_PERFORMANCE,
        SAMPLING_WHOLE_SKIP,
        PREPARING_SHELL_ONLY,
        SAMPLING_SHELL_ONLY,
        PREPARING_ORIGINAL_BER,
        SAMPLING_ORIGINAL_BER,
        RESTORING_PERFORMANCE_TERRAIN,
        CONFIGURING_DISPLAYED_ITEMS,
        SAMPLING_DISPLAYED_ITEMS,
        CLEANING_PERFORMANCE_SCENE,
        PREPARING_TOOL_PREVIEWS,
        WAITING_FOR_UPGRADE_PREVIEW,
        WAITING_FOR_TIER_PREVIEW,
        WAITING_FOR_LOCK_PREVIEW,
        WAITING_FOR_TOOL_RELEASE,
        WAITING_FOR_DOUBLE_CHEST,
        WAITING_FOR_INVALIDATION,
        RETURNING_TO_MATRIX,
        WAITING_FOR_OPEN_TRANSITION,
        WAITING_FOR_CLOSE_TRANSITION,
        WAITING_FOR_FALLBACK,
        WAITING_FOR_FALLBACK_RECOVERY,
        RELOADING_RESOURCES,
        WAITING_FOR_RELOAD_REBUILD,
        RESTORING_PLAYER,
        DONE
    }

    private record MetricStart(
            SophisticatedStorageDiagnostics.Snapshot diagnostics,
            long sodiumPreExtractionSkips
    ) {
    }

    private record PerformanceSample(
            long potentialInvocations,
            long wholeSkipped,
            long wholeExecuted,
            long completedOriginal,
            long suppressedShellCalls,
            long passthroughShellCalls,
            long sodiumPreExtractionSkips,
            long displayItemExecutions,
            Distribution renderWall,
            Distribution renderThreadCpu,
            double averageFps
    ) {
    }

    private record Distribution(int samples, double averageMs, double p50Ms, double p95Ms, double p99Ms) {
        private static Distribution of(List<Long> nanoseconds) {
            if (nanoseconds.isEmpty()) {
                return new Distribution(0, 0, 0, 0, 0);
            }
            List<Long> sorted = nanoseconds.stream().sorted().toList();
            double average = sorted.stream().mapToLong(Long::longValue).average().orElse(0) / 1_000_000D;
            return new Distribution(sorted.size(), average,
                    percentileMs(sorted, 0.50), percentileMs(sorted, 0.95), percentileMs(sorted, 0.99));
        }

        private static double percentileMs(List<Long> sorted, double percentile) {
            int index = Math.min(sorted.size() - 1, (int) Math.ceil(sorted.size() * percentile) - 1);
            return sorted.get(Math.max(0, index)) / 1_000_000D;
        }
    }
}

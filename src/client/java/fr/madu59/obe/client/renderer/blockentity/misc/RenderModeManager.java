package fr.madu59.obe.client.renderer.blockentity.misc;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RenderModeManager {

    public static <T extends BlockEntity> boolean isTerrain(T entity){
        return isTerrain((BlockEntityExt)entity);
    }

    public static boolean isTerrain(BlockEntityExt ext){
        if (ext == null) {
            return false;
        }
        return ext.renderMode() == RenderMode.TERRAIN;
    }

    public static boolean hasBlockEntity(BlockState state){
        return hasBlockEntity(state.getBlock());
    }

    public static boolean hasBlockEntity(Block block){
        return block instanceof BaseEntityBlock;
    }

    public static void setRenderMode(BlockEntity be, RenderMode mode, BlockPos pos){
        setRenderMode((BlockEntityExt)be, mode, pos);
    }

    public static void setRenderMode(BlockEntityExt ext, RenderMode mode, BlockPos pos){
        if(ext.renderMode() != mode) {
            setDirty(pos);
            ext.renderMode(mode);
            ext.renderModeDelayed(mode);
        }
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(T be){
        return shouldRenderEntity(((BlockEntityExt)be));
    }

    public static boolean shouldRenderEntity(BlockEntityExt ext){
        return ext.renderMode() == RenderMode.ENTITY || ext.renderModeDelayed() == RenderMode.ENTITY || ext.renderBoth();
    }

    public static <T extends BlockEntity> void setRenderModeDelayed(T be, RenderMode mode, BlockPos pos){
        setRenderModeDelayed((BlockEntityExt)be, mode, pos);
    }

    public static void setRenderModeDelayed(BlockEntityExt ext, RenderMode mode, BlockPos pos){
        if(ext.renderModeDelayed() != mode) {
            ext.renderModeDelayed(mode);
            setDirty(pos);
        }
    }

    private static void setDirty(BlockPos pos){
        if (!Minecraft.getInstance().isSameThread()) {
            Minecraft.getInstance().execute(() -> setDirty(pos));
            return;
        }
        BlockState state = Minecraft.getInstance().level.getBlockState(pos);
        Minecraft.getInstance().levelRenderer.blockChanged(Minecraft.getInstance().level, pos, state, state, 8);
    }

    public static void updateBlockEntity(BlockEntityExt ext, BlockEntity be){
        if (be.getType() == BlockEntityType.SIGN || be.getType() == BlockEntityType.HANGING_SIGN) {
            ext.isEnabled(SettingsManager.OPTIMISED_SIGNS.getValue());
        }
        else if (be.getType() == BlockEntityType.CHEST || be.getType() == BlockEntityType.TRAPPED_CHEST || be.getType() == BlockEntityType.ENDER_CHEST) {
            ext.isEnabled(SettingsManager.OPTIMISED_CHESTS.getValue());
        }
        else if (be.getType() == BlockEntityType.BANNER) {
            ext.isEnabled(SettingsManager.OPTIMISED_BANNERS.getValue());
        }
        else if (be.getType() == BlockEntityType.SHULKER_BOX) {
            ext.isEnabled(SettingsManager.OPTIMISED_SHULKER_BOXES.getValue());
        }
        else if (be.getType() == BlockEntityType.SKULL) {
            ext.isEnabled(SettingsManager.OPTIMISED_SKULLS.getValue());
        }
        else if (be.getType() == BlockEntityType.BED) {
            ext.isEnabled(SettingsManager.OPTIMISED_BEDS.getValue());
        }
        else if (be.getType() == BlockEntityType.BELL) {
            ext.isEnabled(SettingsManager.OPTIMISED_BELLS.getValue());
        }
        else if (be.getType() == BlockEntityType.DECORATED_POT) {
            ext.isEnabled(SettingsManager.OPTIMISED_DECORATED_POTS.getValue());
        }
        if(ext.isEnabled()){
            if(ext.isTimerFinished()){
            ext.renderMode(RenderMode.TERRAIN);
            }
            if(ext.renderMode() != ext.renderModeDelayed()){
                ext.renderMode(ext.renderModeDelayed());
            }
        }
    }

    public static enum RenderMode {
        TERRAIN,
        ENTITY
    }
}

package fr.madu59.obe.client.renderer.blockentity.misc;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

public class RenderModeManager {

    public static boolean isTerrain(BlockEntityRenderState state){
        return isTerrain(((BlockEntityRenderStateExt)state).blockEntity());
    }

    public static <T extends BlockEntity> boolean isTerrain(T entity){
        return isTerrain((BlockEntityExt)entity);
    }

    public static boolean isTerrain(BlockEntityExt ext){
        if (ext == null) {
            return false;
        }
        return ext.renderMode() == RenderMode.TERRAIN;
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

    public static boolean shouldRenderEntity(BlockEntityRenderState state){
        return shouldRenderEntity(((BlockEntityRenderStateExt)state).blockEntity());
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(boolean setting, BlockEntityRenderState state){
        return shouldRenderEntity(setting, ((BlockEntityRenderStateExt)state).blockEntity());
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(boolean setting, T be){
        return shouldRenderEntity(setting, (BlockEntityExt) be, be);
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(boolean setting, BlockEntityExt ext, BlockEntity be){
        return ext == null || !be.hasLevel() || !ext.isSupportedBlockEntity() || setting;
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(T be){
        return shouldRenderEntity((BlockEntityExt) be, be);
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(BlockEntityExt ext, T be){
        return ext == null || !be.hasLevel() || !ext.isSupportedBlockEntity() || ext.renderMode() == RenderMode.ENTITY || ext.renderModeDelayed() == RenderMode.ENTITY || ext.renderBoth();
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
        Minecraft.getInstance().levelExtractor.blockChanged(pos, 8);
    }

    public static void updateBlockEntity(BlockEntityExt ext, BlockEntity be){
        if (be.getType() == BlockEntityTypes.SIGN || be.getType() == BlockEntityTypes.HANGING_SIGN) {
            ext.isEnabled(SettingsManager.OPTIMISED_SIGNS.getValue());
        }
        else if (be.getType() == BlockEntityTypes.CHEST || be.getType() == BlockEntityTypes.TRAPPED_CHEST || be.getType() == BlockEntityTypes.ENDER_CHEST) {
            ext.isEnabled(SettingsManager.OPTIMISED_CHESTS.getValue());
        }
        else if (be.getType() == BlockEntityTypes.BANNER) {
            ext.isEnabled(SettingsManager.OPTIMISED_BANNERS.getValue());
        }
        else if (be.getType() == BlockEntityTypes.SHULKER_BOX) {
            ext.isEnabled(SettingsManager.OPTIMISED_SHULKER_BOXES.getValue());
        }
        else if (be.getType() == BlockEntityTypes.SKULL) {
            ext.isEnabled(SettingsManager.OPTIMISED_SKULLS.getValue());
        }
        else if (be.getType() == BlockEntityTypes.BELL) {
            ext.isEnabled(SettingsManager.OPTIMISED_BELLS.getValue());
        }
        else if (be.getType() == BlockEntityTypes.DECORATED_POT) {
            ext.isEnabled(SettingsManager.OPTIMISED_DECORATED_POTS.getValue());
        }
        else if (be.getType() == BlockEntityTypes.COPPER_GOLEM_STATUE) {
            ext.isEnabled(SettingsManager.OPTIMISED_COPPER_GOLEMS.getValue());
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

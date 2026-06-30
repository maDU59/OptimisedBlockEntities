package fr.madu59.obe.client.renderer.blockentity.misc;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.config.Option;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RenderModeManager {

    public static  <T extends BlockEntity> boolean isSupportBlockEntity(T entity){
        return isSupportBlockEntity((BlockEntityExt)entity);
    }

    public static boolean isSupportBlockEntity(BlockEntityExt ext){
        return ext != null && ext.isSupportedBlockEntity();
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
        return ext == null || !be.hasLevel() || ext.forceEntity() || !ext.isSupportedBlockEntity() || ext.renderMode() == RenderMode.ENTITY || ext.renderModeDelayed() == RenderMode.ENTITY || ext.renderBoth() || ext.renderMode() == RenderMode.INTERMEDIATE || ModCompat.shouldRenderEntity(be);
    }

    public static <T extends BlockEntity> boolean shouldRenderEntityFast(BlockEntityExt ext){
        return ext.forceEntity() || !ext.isSupportedBlockEntity() || ext.renderMode() == RenderMode.ENTITY || ext.renderModeDelayed() == RenderMode.ENTITY || ext.renderBoth() || ext.renderMode() == RenderMode.INTERMEDIATE;
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

    public static void updateOnRender(BlockEntityRenderState state){
        updateOnRender((BlockEntityExt)((BlockEntityRenderStateExt)state).blockEntity());
    }

    public static void updateOnRender(BlockEntityExt ext){
        if(ext.renderMode() == RenderMode.INTERMEDIATE){
            if(ext.renderModSwitchTimer() <= 0) ext.renderMode(RenderMode.TERRAIN);
            ext.tickRenderModeSwitchTimer();
        }
    }

    public static void updateBlockEntityOnChunkRemesh(BlockEntityExt ext, BlockEntity be){
        if(!ext.isSupportedBlockEntity()) return;
        else if(!SettingsManager.MOD_TOGGLE.getValue()) ext.isEnabled(false);
        else{
            String group = Registry.getGroup(be.getType());
            if(group == null) return;
            Option<Boolean> option = SettingsManager.GROUP_TOGGLE_SETTINGS.get(group);
            if(option != null) ext.isEnabled(option.getValue());
            if(ext.isEnabled()){
                if(ext.isTimerFinished()){
                    ext.renderMode(RenderMode.TERRAIN);
                }
                if(ext.renderMode() != ext.renderModeDelayed()){
                    if(ext.renderModeDelayed() == RenderMode.TERRAIN) ext.renderMode(RenderMode.INTERMEDIATE);
                    if(ext.renderModeDelayed() == RenderMode.ENTITY) ext.renderMode(ext.renderModeDelayed());
                }
            }
        }
    }

    public static enum RenderMode {
        TERRAIN,
        ENTITY,
        INTERMEDIATE
    }
}

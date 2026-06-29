package fr.madu59.obe.client.renderer.blockentity.misc;

import fr.madu59.obe.client.compat.ModCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RenderModeManager {

    public static  <T extends BlockEntity> boolean isSupportBlockEntity(T entity){
        return isSupportBlockEntity((BlockEntityExt)entity);
    }

    public static boolean isSupportBlockEntity(BlockEntityExt ext){
        return ext != null && ext.isSupportedBlockEntity();
    }

    public static boolean hasBlockEntity(BlockState state){
        return hasBlockEntity(state.getBlock());
    }

    public static boolean hasBlockEntity(Block block){
        return block instanceof EntityBlock;
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

    public static void setDirty(BlockPos pos){
        if (!Minecraft.getInstance().isSameThread()) {
            Minecraft.getInstance().execute(() -> setDirty(pos));
            return;
        }
        BlockState state = Minecraft.getInstance().level.getBlockState(pos);
        Minecraft.getInstance().levelRenderer.blockChanged(Minecraft.getInstance().level, pos, state, state, 8);
    }

    public static void updateOnRender(BlockEntity be){
        updateOnRender((BlockEntityExt)be);
    }

    public static void updateOnRender(BlockEntityExt ext){
        if(ext.renderMode() == RenderMode.INTERMEDIATE) ext.renderMode(RenderMode.TERRAIN);
    }

    public static void updateBlockEntityOnChunkRemesh(BlockEntityExt ext, BlockEntity be){
        if(!SettingsManager.MOD_TOGGLE.getValue()) ext.isEnabled(false);
        if (Registry.isSupported("sign", be.getType())) {
            ext.isEnabled(SettingsManager.OPTIMISED_SIGNS.getValue());
        }
        else if (Registry.isSupported("hanging_sign", be.getType())) {
            ext.isEnabled(SettingsManager.OPTIMISED_SIGNS.getValue());
        }
        else if (Registry.isSupported("bed", be.getType())) {
            ext.isEnabled(SettingsManager.OPTIMISED_BEDS.getValue());
        }
        else if (Registry.isSupported("chest", be.getType())) {
            ext.isEnabled(SettingsManager.OPTIMISED_CHESTS.getValue());
        }
        else if (Registry.isSupported("banner", be.getType())) {
            ext.isEnabled(SettingsManager.OPTIMISED_BANNERS.getValue());
        }
        else if (Registry.isSupported("shulker_box", be.getType())) {
            ext.isEnabled(SettingsManager.OPTIMISED_SHULKER_BOXES.getValue());
        }
        else if (Registry.isSupported("skull", be.getType())) {
            ext.isEnabled(SettingsManager.OPTIMISED_SKULLS.getValue());
        }
        else if (Registry.isSupported("bell", be.getType())) {
            ext.isEnabled(SettingsManager.OPTIMISED_BELLS.getValue());
        }
        else if (Registry.isSupported("decorated_pot", be.getType())) {
            ext.isEnabled(SettingsManager.OPTIMISED_DECORATED_POTS.getValue());
        }
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

    public static enum RenderMode {
        TERRAIN,
        ENTITY,
        INTERMEDIATE
    }
}

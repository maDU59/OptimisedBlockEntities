package fr.madu59.obe.client.renderer.misc;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.chunk.ChunkTaskHolder;
import fr.madu59.obe.client.config.Option;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.entity.ext.EntityExt;
import fr.madu59.obe.client.renderer.entity.ext.EntityRenderStateExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RenderModeManager {

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

    public static boolean shouldRenderEntity(EntityRenderState state){
        return shouldRenderEntity(((EntityRenderStateExt)state).entity());
    }

    public static <T extends Entity> boolean shouldRenderEntity(T entity){
        return shouldRenderEntity((EntityExt)entity);
    }

    public static <T extends Entity> boolean shouldRenderEntity(EntityExt ext){
        return ext.renderMode() == RenderMode.ENTITY;
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
        return ext == null || !be.hasLevel() || !ext.isSupported() || setting || !SettingsManager.MOD_TOGGLE.getValue();
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(T be){
        return shouldRenderEntity((BlockEntityExt) be, be);
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(BlockEntityExt ext, T be){
        return ext == null || !be.hasLevel() || ext.forceEntity() || !ext.isSupported() || ext.renderMode() == RenderMode.ENTITY || ext.renderModeDelayed() == RenderMode.ENTITY || ext.renderBoth();
    }

    public static <T extends BlockEntity> boolean shouldRenderEntityFast(BlockEntityExt ext){
        return ext.forceEntity() || !ext.isSupported() || ext.renderMode() == RenderMode.ENTITY || ext.renderModeDelayed() == RenderMode.ENTITY || ext.renderBoth();
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
        Minecraft client = Minecraft.getInstance();
        if(client.level == null) return;
        if (!client.isSameThread()) {
            client.execute(() -> setDirty(pos));
            return;
        }
        BlockState state = client.level.getBlockState(pos);
        client.levelRenderer.blockChanged(Minecraft.getInstance().level, pos, state, state, 8);
    }

    public static void updateBlockEntityOnChunkRemesh(BlockEntityExt ext, SectionPos pos){
        if(!ext.isSupported()) return;
        else if(!SettingsManager.MOD_TOGGLE.getValue()) ext.isEnabled(false);
        else{
            String group = Registry.getGroup(((BlockEntity)ext).getType());
            if(group == null) return;
            Option<Boolean> option = SettingsManager.GROUP_TOGGLE_SETTINGS.get(group);
            if(option != null) ext.isEnabled(option.getValue());
            if(ext.isEnabled()){
                if(ext.isTimerFinished()){
                    ChunkTaskHolder.addTask(pos, () -> ext.renderMode(RenderMode.TERRAIN));
                }
                if(ext.renderMode() != ext.renderModeDelayed()){
                    ChunkTaskHolder.addTask(pos, () -> ext.renderMode(ext.renderModeDelayed()));
                }
            }
        }
    }

    public static enum RenderMode {
        TERRAIN,
        ENTITY
    }
}

package fr.madu59.obe.client.renderer.misc;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.chunk.ChunkTaskHolder;
import fr.madu59.obe.client.config.Option;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
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
        if(ext.obe$renderMode() != mode) {
            setDirty(pos);
            ext.obe$renderMode(mode);
            ext.obe$renderModeDelayed(mode);
        }
    }

    public static boolean shouldRenderEntity(EntityRenderState state){
        return shouldRenderEntity(((EntityRenderStateExt)state).entity());
    }

    public static <T extends Entity> boolean shouldRenderEntity(T entity){
        return shouldRenderEntity((EntityExt)entity);
    }

    public static <T extends Entity> boolean shouldRenderEntity(EntityExt ext){
        return ext.obe$renderMode() == RenderMode.ENTITY;
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
        return ext == null || !be.hasLevel() || !ext.obe$isSupported() || setting || !SettingsManager.MOD_TOGGLE.getValue();
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(T be){
        return shouldRenderEntity((BlockEntityExt) be, be);
    }

    public static <T extends BlockEntity> boolean shouldRenderEntity(BlockEntityExt ext, T be){
        return ext == null || !be.hasLevel() || ext.obe$forceEntity() || !ext.obe$isSupported() || ext.obe$renderMode() == RenderMode.ENTITY || ext.obe$renderModeDelayed() == RenderMode.ENTITY || ext.obe$renderBoth();
    }

    public static <T extends BlockEntity> boolean shouldRenderEntityFast(BlockEntityExt ext){
        return ext.obe$forceEntity() || !ext.obe$isSupported() || ext.obe$renderMode() == RenderMode.ENTITY || ext.obe$renderModeDelayed() == RenderMode.ENTITY || ext.obe$renderBoth();
    }

    public static <T extends BlockEntity> void setRenderModeDelayed(T be, RenderMode mode, BlockPos pos){
        setRenderModeDelayed((BlockEntityExt)be, mode, pos);
    }

    public static void setRenderModeDelayed(BlockEntityExt ext, RenderMode mode, BlockPos pos){
        if(ext.obe$renderModeDelayed() != mode && (mode != RenderMode.TERRAIN || canBeTerrain(ext))){
            ext.obe$renderModeDelayed(mode);
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
        client.levelExtractor.blockChanged(pos, 8);
    }

    public static void updateBlockEntityOnChunkRemesh(BlockEntityExt ext, SectionPos pos){
        if(!ext.obe$isSupported()) return;
        else if(!SettingsManager.MOD_TOGGLE.getValue()) ext.obe$isEnabled(false);
        else{
            String group = Registry.getGroup(((BlockEntity)ext).getType());
            if(group == null) return;
            Option<Boolean> option = SettingsManager.GROUP_TOGGLE_SETTINGS.get(group);
            if(option != null) ext.obe$isEnabled(option.getValue());
            if(ext.obe$isEnabled()){
                if(ext.obe$isTimerFinished()){
                    ChunkTaskHolder.addTask(pos, () -> ext.obe$renderMode(RenderMode.TERRAIN));
                }
                if(ext.obe$renderMode() != ext.obe$renderModeDelayed()){
                    ChunkTaskHolder.addTask(pos, () -> ext.obe$renderMode(ext.obe$renderModeDelayed()));
                }
            }
        }
    }

    public static boolean canBeTerrain(BlockEntityExt ext){

        if(Minecraft.getInstance().level == null) return false;
        
        BlockEntity be = (BlockEntity)ext;
        BlockState state = be.getBlockState();
        String group = Registry.getGroup(state);

        SpecialModelProvider customModelProvider = SpecialModelGetter.getSpecialModelProvider(state, group);

        if(ext.obe$hasSpecialRenderer() && customModelProvider != null){
            if(customModelProvider.getModelLayerLocationProvider().apply(state, be) == null) return false;
            if(customModelProvider.getMaterialProvider().apply(state, be) == null) return false;
        }
        else{
            if(ModelLayerLocationGetter.getModelLayerLocation(state, group) == null) return false;
            if(MaterialGetter.getMaterial(state, group) == null) return false;
        }
        return true;
    }

    public static enum RenderMode {
        TERRAIN,
        ENTITY
    }
}

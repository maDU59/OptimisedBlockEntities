package fr.madu59.obe.client.mixin.blockentity.ext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityExt {
    @Unique private RenderMode renderMode = RenderMode.ENTITY;
    @Unique private RenderMode renderModeDelayed = RenderMode.TERRAIN;
    @Unique private boolean isSupported = false;
    @Unique private boolean hasSpecialRenderer = false;
    @Unique private long timerStart = 0;
    @Unique private int timerDuration = 0;
    @Unique private boolean isEnabled = true;
    @Unique private boolean renderBoth = false;
    @Unique private boolean shouldSkipRendering = false;
    @Unique private boolean forceEntity = false;
    @Unique private byte specialKind = 0;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void obe$onInit(CallbackInfo ci) {
        BlockEntity be = (BlockEntity) (Object) this;
        if(be instanceof SignBlockEntity){
            specialKind = 1;
        }
        else if(be instanceof BeaconBlockEntity){
            specialKind = 2;
        }
        else if(be instanceof CampfireBlockEntity){
            specialKind = 3;
        }
//        else if(be instanceof ShelfBlockEntity){
//            specialKind = 4;
//        }
        else if(be instanceof SpawnerBlockEntity){
            specialKind = 5;
        }
        else if(be instanceof VaultBlockEntity){
            specialKind = 6;
        }
        else if(be instanceof BrushableBlockEntity){
            specialKind = 7;
        }
    }

    @Override public boolean obe$isSupported() { return isSupported; }
    @Override public void obe$isSupported(boolean bl) {
        if(bl && bl != isSupported && renderModeDelayed == RenderMode.TERRAIN){
            if(!RenderModeManager.canBeTerrain(this)) {
                renderModeDelayed = RenderMode.ENTITY;
            }
        }
        this.isSupported = bl; 
    }

    @Override public RenderMode obe$renderMode() { return renderMode; }
    @Override public void obe$renderMode(RenderMode mode) {
        if(isEnabled) renderMode = mode;
        renderModeDelayed = mode;
    }

    @Override public RenderMode obe$renderModeDelayed() { return renderModeDelayed; }
    @Override public void obe$renderModeDelayed(RenderMode mode) { renderModeDelayed = mode; }

    @Override public boolean obe$hasSpecialRenderer() { return hasSpecialRenderer; }
    @Override public void obe$hasSpecialRenderer(boolean bl) { hasSpecialRenderer = bl; }

    @Override public boolean obe$isEnabled() { return isEnabled; }
    @Override public void obe$isEnabled(boolean bl) {
        isEnabled = bl;
        if(!isEnabled) renderMode = RenderMode.ENTITY;
    }

    @Override public boolean obe$renderBoth() { return renderBoth; }
    @Override public void obe$renderBoth(boolean bl) { renderBoth = bl;}

    @Override public boolean obe$isTimerFinished(){
        if(timerStart == 0) return false;
        else return Minecraft.getInstance().level != null && Minecraft.getInstance().level.getGameTime() - timerStart > timerDuration;
    }

    @Override public void obe$setTimer(long start, int duration){
        timerStart = start;
        timerDuration = duration;
    }

    @Override public boolean obe$shouldSkipRendering() { return shouldSkipRendering; }
    @Override public void obe$shouldSkipRendering(boolean bl) { shouldSkipRendering = bl; }
    @Override public void obe$shouldSkipRenderingAndUpdate(boolean bl) {
        if(bl != shouldSkipRendering) {
            shouldSkipRendering = bl;
            BlockPos pos = ((BlockEntity)(Object)this).getBlockPos();
            if(Minecraft.getInstance().level != null && Minecraft.getInstance().level.getChunkSource() != null) RenderModeManager.setDirty(pos);
        }
    }

    @Override public byte obe$specialKind() { return specialKind; }
    @Override public void obe$specialKind(byte b) { specialKind = b; }

    @Override public boolean obe$forceEntity() { return forceEntity; }
    @Override public void obe$forceEntity(boolean bl) { forceEntity = bl; }
}
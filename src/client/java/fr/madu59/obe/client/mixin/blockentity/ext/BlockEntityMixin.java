package fr.madu59.obe.client.mixin.blockentity.ext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements BlockEntityExt {
    @Unique private RenderMode renderMode = RenderMode.TERRAIN;
    @Unique private RenderMode renderModeDelayed = RenderMode.TERRAIN;
    @Unique private boolean isSupportedBlockEntity = false;
    @Unique private boolean hasSpecialRenderer = false;
    @Unique private long timerStart = 0;
    @Unique private int timerDuration = 0;
    @Unique private boolean isEnabled = true;
    @Unique private boolean renderBoth = false;
    @Unique private boolean shouldSkipBeRendering = false;
    @Unique private boolean forceEntity = false;
    @Unique private int countdown = 0;

    @Override public boolean isSupportedBlockEntity() { return isSupportedBlockEntity; }
    @Override public void isSupportedBlockEntity(boolean bl) {this.isSupportedBlockEntity = bl; }

    @Override public RenderMode renderMode() { return renderMode; }
    @Override public void renderMode(RenderMode mode) {
        if(isEnabled) renderMode = mode;
        if(mode == RenderMode.INTERMEDIATE) countdown = 3;
        renderModeDelayed = mode;
    }

    @Override public RenderMode renderModeDelayed() { return renderModeDelayed; }
    @Override public void renderModeDelayed(RenderMode mode) { renderModeDelayed = mode; }

    @Override public boolean hasSpecialRenderer() { return hasSpecialRenderer; }
    @Override public void hasSpecialRenderer(boolean bl) { hasSpecialRenderer = bl; }

    @Override public boolean isEnabled() { return isEnabled; }
    @Override public void isEnabled(boolean bl) {
        isEnabled = bl;
        if(!isEnabled) renderMode = RenderMode.ENTITY;
    }

    @Override public boolean renderBoth() { return renderBoth; }
    @Override public void renderBoth(boolean bl) { renderBoth = bl;}

    @Override public boolean isTimerFinished(){
        if(timerStart == 0) return false;
        else return Minecraft.getInstance().level.getGameTime() - timerStart > timerDuration;
    }

    @Override public void setTimer(long start, int duration){
        timerStart = start;
        timerDuration = duration;
    }

    @Override public boolean shouldSkipBeRendering() { return shouldSkipBeRendering; }
    @Override public void shouldSkipBeRendering(boolean bl) { shouldSkipBeRendering = bl; }

    @Override public boolean forceEntity() { return forceEntity; }
    @Override public void forceEntity(boolean bl) { forceEntity = bl; }

    @Override public int renderModSwitchTimer() { return countdown; }
    @Override public void tickRenderModeSwitchTimer() { countdown -= 1; }
}
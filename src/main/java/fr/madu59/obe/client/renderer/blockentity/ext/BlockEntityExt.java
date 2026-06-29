package fr.madu59.obe.client.renderer.blockentity.ext;

import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;

public interface BlockEntityExt {
    boolean isSupportedBlockEntity();
    void isSupportedBlockEntity(boolean bl);

    RenderMode renderMode();
    void renderMode(RenderMode mode);

    RenderMode renderModeDelayed();
    void renderModeDelayed(RenderMode mode);

    boolean hasSpecialRenderer();
    void hasSpecialRenderer(boolean bl);

    boolean isTimerFinished();
    void setTimer(long start, int duration);

    boolean isEnabled();
    void isEnabled(boolean bl);
    
    boolean renderBoth();
    void renderBoth(boolean bl);

    boolean shouldSkipBeRendering();
    void shouldSkipBeRendering(boolean bl);

    boolean forceEntity();
    void forceEntity(boolean bl);
}

package fr.madu59.obe.client.renderer.entity.ext;

import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;

public interface EntityExt {
    boolean isSupported();
    void isSupported(boolean bl);

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

    boolean shouldSkipRendering();
    void shouldSkipRendering(boolean bl);
    void shouldSkipRenderingAndUpdate(boolean bl);

    boolean forceEntity();
    void forceEntity(boolean bl);
}
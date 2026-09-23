package fr.madu59.obe.client.renderer.entity.ext;

import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;

public interface EntityExt {
    boolean obe$isSupported();
    void obe$isSupported(boolean bl);

    RenderMode obe$renderMode();
    void obe$renderMode(RenderMode mode);

    RenderMode obe$renderModeDelayed();
    void obe$renderModeDelayed(RenderMode mode);

    boolean obe$hasSpecialRenderer();
    void obe$hasSpecialRenderer(boolean bl);

    boolean obe$isTimerFinished();
    void obe$setTimer(long start, int duration);

    boolean obe$isEnabled();
    void obe$isEnabled(boolean bl);

    boolean obe$shouldSkipRendering();
    void obe$shouldSkipRendering(boolean bl);
    void obe$shouldSkipRenderingAndUpdate(boolean bl);

    boolean obe$forceEntity();
    void obe$forceEntity(boolean bl);
}

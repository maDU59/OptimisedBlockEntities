package fr.madu59.obe.client.renderer.blockentity.sign.ext;

import net.minecraft.util.FormattedCharSequence;

public interface SignRenderStateExt {
    // Cached formatted lines
    FormattedCharSequence[] getCachedLines(boolean front);
    void setCachedLines(FormattedCharSequence[] frontLines, FormattedCharSequence[] backLines);

    // Cached line width
    float[] getLineWidths(boolean front);
    void setLineWidths(float[] frontWidths, float[] backWidths);
}

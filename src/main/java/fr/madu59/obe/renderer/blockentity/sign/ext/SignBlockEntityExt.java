package fr.madu59.obe.renderer.blockentity.sign.ext;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.entity.SignText;

public interface SignBlockEntityExt {
    // Cached formatted lines
    FormattedCharSequence[] getCachedLines(boolean front);
    void setCachedLines(boolean front, FormattedCharSequence[] lines);

    // Cached line width
    float[] getLineWidths(boolean front);
    void setLineWidths(boolean front, float[] widths);

    SignText getLastText(boolean front);
    void setLastText(boolean front, SignText text);
    boolean getLastFiltering();
    void setLastFiltering(boolean v);
}

package fr.madu59.obe.client.mixin.blockentity.sign;

import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fr.madu59.obe.client.renderer.blockentity.sign.ext.SignRenderStateExt;

@Mixin(SignRenderState.class)
public class SignRenderStateMixin implements SignRenderStateExt {

    @Unique private FormattedCharSequence[] frontLines = null;
    @Unique private FormattedCharSequence[] backLines = null;
    @Unique private float[] frontWidths = null;
    @Unique private float[] backWidths = null;

    @Override
    public FormattedCharSequence[] getCachedLines(boolean front) {
        return front ? frontLines : backLines;
    }

    @Override
    public void setCachedLines(FormattedCharSequence[] frontLines, FormattedCharSequence[] backLines) {
        this.frontLines = frontLines;
        this.backLines = backLines;
    }

    @Override
    public float[] getLineWidths(boolean front) {
        return front ? frontWidths : backWidths;
    }

    @Override
    public void setLineWidths(float[] frontWidths, float[] backWidths) {
        this.frontWidths = frontWidths;
        this.backWidths = backWidths;
    }
}

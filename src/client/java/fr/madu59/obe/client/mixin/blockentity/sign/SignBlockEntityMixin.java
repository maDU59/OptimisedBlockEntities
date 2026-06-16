package fr.madu59.obe.client.mixin.blockentity.sign;

import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.sign.ext.SignBlockEntityExt;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin implements SignBlockEntityExt {

    @Inject(method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.renderBoth(true);
        ext.isSupportedBlockEntity(be.getType() == BlockEntityType.SIGN || be.getType() == BlockEntityType.HANGING_SIGN);
    }

    @Unique private FormattedCharSequence[] frontLines = null;
    @Unique private FormattedCharSequence[] backLines = null;
    @Unique private float[] frontWidths = null;
    @Unique private float[] backWidths = null;
    @Unique private SignText lastFrontText = null;
    @Unique private SignText lastBackText = null;
    @Unique private boolean isFilteringEnabled = true;

    @Override
    public FormattedCharSequence[] getCachedLines(boolean front) {
        return front ? frontLines : backLines;
    }

    @Override
    public void setCachedLines(boolean front, FormattedCharSequence[] lines) {
        if (front) frontLines = lines;
        else backLines = lines;
    }

    @Override
    public SignText getLastText(boolean front) {
        return front ? lastFrontText : lastBackText;
    }

    @Override
    public void setLastText(boolean front, SignText text) {
        if (front) lastFrontText = text;
        else lastBackText = text;
    }

    @Override
    public float[] getLineWidths(boolean front) {
        if (front) return this.frontWidths;
        else return this.backWidths;
    }

    @Override
    public void setLineWidths(boolean front, float[] widths) {
        if (front) this.frontWidths = widths;
        else this.backWidths = widths;
    }

    @Override
    public boolean getLastFiltering() {
        return this.isFilteringEnabled;
    }

    @Override
    public void setLastFiltering(boolean isEnabled) {
        this.isFilteringEnabled = isEnabled;
    }
}

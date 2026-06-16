package fr.madu59.obe.client.mixin.blockentity.decoratedpot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;

@Mixin(DecoratedPotBlockEntity.class)
public class DecoratedPotBlockEntityMixin{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;

        ext.isSupportedBlockEntity(be.getType() == BlockEntityTypes.DECORATED_POT);
        if(((DecoratedPotBlockEntity)be).getDecorations() != PotDecorations.EMPTY) ext.renderMode(RenderMode.ENTITY);
    }

    @Inject(method = "triggerEvent", at = @At("RETURN"))
    public void obe$triggerEvent(final int event, final int data, CallbackInfoReturnable<Boolean> ci) {
        DecoratedPotBlockEntity be = (DecoratedPotBlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        RenderModeManager.setRenderModeDelayed(ext, RenderMode.ENTITY, be.getBlockPos());
        ext.setTimer(be.wobbleStartedAtTick, be.lastWobbleStyle.duration);
    }

    @Inject(method = "getDecorations", at = @At("RETURN"))
    public void obe$updateTimer(CallbackInfoReturnable<PotDecorations> cir) {
        DecoratedPotBlockEntity be = (DecoratedPotBlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        if(ext.isTimerFinished()) RenderModeManager.setRenderModeDelayed(ext, RenderMode.TERRAIN, be.getBlockPos());
        if(cir.getReturnValue() != PotDecorations.EMPTY) RenderModeManager.setRenderModeDelayed(ext, RenderMode.ENTITY, be.getBlockPos());
    }
}

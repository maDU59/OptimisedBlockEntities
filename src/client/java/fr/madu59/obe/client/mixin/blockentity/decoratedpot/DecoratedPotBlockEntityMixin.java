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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity.Decorations;

@Mixin(DecoratedPotBlockEntity.class)
public class DecoratedPotBlockEntityMixin{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;

        ext.isSupportedBlockEntity(be.getType() == BlockEntityType.DECORATED_POT);
        if(((DecoratedPotBlockEntity)be).getDecorations() != Decorations.EMPTY) ext.renderMode(RenderMode.ENTITY);
    }

    @Inject(method = "getDecorations", at = @At("RETURN"))
    public void obe$updateTimer(CallbackInfoReturnable<Decorations> cir) {
        DecoratedPotBlockEntity be = (DecoratedPotBlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        if(cir.getReturnValue() != Decorations.EMPTY) RenderModeManager.setRenderModeDelayed(ext, RenderMode.ENTITY, be.getBlockPos());
    }
}

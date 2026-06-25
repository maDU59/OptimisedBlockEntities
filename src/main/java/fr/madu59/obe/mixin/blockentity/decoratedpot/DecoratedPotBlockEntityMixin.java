package fr.madu59.obe.mixin.blockentity.decoratedpot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.registry.Registry;
import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity.Decorations;

@Mixin(DecoratedPotBlockEntity.class)
public abstract class DecoratedPotBlockEntityMixin{

    @Shadow
    private Decorations decorations;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;

        ext.isSupportedBlockEntity(Registry.isSupported("decorated_pot", be.getType()));
        obe$updatePot();
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void obe$load(CallbackInfo ci) {
        obe$updatePot();
    }

    @Inject(method = "setFromItem", at = @At("RETURN"))
    public void obe$fromItem(CallbackInfo ci) {
        obe$updatePot();
    }

    @Unique
    private void obe$updatePot(){
        DecoratedPotBlockEntity be = (DecoratedPotBlockEntity)(Object)this;
        if(!be.hasLevel()) return;
        BlockEntityExt ext = (BlockEntityExt)be;
        if(!decorations.equals(Decorations.EMPTY)){
            if(!ext.forceEntity()) RenderModeManager.setDirty(be.getBlockPos());
            ext.forceEntity(true);
        }
        else{
            ext.forceEntity(false);
        }
    }
}

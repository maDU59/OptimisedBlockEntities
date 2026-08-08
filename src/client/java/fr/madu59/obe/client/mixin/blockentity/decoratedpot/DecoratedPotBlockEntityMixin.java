package fr.madu59.obe.client.mixin.blockentity.decoratedpot;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;

@Mixin(DecoratedPotBlockEntity.class)
public abstract class DecoratedPotBlockEntityMixin{

    @Unique PotDecorations defaultPotDecorations = new PotDecorations(Optional.of(new ItemStackTemplate(Items.BRICK)), Optional.of(new ItemStackTemplate(Items.BRICK)), Optional.of(new ItemStackTemplate(Items.BRICK)), Optional.of(new ItemStackTemplate(Items.BRICK)));

    @Shadow
    private PotDecorations decorations;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {

        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;

        ext.isSupported(Registry.isSupported("decorated_pot", be.getType()));
        obe$updatePot();
    }

    @Inject(method = "loadAdditional", at = @At("RETURN"))
    public void obe$load(CallbackInfo ci) {
        obe$updatePot();
    }

    @Inject(method = "applyImplicitComponents", at = @At("RETURN"))
    public void obe$applyComponents(CallbackInfo ci) {
        obe$updatePot();
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
    }

    @Unique
    private void obe$updatePot(){
        DecoratedPotBlockEntity be = (DecoratedPotBlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        if(!decorations.equals(PotDecorations.EMPTY) && !decorations.equals(defaultPotDecorations)){
            ext.forceEntity(true);
        }
        else{
            ext.forceEntity(false);
        }
    }
}

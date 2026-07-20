package fr.madu59.obe.client.mixin.blockentity.compat.lootr;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity.AnimationStatus;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.block.entities.LootrShulkerBlockEntity;

@Pseudo
@Mixin(value = LootrShulkerBlockEntity.class, remap = false)
public class LootrShulkerBoxBlockEntityMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$register(CallbackInfo ci){
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.isSupported(Registry.isSupported("shulker_box", be.getType()));
        ext.hasSpecialRenderer(true);
    }

    @Inject(method = "updateAnimation", at = @At("HEAD"))
    private void obe$lidAnimateTick(final Level level, final BlockPos pos, final BlockState state, CallbackInfo ci) {
        LootrShulkerBlockEntity be = (LootrShulkerBlockEntity)(Object)this;
        if(be.getAnimationStatus() != AnimationStatus.CLOSED){
            RenderModeManager.setRenderModeDelayed(be, RenderMode.ENTITY, be.getBlockPos());
        }
        else{
            RenderModeManager.setRenderModeDelayed(be, RenderMode.TERRAIN, be.getBlockPos());
        }
    }
}

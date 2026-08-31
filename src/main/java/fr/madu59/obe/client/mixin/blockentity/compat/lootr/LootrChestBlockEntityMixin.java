package fr.madu59.obe.client.mixin.blockentity.compat.lootr;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;

@Pseudo
@Mixin(value = LootrChestBlockEntity.class, remap = false)
public class LootrChestBlockEntityMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$register(CallbackInfo ci){
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.hasSpecialRenderer(true);
    }

    @Inject(method = "defaultTick", at = @At("HEAD"))
    private void obe$lidAnimateTick(final Level level, final BlockPos pos, final BlockState state, CallbackInfo ci) {
        LootrChestBlockEntity be = (LootrChestBlockEntity)(Object)this;
        if(be.getOpenNess(0.5f) > 0.0001){
            RenderModeManager.setRenderModeDelayed(be, RenderMode.ENTITY, pos);
        }
        else{
            RenderModeManager.setRenderModeDelayed(be, RenderMode.TERRAIN, pos);
        }
    }
}

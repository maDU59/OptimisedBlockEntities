package fr.madu59.obe.mixin.blockentity.chest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.registry.Registry;
import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(EnderChestBlockEntity.class)
public abstract class EnderChestBlockEntityMixin{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.isSupportedBlockEntity(Registry.isSupported("chest", be.getType()));
    }

    @Inject(method = "lidAnimateTick", at = @At("HEAD"))
    private static void obe$lidAnimateTick(final Level level, final BlockPos pos, final BlockState state, final EnderChestBlockEntity entity, CallbackInfo ci) {
        BlockEntityExt ext = (BlockEntityExt)entity;
        if(entity.getOpenNess(0) > 0.05){
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.ENTITY, pos);
        }
        else{
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.TERRAIN, pos);
        }
    }
}

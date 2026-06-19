package fr.madu59.obe.client.mixin.blockentity.chest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin{
    @Inject(method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.isSupportedBlockEntity(Registry.isSupported("chest", be.getType()));
    }

    @Inject(method = "lidAnimateTick", at = @At("HEAD"))
    private static void obe$lidAnimateTick(final Level level, final BlockPos pos, final BlockState state, final ChestBlockEntity entity, CallbackInfo ci) {
        BlockEntityExt ext = (BlockEntityExt)entity;
        if(entity.getOpenNess(0) > 0.05){
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.ENTITY, pos);
        }
        else{
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.TERRAIN, pos);
        }
    }
}

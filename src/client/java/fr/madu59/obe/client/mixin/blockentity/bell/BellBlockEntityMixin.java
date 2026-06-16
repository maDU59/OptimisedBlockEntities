package fr.madu59.obe.client.mixin.blockentity.bell;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;

@Mixin(BellBlockEntity.class)
public class BellBlockEntityMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;

        ext.isSupportedBlockEntity(be.getType() == BlockEntityTypes.BELL);
        ext.hasSpecialRenderer(true);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private static void tick(final Level level, final BlockPos pos, final BlockState state, final BellBlockEntity entity, final @Coerce Object onResonationEnd, CallbackInfo ci) {
        if (level != null && !level.isClientSide()) return;
        BlockEntityExt ext = (BlockEntityExt)entity;
        if(entity.shaking){
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.ENTITY, pos);
        }
        else{
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.TERRAIN, pos);
        }
    }
}

package fr.madu59.obe.client.mixin.blockentity.chest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin{
    @Inject(method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.obe$isSupported(Registry.isSupported("chest", be));
    }

    @WrapMethod(method = "lidAnimateTick", order = 2000)
    private static void obe$lidAnimateTick(final Level level, final BlockPos pos, final BlockState state, final ChestBlockEntity entity, Operation<Void> original) {
        original.call(level, pos, state, entity);
        
        BlockEntityExt ext = (BlockEntityExt)entity;

        ChestBlockEntity doubleChest = ChestUtil.getOtherHalf(level, pos, state);
        float openness = entity.getOpenNess(0.5f);
        if(doubleChest != null) openness = Math.max(openness, doubleChest.getOpenNess(0.5f));

        RenderMode mode = openness > 0 ? RenderMode.ENTITY : RenderMode.TERRAIN;
        RenderModeManager.setRenderModeDelayed(ext, mode, pos);
        if(doubleChest != null) RenderModeManager.setRenderModeDelayed(doubleChest, mode, pos);
    }
}

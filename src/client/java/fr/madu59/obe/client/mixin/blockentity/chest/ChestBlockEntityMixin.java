package fr.madu59.obe.client.mixin.blockentity.chest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin{
    @Inject(method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.isSupported(Registry.isSupported("chest", be.getType()));
    }

    @Inject(method = "lidAnimateTick", at = @At("RETURN"))
    private static void obe$lidAnimateTick(final Level level, final BlockPos pos, final BlockState state, final ChestBlockEntity entity, CallbackInfo ci) {
        BlockEntityExt ext = (BlockEntityExt)entity;
        if(entity.getOpenNess(0.5f) > 0){
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.ENTITY, pos);

            ChestBlockEntity doubleChest = obe$getDoubleChest(level, pos, state);
            if(doubleChest != null) RenderModeManager.setRenderModeDelayed(doubleChest, RenderMode.ENTITY, pos);
        }
        else{
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.TERRAIN, pos);

            ChestBlockEntity doubleChest = obe$getDoubleChest(level, pos, state);
            if(doubleChest != null) RenderModeManager.setRenderModeDelayed(doubleChest, RenderMode.TERRAIN, pos);
        }
    }

    @Unique
    private static ChestBlockEntity obe$getDoubleChest(final Level level, final BlockPos pos, final BlockState state){
        ChestType type = state.getValueOrElse(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
        if (type == ChestType.SINGLE) {
            return null;
        }

        Direction facing = state.getValueOrElse(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
        
        Direction connectedDirection = (type == ChestType.LEFT) ? facing.getClockWise() : facing.getCounterClockWise();
        BlockPos neighborPos = pos.relative(connectedDirection);

        if (level.getBlockEntity(neighborPos) instanceof ChestBlockEntity neighborChest) {
            return neighborChest;
        }

        return null;
    }
}

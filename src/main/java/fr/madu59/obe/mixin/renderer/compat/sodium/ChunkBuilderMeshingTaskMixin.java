package fr.madu59.obe.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager.RenderMode;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Pseudo
@Mixin(value = ChunkBuilderMeshingTask.class, remap = false)
public class ChunkBuilderMeshingTaskMixin {

    // @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/world/WorldSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"))
    // private BlockState obe$getBlockState(@Coerce Object slice, int x, int y, int z, Operation<BlockState> original, @Share("be") LocalRef<BlockEntity> beRef){
    //     beRef.set(slice.getBlockEntity(x, y, z)); Can't get it to work, there are always some fabric api errors
    //     return original.call(slice, x, y, z);
    // }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"), require = 0)
    private RenderShape obe$getRenderShape(BlockState state, @Local(ordinal = 8) int x, @Local(ordinal = 6) int y, @Local(ordinal = 7) int z){
        if(RenderModeManager.hasBlockEntity(state)){
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(new BlockPos(x, y, z));
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupportedBlockEntity()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, be);
                if(ext.isEnabled() && !ext.hasSpecialRenderer() && ext.renderMode() != RenderMode.TERRAIN && ext.renderMode() != RenderMode.INTERMEDIATE){
                    return RenderShape.INVISIBLE;
                }
                if(ext.isEnabled() && (ext.renderMode() == RenderMode.TERRAIN || ext.renderMode() == RenderMode.INTERMEDIATE)){
                    return RenderShape.MODEL;
                }
            }
        }
        return state.getRenderShape();
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;m_60799_()Lnet/minecraft/world/level/block/RenderShape;"), require = 0)
    private RenderShape obe$getRenderShape2(BlockState state, @Local(ordinal = 8) int x, @Local(ordinal = 6) int y, @Local(ordinal = 7) int z){
        if(RenderModeManager.hasBlockEntity(state)){
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(new BlockPos(x, y, z));
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupportedBlockEntity()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, be);
                if(ext.isSupportedBlockEntity() && !ext.hasSpecialRenderer() && ext.renderMode() != RenderMode.TERRAIN && ext.renderMode() != RenderMode.INTERMEDIATE){
                    return RenderShape.INVISIBLE;
                }
                if(ext.isSupportedBlockEntity() && (ext.renderMode() == RenderMode.TERRAIN || ext.renderMode() == RenderMode.INTERMEDIATE)){
                    return RenderShape.MODEL;
                }
            }
        }
        return state.getRenderShape();
    }
}

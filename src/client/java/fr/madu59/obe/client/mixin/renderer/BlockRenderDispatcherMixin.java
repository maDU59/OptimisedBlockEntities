package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {

    @Redirect(method = "renderBatched", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, @Local BlockPos pos){
        if(RenderModeManager.hasBlockEntity(state)){
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, be);
                if(ext.isSupportedBlockEntity() && !ext.hasSpecialRenderer() && ext.renderMode() != RenderMode.TERRAIN){
                    return RenderShape.INVISIBLE;
                }
                if(ext.isSupportedBlockEntity() && ext.renderMode() == RenderMode.TERRAIN){
                    return RenderShape.MODEL;
                }
            }
        }
        return state.getRenderShape();
    }

    @WrapOperation(method = "renderBreakingTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, Operation<RenderShape> original, @Local BlockPos pos){
        if(RenderModeManager.hasBlockEntity(state)){
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupportedBlockEntity()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, be);
                if(ext.forceEntity()){
                    return RenderShape.INVISIBLE;
                }
                if(ext.isEnabled() && !ext.hasSpecialRenderer() && ext.renderMode() != RenderMode.TERRAIN && ext.renderMode() != RenderMode.INTERMEDIATE){
                    return RenderShape.INVISIBLE;
                }
                if(ext.isEnabled() && (ext.renderMode() == RenderMode.TERRAIN || ext.renderMode() == RenderMode.INTERMEDIATE)){
                    return RenderShape.MODEL;
                }
            }
        }
        return original.call(state);
    }
}

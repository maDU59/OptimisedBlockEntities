package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SectionCompiler.class)
public class SectionCompilerMixin {

    @WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState obe$getBlockState(RenderSectionRegion region, BlockPos pos, Operation<BlockState> original, @Share("be") LocalRef<BlockEntity> beRef){
        beRef.set(region.getBlockEntity(pos));
        return original.call(region, pos);
    }

    @WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, Operation<RenderShape> original, @Share("be") LocalRef<BlockEntity> beRef, @Local SectionPos sectionPos){
        if(state.hasBlockEntity()){
            BlockEntity be = beRef.get();
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupportedBlockEntity()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, sectionPos);
                if(ext.forceEntity()){
                    return RenderShape.INVISIBLE;
                }
                if(ext.isEnabled() && !ext.hasSpecialRenderer() && ext.renderModeDelayed() != RenderMode.TERRAIN){
                    return RenderShape.INVISIBLE;
                }
                if(ext.isEnabled() && ext.renderModeDelayed() == RenderMode.TERRAIN){
                    return RenderShape.MODEL;
                }
            }
        }
        return original.call(state);
    }
}

package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import fr.madu59.obe.client.renderer.OBEBlockRenderer;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SectionCompiler.class)
public class SectionCompilerMixin {
    @Unique private OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();;

    @WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState obe$getBlockState(RenderSectionRegion region, BlockPos pos, Operation<BlockState> original, @Share("be") LocalRef<BlockEntity> beRef){
        beRef.set(region.getBlockEntity(pos));
        return original.call(region, pos);
    }

    @WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, Operation<RenderShape> original, @Share("be") LocalRef<BlockEntity> beRef){
        if(state.hasBlockEntity()){
            BlockEntity be = beRef.get();
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, be);
                if(ext.isSupportedBlockEntity() && !ext.hasSpecialRenderer() && ext.renderMode() != RenderMode.TERRAIN && ext.renderMode() != RenderMode.INTERMEDIATE){
                    return RenderShape.INVISIBLE;
                }
                if(ext.isSupportedBlockEntity() && (ext.renderMode() == RenderMode.TERRAIN || ext.renderMode() == RenderMode.INTERMEDIATE)){
                    return RenderShape.MODEL;
                }
            }
        }
        return original.call(state);
    }
}

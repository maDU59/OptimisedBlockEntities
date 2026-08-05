package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SectionCompiler.class)
public abstract class SectionCompilerMixin {

    @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

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
            if(ext != null && ext.isSupported()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, sectionPos);
                if(ext.isEnabled() && ext.renderModeDelayed() == RenderMode.TERRAIN){
                    return RenderShape.MODEL;
                }
            }
        }
        return original.call(state);
    }

    @WrapOperation(
        method = "compile",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/block/model/BlockStateModel;"
        )
    )
    public BlockStateModel obe$wrapRenderModel(BlockRenderDispatcher instance, BlockState state, Operation<BlockStateModel> original, @Share("be") LocalRef<BlockEntity> beRef) {
        BlockStateModel originalModel = original.call(instance, state);
        
        if(state.hasBlockEntity()){

            BlockStateModel model = originalModel;
            BlockEntity be = beRef.get();
            BlockEntityExt ext = (BlockEntityExt) be;

            if(ext != null){
                if(ext.renderModeDelayed() != RenderMode.TERRAIN || !ext.isSupported() || !ext.isEnabled() || ext.forceEntity()){
                    model = ResourceUtil.getDefaultModel(be.getBlockState());
                }
                else if(ext.hasSpecialRenderer()) model = blockEntityModelsManager.getModel(state, originalModel, be);
            }

            return model;
        }
        return originalModel;
    }
}

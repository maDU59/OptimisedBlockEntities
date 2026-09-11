package fr.madu59.obe.client.mixin.renderer.compat.vulkanmod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.util.meshing.SectionMeshingUtil;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.vulkanmod.render.chunk.build.renderer.AbstractBlockRenderContext;
import net.vulkanmod.render.chunk.build.renderer.BlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(value = BlockRenderer.class, remap = false)
public abstract class BlockRendererMixin extends AbstractBlockRenderContext {

    @WrapOperation(
            method = "renderBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/BlockStateModelSet;get(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;"
            )
    )
    private BlockStateModel obe$getCorrectedModel(BlockStateModelSet instance, BlockState state, Operation<BlockStateModel> original, @Local BlockPos pos) {
        BlockStateModel originalModel = original.call(instance, state);
        BlockEntity be = this.renderRegion.getBlockEntity(pos);
        return SectionMeshingUtil.getCorrectedModel(state, be, originalModel, pos);
    }
}
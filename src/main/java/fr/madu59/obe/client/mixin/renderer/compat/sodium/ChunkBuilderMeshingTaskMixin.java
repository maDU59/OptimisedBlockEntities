package fr.madu59.obe.client.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.util.meshing.SectionMeshingUtil;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Pseudo
@Mixin(value = ChunkBuilderMeshingTask.class, remap = false)
public abstract class ChunkBuilderMeshingTaskMixin {

    @Unique private SectionPos sectionPos;

    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, Operation<RenderShape> original, @Share("be") LocalRef<BlockEntity> beRef, @Local WorldSlice slice, @Local(ordinal = 0) MutableBlockPos pos){
        RenderShape renderShape = original.call(state);
        if(state.hasBlockEntity()){
            if(sectionPos == null){
                sectionPos = SectionPos.of(pos);
            }
            BlockEntity be = slice.getBlockEntity(pos);
            beRef.set(be);
            return SectionMeshingUtil.getCorrectedRenderShape(state, be, sectionPos, renderShape);
        }
        else return renderShape;
    }

    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;m_60799_()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape2(BlockState state, Operation<RenderShape> original, @Share("be") LocalRef<BlockEntity> beRef, @Local WorldSlice slice, @Local(ordinal = 0) MutableBlockPos pos){
        RenderShape renderShape = original.call(state);
        if(state.hasBlockEntity()){
            if(sectionPos == null){
                sectionPos = SectionPos.of(pos);
            }
            BlockEntity be = slice.getBlockEntity(pos);
            beRef.set(be);
            return SectionMeshingUtil.getCorrectedRenderShape(state, be, sectionPos, renderShape);
        }
        else return renderShape;
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V"
        )
    )
    public void obe$wrapRenderModel(BlockRenderer instance, BlockRenderContext ctx, ChunkBuildBuffers buffers, Operation<Void> original, @Share("be") LocalRef<BlockEntity> beRef) {
        BakedModel model = SectionMeshingUtil.getCorrectedModel(ctx.state(), beRef.get(), ctx.model());

        BlockPos origin = new BlockPos((int)ctx.origin().x(), (int)ctx.origin().y(), (int)ctx.origin().z());
        ctx.update(ctx.pos(), origin, ctx.state(), model, ctx.seed(), ctx.modelData(), ctx.renderLayer());
        original.call(instance, ctx, buffers);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;"
        ),
        require = 0
    )
    private BlockEntityRenderer<?> obe$wrapShouldRender(BlockEntityRenderDispatcher instance, BlockEntity be, Operation<BlockEntityRenderer<?>> original) {
        BlockEntityExt ext = (BlockEntityExt) be;
        if(ext != null && ext.obe$isEnabled() && (!(ext.obe$forceEntity() || !ext.obe$isSupported() || ext.obe$renderModeDelayed() == RenderMode.ENTITY || ext.obe$renderBoth()) || ext.obe$shouldSkipRendering())) {
            return null;
        }
        return original.call(instance, be);
    }

    @WrapOperation(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;m_112265_(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;"
            ),
            require = 0
    )
    private BlockEntityRenderer<?> obe$wrapShouldRender2(BlockEntityRenderDispatcher instance, BlockEntity be, Operation<BlockEntityRenderer<?>> original) {
        BlockEntityExt ext = (BlockEntityExt) be;
        if(ext != null && ext.obe$isEnabled() && (!(ext.obe$forceEntity() || !ext.obe$isSupported() || ext.obe$renderModeDelayed() == RenderMode.ENTITY || ext.obe$renderBoth()) || ext.obe$shouldSkipRendering())) {
            return null;
        }
        return original.call(instance, be);
    }
}

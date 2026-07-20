package fr.madu59.obe.client.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
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
public class ChunkBuilderMeshingTaskMixin {

    @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();
    @Unique private SectionPos sectionPos;

    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/world/WorldSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState obe$getBlockState(WorldSlice slice, int x, int y, int z, Operation<BlockState> original, @Share("be") LocalRef<BlockEntity> beRef){
        beRef.set(slice.getBlockEntity(x, y, z));
        return original.call(slice, x, y, z);
    }

    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, Operation<RenderShape> original, @Share("be") LocalRef<BlockEntity> beRef, @Local(ordinal = 0) MutableBlockPos pos){
        if(state.hasBlockEntity()){
            BlockEntity be = beRef.get();
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupported()) {
                if(sectionPos == null){
                    sectionPos = SectionPos.of(pos);
                }
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, sectionPos);
                if(ext.forceEntity()){
                    return RenderShape.INVISIBLE;
                }
                if(ext.isEnabled() && ext.renderModeDelayed() == RenderMode.TERRAIN){
                    return RenderShape.MODEL;
                }
            }
        }
        return original.call(state);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V"
        )
    )
    public void obe$wrapRenderModel(BlockRenderer instance, BlockRenderContext ctx, ChunkBuildBuffers buffers, Operation<Void> original, @Share("be") LocalRef<BlockEntity> beRef) {
        if(ctx.state().hasBlockEntity()){
            BakedModel model = null;
            BlockPos origin = new BlockPos((int)ctx.origin().x(), (int)ctx.origin().y(), (int)ctx.origin().z());
            BlockEntity be = beRef.get();
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupported()) {
                if(ext.isEnabled() && !ext.hasSpecialRenderer() && ext.renderModeDelayed() != RenderMode.TERRAIN){
                    model = new BlockEntityStateModel();
                }
                else if(ext.hasSpecialRenderer()) model = blockEntityModelsManager.getModel(ctx.state(), ctx.pos(), ctx.seed(), ctx.model(), be);
            }
            if(model != null) ctx.update(ctx.pos(), origin, ctx.state(), model, ctx.seed());
        }
        original.call(instance, ctx, buffers);
    }

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;getRenderer(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;"
        )
    )
    private BlockEntityRenderer<?> obe$wrapShouldRender(BlockEntityRenderDispatcher instance, BlockEntity be, Operation<BlockEntityRenderer<?>> original) {
        BlockEntityExt ext = (BlockEntityExt) be;
        if(ext != null && ext.isEnabled() && (!(ext.forceEntity() || !ext.isSupported() || ext.renderModeDelayed() == RenderMode.ENTITY || ext.renderBoth()) || ext.shouldSkipRendering())) {
            return null;
        }
        return original.call(instance, be);
    }
}

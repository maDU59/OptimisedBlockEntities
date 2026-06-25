package fr.madu59.obe.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import fr.madu59.obe.model.BlockEntityStateModel;
import fr.madu59.obe.renderer.OBEBlockRenderer;
import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager.RenderMode;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Pseudo
@Mixin(value = ChunkBuilderMeshingTask.class, remap = false)
public class ChunkBuilderMeshingTaskMixin {

    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();

    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/world/WorldSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState obe$getBlockState(WorldSlice slice, int x, int y, int z, Operation<BlockState> original, @Share("be") LocalRef<BlockEntity> beRef){
        beRef.set(slice.getBlockEntity(x, y, z));
        return original.call(slice, x, y, z);
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"), require = 0)
    private RenderShape obe$getRenderShape(BlockState state, @Local(ordinal = 8) int x, @Local(ordinal = 6) int y, @Local(ordinal = 7) int z){
        if(RenderModeManager.hasBlockEntity(state)){
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(new BlockPos(x, y, z));
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
        return state.getRenderShape();
    }

    @Redirect(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;m_60799_()Lnet/minecraft/world/level/block/RenderShape;"), require = 0)
    private RenderShape obe$getRenderShape2(BlockState state, @Local(ordinal = 8) int x, @Local(ordinal = 6) int y, @Local(ordinal = 7) int z, @Share("be") LocalRef<BlockEntity> beRef){
        if(RenderModeManager.hasBlockEntity(state)){
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(new BlockPos(x, y, z));
            beRef.set(be);
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

    @WrapOperation(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V"
        )
    )
    public void obe$wrapRenderModel(BlockRenderer instance, BlockRenderContext ctx, ChunkBuildBuffers buffers, Operation<Void> original, @Share("be") LocalRef<BlockEntity> beRef) {
        BakedModel model = null;
        if(RenderModeManager.hasBlockEntity(ctx.state())){
            BlockPos origin = new BlockPos((int)ctx.origin().x(), (int)ctx.origin().y(), (int)ctx.origin().z());
            BlockEntity be = beRef.get();
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupportedBlockEntity()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, be);
                if(ext.isEnabled() && !ext.hasSpecialRenderer() && ext.renderMode() != RenderMode.TERRAIN && ext.renderMode() != RenderMode.INTERMEDIATE){
                    model =  new BlockEntityStateModel();
                }
                else model = obeBlockRenderer.getModel(ctx.state(), ctx.pos(), ctx.seed(), ctx.model());
            }
            if(model != null) ctx.update(ctx.pos(), origin, ctx.state(), model, ctx.seed(), ctx.modelData(), ctx.renderLayer());
        }
        original.call(instance, ctx, buffers);
    }
}

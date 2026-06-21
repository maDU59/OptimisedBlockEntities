package fr.madu59.obe.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import fr.madu59.obe.model.BlockEntityStateModel;
import fr.madu59.obe.renderer.OBEBlockRenderer;
import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager.RenderMode;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

@Pseudo
@Mixin(value = BlockRenderer.class, remap = false)
public class BlockRendererMixin {
    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();

    @ModifyVariable(method = "renderModel", at = @At("HEAD"), argsOnly = true)
    public BlockRenderContext renderModel(BlockRenderContext ctx, BlockRenderContext originalCtx, ChunkBuildBuffers buffers) {
        if(RenderModeManager.hasBlockEntity(ctx.state())){
            BlockPos origin = new BlockPos((int)ctx.origin().x(), (int)ctx.origin().y(), (int)ctx.origin().z());
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(ctx.pos());
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupportedBlockEntity()) {
                RenderModeManager.updateBlockEntity(ext, be);
                if(ext.isSupportedBlockEntity() && !ext.hasSpecialRenderer() && ext.renderMode() != RenderMode.TERRAIN){
                    ctx.update(ctx.pos(), origin, ctx.state(), new BlockEntityStateModel(), ctx.seed(), ctx.modelData(), ctx.renderLayer());
                    return ctx;
                }
                BakedModel model = obeBlockRenderer.getModel(ctx.state(), ctx.pos(), ctx.seed(), ctx.model());
                if(model != null) {
                    ctx.update(ctx.pos(), origin, ctx.state(), model, ctx.seed(), ctx.modelData(), ctx.renderLayer());
                    return ctx;
                }
            }
        }
        return originalCtx;
    }
}

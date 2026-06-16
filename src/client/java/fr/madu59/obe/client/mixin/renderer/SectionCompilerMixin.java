package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.renderer.OBEBlockRenderer;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SectionCompiler.class)
public class SectionCompilerMixin {
    @Unique private OBEBlockRenderer obeBlockRenderer;
    @Unique private final ThreadLocal<BlockPos> obe$pos = ThreadLocal.withInitial(() -> null);
    @Unique private final ThreadLocal<RenderSectionRegion> obe$region = ThreadLocal.withInitial(() -> null);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$init(final boolean ambientOcclusion, final boolean cutoutLeaves, final BlockStateModelSet blockModelSet, final FluidStateModelSet fluidModelSet, final BlockColors blockColors, final BlockEntityRenderDispatcher blockEntityRenderer, CallbackInfo ci) {
        this.obeBlockRenderer = new OBEBlockRenderer();
    }

    @Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState obe$getBlockState(RenderSectionRegion region, BlockPos pos){
        this.obe$pos.set(pos);
        this.obe$region.set(region);
        return region.getBlockState(pos);
    }

    @Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state){
        if(state.hasBlockEntity()){
            BlockEntity be = obe$region.get().getBlockEntity(obe$pos.get());
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null) {
                RenderModeManager.updateBlockEntity(ext, be);
                if(ext.isSupportedBlockEntity() && !ext.hasSpecialRenderer() && ext.renderMode() != RenderMode.TERRAIN){
                    return RenderShape.INVISIBLE;
                }
            }
        }
        return state.getRenderShape();
    }
}

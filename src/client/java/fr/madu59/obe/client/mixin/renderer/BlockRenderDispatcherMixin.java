package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {

    @Unique BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

    @WrapOperation(method = "renderBreakingTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, Operation<RenderShape> original, @Local BlockPos pos){
        if(state.hasBlockEntity()){
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupported()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, SectionPos.of(pos));
                if(ext.isEnabled() && ext.renderModeDelayed() == RenderMode.TERRAIN && !ext.forceEntity()){
                    return RenderShape.MODEL;
                }
            }
        }
        return original.call(state);
    }
    
    @WrapOperation(
        method = "renderBreakingTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;"
        )
    )
    private BakedModel obe$wrapBreakingTextureModel(
        BlockModelShaper shaper,
        BlockState state,
        Operation<BakedModel> original,
        @Local BlockPos pos,
        @Local BlockAndTintGetter level
    ) {    
        BakedModel originalModel = original.call(shaper, state);

        if(state.hasBlockEntity()){

            BakedModel model = originalModel;
            BlockEntity be = level.getBlockEntity(pos);
            BlockEntityExt ext = (BlockEntityExt) be;

            if(ext != null){
                if(ext.renderModeDelayed() != RenderMode.TERRAIN || !ext.isSupported() || !ext.isEnabled() || ext.forceEntity()){
                    model = ResourceUtil.getDefaultModel(be.getBlockState());
                }
                else if(ext.hasSpecialRenderer()) model = blockEntityModelsManager.getModel(state, originalModel, be);
            }

            if(model == null) model = ResourceUtil.getDefaultModel(be.getBlockState());

            return model;
        }
        return originalModel;
    }

    @WrapOperation(
        method = "renderBatched",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;"
        )
    )
    private BakedModel obe$wrapRenderBatchedModel(
        BlockRenderDispatcher dispatcher,
        BlockState state,
        Operation<BakedModel> original,
        @Local BlockPos pos,
        @Local BlockAndTintGetter level
    ) {    
        BakedModel originalModel = original.call(dispatcher, state);

        if(state.hasBlockEntity()){

            BakedModel model = originalModel;
            BlockEntity be = level.getBlockEntity(pos);
            BlockEntityExt ext = (BlockEntityExt) be;

            if(ext != null){
                if(ext.renderModeDelayed() != RenderMode.TERRAIN || !ext.isSupported() || !ext.isEnabled() || ext.forceEntity()){
                    model = ResourceUtil.getDefaultModel(be.getBlockState());
                }
                else if(ext.hasSpecialRenderer()) model = blockEntityModelsManager.getModel(state, originalModel, be);
            }

            if(model == null) model = ResourceUtil.getDefaultModel(be.getBlockState());

            return model;
        }
        return originalModel;
    }
}
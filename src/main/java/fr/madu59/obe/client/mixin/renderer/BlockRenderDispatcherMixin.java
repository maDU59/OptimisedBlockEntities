package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.util.meshing.SectionMeshingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockRenderDispatcher.class)
public class BlockRenderDispatcherMixin {

    @Unique BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

    @WrapOperation(method = "renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/neoforged/neoforge/client/model/data/ModelData;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, Operation<RenderShape> original, @Local BlockPos pos){
        
        return SectionMeshingUtil.getCorrectedRenderShape(state, Minecraft.getInstance().level.getBlockEntity(pos), SectionPos.of(pos), original.call(state));
    }
    
    @WrapOperation(
        method = "renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/neoforged/neoforge/client/model/data/ModelData;)V",
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

        return SectionMeshingUtil.getCorrectedModel(state, level.getBlockEntity(pos), originalModel);
    }

    @WrapOperation(
        method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
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

        return SectionMeshingUtil.getCorrectedModel(state, level.getBlockEntity(pos), originalModel);
    }
}

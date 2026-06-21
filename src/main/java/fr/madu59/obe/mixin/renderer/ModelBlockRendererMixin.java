package fr.madu59.obe.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import fr.madu59.obe.renderer.OBEBlockRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();

    @ModifyVariable(method = "tesselateBlock", at = @At("HEAD"), argsOnly = true)
    private BakedModel obe$modifyModel(BakedModel model, BlockAndTintGetter blockAndTintGetter, BakedModel originalModel, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, RandomSource randomSource, long l, int i) {
        model = obeBlockRenderer.getModel(blockState, blockPos, blockState.getSeed(blockPos), model);
        return model == null? originalModel : model;
    }
}

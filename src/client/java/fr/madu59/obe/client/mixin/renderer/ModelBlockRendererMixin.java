package fr.madu59.obe.client.mixin.renderer;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.renderer.OBEBlockRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();
    @Unique private final RandomSource random = RandomSource.create();

    @ModifyVariable(method = "tesselateBlock", at = @At("HEAD"), argsOnly = true)
    private List<BlockModelPart> obe$modifyModel(List<BlockModelPart> list, BlockAndTintGetter blockAndTintGetter, List<BlockModelPart> originalList, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer vertexConsumer, boolean bl, int i) {
        BlockStateModel model = obeBlockRenderer.getModel(blockState, blockPos, blockState.getSeed(blockPos), new BlockEntityStateModel(list.get(0).particleIcon()));
        if(model != null){
            random.setSeed(blockState.getSeed(blockPos));
            list = model.collectParts(random);
        }
        return list == null? originalList : list;
    }
}

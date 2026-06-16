package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import fr.madu59.obe.client.renderer.OBEBlockRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();

    @ModifyVariable(method = "tesselateBlock", at = @At("HEAD"), argsOnly = true)
    private BlockStateModel obe$modifyModel(BlockStateModel model, BlockQuadOutput quadOutput, float x, float y, float z, BlockAndTintGetter region, BlockPos pos, BlockState blockState, BlockStateModel originalModel, long seed) {
        model = obeBlockRenderer.getModel(blockState, pos, seed);
        return model == null? originalModel : model;
    }
}

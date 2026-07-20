package fr.madu59.obe.client.mixin.renderer.compat.indigo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import net.minecraft.core.BlockPos;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;

@Pseudo
@Mixin(value = TerrainRenderContext.class, remap = false)
public abstract class TerrainRenderContextMixin extends AbstractBlockRenderContext {
    @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

    @ModifyVariable(method = "tessellateBlock", at = @At("HEAD"), argsOnly = true)
    private BakedModel obe$modifyModel(BakedModel model, BlockState blockState, BlockPos blockPos, BakedModel originalModel, PoseStack matrixStack) {
        model = blockEntityModelsManager.getModel(blockState, blockPos, blockState.getSeed(blockPos), model, blockInfo.blockView);
        return model == null? originalModel : model;
    }
}

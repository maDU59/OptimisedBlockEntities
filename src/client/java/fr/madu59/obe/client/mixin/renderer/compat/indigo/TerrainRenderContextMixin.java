package fr.madu59.obe.client.mixin.renderer.compat.indigo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.core.BlockPos;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Pseudo
@Mixin(value = TerrainRenderContext.class, remap = false)
public abstract class TerrainRenderContextMixin extends AbstractBlockRenderContext {
    @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

    @ModifyVariable(method = "tessellateBlock", at = @At("HEAD"), argsOnly = true)
    private BakedModel obe$modifyModel(BakedModel model, BlockState state, BlockPos blockPos, BakedModel originalModel, PoseStack matrixStack) {
        
        if(state.hasBlockEntity()){

            BlockEntity be = this.blockInfo.blockView.getBlockEntity(blockPos);
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

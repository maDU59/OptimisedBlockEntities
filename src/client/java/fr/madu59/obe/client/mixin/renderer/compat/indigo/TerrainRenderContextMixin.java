package fr.madu59.obe.client.mixin.renderer.compat.indigo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.ResourceUtil;
import fr.madu59.obe.client.util.meshing.SectionMeshingUtil;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractTerrainRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Pseudo
@Mixin(TerrainRenderContext.class)
public abstract class TerrainRenderContextMixin extends AbstractTerrainRenderContext{

    @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

    @ModifyVariable(
        method = "bufferModel",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    public BlockStateModel obe$wrapRenderModel(BlockStateModel originalModel, BlockStateModel model, BlockState state, BlockPos pos) {
        
        return SectionMeshingUtil.getCorrectedModel(state, this.blockInfo.blockView.getBlockEntity(pos), originalModel);
    }
}

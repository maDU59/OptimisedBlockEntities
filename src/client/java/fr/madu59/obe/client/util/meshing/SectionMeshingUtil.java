package fr.madu59.obe.client.util.meshing;

import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SectionMeshingUtil {

    private static final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

    public static RenderShape getCorrectedRenderShape(BlockState state, BlockEntity be, SectionPos sectionPos, RenderShape originalRenderShape){
        if(state.hasBlockEntity()){
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.obe$isSupported()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, sectionPos);
                if(ext.obe$isEnabled() && ext.obe$renderModeDelayed() == RenderMode.TERRAIN && !ext.obe$forceEntity()){
                    return RenderShape.MODEL;
                }
            }
        }
        return originalRenderShape;
    }

    public static BlockStateModel getCorrectedModel(BlockState state, BlockEntity be, BlockStateModel originalModel, BlockPos pos){
        if(state.hasBlockEntity()){

            BlockStateModel model = originalModel;
            BlockEntityExt ext = (BlockEntityExt) be;

            if(ext != null){
                if(ext.obe$renderModeDelayed() != RenderMode.TERRAIN || !ext.obe$isSupported() || !ext.obe$isEnabled() || ext.obe$forceEntity()){
                    model = ResourceUtil.getDefaultModel(be.getBlockState());
                }
                else if(ext.obe$hasSpecialRenderer()) model = blockEntityModelsManager.getModel(state, originalModel, be);
            }

            if(model == null) model = ResourceUtil.getDefaultModel(be.getBlockState());

            return model;
        }
        else{
            return originalModel;
        }
    }
}

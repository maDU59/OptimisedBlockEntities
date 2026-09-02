package fr.madu59.obe.client.util.meshing;

import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SectionMeshingUtil {

    private static final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

    public static RenderShape getCorrectedRenderShape(BlockState state, BlockEntity be, SectionPos sectionPos, RenderShape originalRenderShape){
        if(state.hasBlockEntity()){
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupported()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, sectionPos);
                if(ext.isEnabled() && ext.renderModeDelayed() == RenderMode.TERRAIN && !ext.forceEntity()){
                    return RenderShape.MODEL;
                }
            }
        }
        return originalRenderShape;
    }

    public static BakedModel getCorrectedModel(BlockState state, BlockEntity be, BakedModel originalModel){
        if(state.hasBlockEntity()){

            BakedModel model = originalModel;
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
        else{
            return originalModel;
        }
    }
}

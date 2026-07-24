package fr.madu59.obe.client.compat.emf;

import java.util.function.BiFunction;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.utils.ETFEntity;

public class EMFCompat {

    // Thanks to Traben, EMF/ETF dev for helping fixing issues with the compatibility  :D
    
    public static ModelPart applyRestPose(ModelPart root, BlockState blockState) {
        if (blockState.getBlock() instanceof EntityBlock entityBlock) {
            return applyRestPose(root, entityBlock.newBlockEntity(BlockPos.ZERO, blockState));
        }
        return root;
    }

    public static <T extends BlockEntity> ModelPart applyRestPose(ModelPart root, BlockEntity be) {
        try{
            if (root instanceof EMFModelPartRoot emfRoot) {
                
                var state = (EMFEntityRenderState) ETFEntityRenderState.forEntity((ETFEntity) be);

                EMFAnimationEntityContext.setCurrentEntityIteration(state);
                emfRoot.animate();
                
                return emfRoot;
            }

            return root;
        }
        catch(Exception e){
            System.out.println(e);
            return root;
        }
        finally{
            EMFAnimationEntityContext.reset();
            ETFRenderContext.endSpecialRenderOverlayPhase();
            ETFRenderContext.reset();
        }
    }
}

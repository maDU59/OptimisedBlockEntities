package fr.madu59.obe.client.compat.emf;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFEntity;

public class EMFCompat {

    // Thanks to Traben, EMF/ETF dev for helping fixing issues with the compatibility  :D
    
    public static ModelPart applyRestPose(ModelPart root, BlockState blockState) {
        if (root instanceof EMFModelPartRoot && blockState.getBlock() instanceof EntityBlock entityBlock) {
            return applyRestPose(root, entityBlock.newBlockEntity(BlockPos.ZERO, blockState));
        }
        return root;
    }

    public static <T extends BlockEntity> ModelPart applyRestPose(ModelPart root, BlockEntity be) {
        try{
            if (root instanceof EMFModelPartRoot emfRoot) {

                EMFAnimationApi.animateModelForEntity((EMFEntity) be, emfRoot, true);
                
                return emfRoot;
            }

            return root;
        }
        catch(Exception e){
            System.out.println(e);
            return root;
        }
    }
}

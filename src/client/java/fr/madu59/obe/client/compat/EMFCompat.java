package fr.madu59.obe.client.compat;

import java.util.function.BiFunction;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
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
        if(blockState.getBlock() instanceof TrappedChestBlock) return applyTrappedChestRestPose(root, blockState);
        else if(blockState.getBlock() instanceof EnderChestBlock) return applyEnderChestRestPose(root, blockState);
        else if(blockState.getBlock() instanceof BellBlock) return applyBellRestPose(root, blockState);
        else if(blockState.getBlock() instanceof ShulkerBoxBlock) return applyShulkerRestPose(root, blockState);
        else if(blockState.getBlock() instanceof ChestBlock) return applyChestRestPose(root, blockState);

        return root;
    }

    public static ModelPart applyChestRestPose(ModelPart root, BlockState blockState) {
        return applyRestPose(root, blockState, Blocks.CHEST, ChestBlockEntity::new);
    }

    public static ModelPart applyTrappedChestRestPose(ModelPart root, BlockState blockState) {
        return applyRestPose(root, blockState, Blocks.TRAPPED_CHEST, TrappedChestBlockEntity::new);
    }

    public static ModelPart applyEnderChestRestPose(ModelPart root, BlockState blockState) {
        return applyRestPose(root, blockState, Blocks.ENDER_CHEST, EnderChestBlockEntity::new);
    }

    public static ModelPart applyBellRestPose(ModelPart root, BlockState blockState) {
        return applyRestPose(root, blockState, Blocks.BELL, BellBlockEntity::new);
    }

    public static ModelPart applyShulkerRestPose(ModelPart root, BlockState blockState) {
        return applyRestPose(root, blockState, Blocks.SHULKER_BOX, ShulkerBoxBlockEntity::new);
    }

    public static <T extends BlockEntity> ModelPart applyRestPose(ModelPart root, BlockState blockState, Block block, BiFunction<BlockPos, BlockState, T> beConstructor) {
        try{
            if (root instanceof EMFModelPartRoot emfRoot) {
                
                var state = (EMFEntityRenderState) ETFEntityRenderState.forEntity(
                        (ETFEntity) beConstructor.apply(BlockPos.ZERO, block.defaultBlockState()));

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
            EMFAnimationEntityContext.setCurrentEntityOnShoulder(false);
        }
    }
}

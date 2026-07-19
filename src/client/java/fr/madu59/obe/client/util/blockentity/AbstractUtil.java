package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractUtil {
    public static Identifier getMaterial(BlockState state){
        return null;
    }

    public static Identifier getMaterial(BlockState state, BlockEntity be){
        throw new AbstractMethodError("getMaterial(BlockState, BlockEntity) was not implemented by " + AbstractUtil.class.getSimpleName());
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return null;
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state, BlockEntity be){
        throw new AbstractMethodError("getModelLayerLocation(BlockState, BlockEntity) was not implemented by " + AbstractUtil.class.getSimpleName());
    }

    public static void transform(BlockState state, PoseStack poseStack){
        
    }

    public static void transform(BlockState state, BlockEntity be, PoseStack poseStack){
        throw new AbstractMethodError("transform(BlockState, BlockEntity, PoseStack) was not implemented by " + AbstractUtil.class.getSimpleName());
    }
}

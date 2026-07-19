package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class BedUtil extends AbstractUtil{
    public static Identifier getMaterial(BlockState state){
        if(state.getBlock() instanceof BedBlock block){
            return  Sheets.getBedSprite(block.getColor()).texture();
        }
        return null;
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        if(state.hasProperty(BedBlock.PART)){
            if(state.getValue(BedBlock.PART) == BedPart.FOOT) return ModelLayers.BED_FOOT;
            else return ModelLayers.BED_HEAD;
        }
        else return null;
    }

    public static void transform(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(BedBlock.FACING);
        poseStack.mulPose(BedRenderer.modelTransform(facing));
    }
}

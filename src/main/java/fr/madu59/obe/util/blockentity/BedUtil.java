package fr.madu59.obe.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class BedUtil {
    public static ResourceLocation getBedMaterial(BlockState state){
        BedBlock block = (BedBlock) state.getBlock();
        return  Sheets.BED_TEXTURES[block.getColor().getId()].texture();
    }

    public static ModelLayerLocation getBedModelLayerLocation(BlockState state){
        if(state.getValue(BedBlock.PART) == BedPart.FOOT) return ModelLayers.BED_FOOT;
        else return ModelLayers.BED_HEAD;
    }

    public static void transformBed(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(BedBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5625F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F + facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
    }
}

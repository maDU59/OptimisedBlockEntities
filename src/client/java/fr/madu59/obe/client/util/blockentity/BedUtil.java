package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class BedUtil {
    public static Identifier getBedMaterial(BlockState state){
        BedBlock block = (BedBlock) state.getBlock();
        return  Sheets.getBedSprite(block.getColor()).texture();
    }

    public static ModelLayerLocation getBedModelLayerLocation(BlockState state){
        if(state.getValue(BedBlock.PART) == BedPart.FOOT) return ModelLayers.BED_FOOT;
        else return ModelLayers.BED_HEAD;
    }

    public static void transformBed(BlockState state, PoseStack poseStack){

    }
}

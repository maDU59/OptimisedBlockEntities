package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BedUtil {
    public static Identifier getBedMaterial(BlockState state){
        BedBlock block = (BedBlock) state.getBlock();
        return  Sheets.getBedSprite(block.getColor()).texture();
    }
}

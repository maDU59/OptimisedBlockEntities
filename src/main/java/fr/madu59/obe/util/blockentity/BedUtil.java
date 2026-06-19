package fr.madu59.obe.util.blockentity;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BedUtil {
    public static ResourceLocation getBedMaterial(BlockState state){
        BedBlock block = (BedBlock) state.getBlock();
        return  Sheets.BED_TEXTURES[block.getColor().getId()].texture();
    }
}

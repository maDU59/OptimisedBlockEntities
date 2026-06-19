package fr.madu59.obe.client.util.blockentity;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SignUtil {
    public static Identifier getSignMaterial(BlockState state){
        Block block = state.getBlock();
        WoodType woodType = SignBlock.getWoodType(state.getBlock());
        if(block instanceof WallSignBlock || block instanceof StandingSignBlock) return Sheets.getSignSprite(woodType).texture();
        return Sheets.getHangingSignSprite(woodType).texture();
    }
}

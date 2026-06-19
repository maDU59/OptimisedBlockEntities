package fr.madu59.obe.client.util.blockentity;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.golem.CopperGolemOxidationLevels;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockState;

public class CopperGolemStatueUtil {
    public static Identifier getCopperGolemStatueMaterial(BlockState state) {
        WeatherState oxydationLevel;
        if (state.getBlock() instanceof CopperGolemStatueBlock copperGolemStatueBlock) {
            oxydationLevel = copperGolemStatueBlock.getWeatheringState();
        } else {
            oxydationLevel = WeatherState.UNAFFECTED;
        }
        return CopperGolemOxidationLevels.getOxidationLevel(oxydationLevel).texture();
    }
}

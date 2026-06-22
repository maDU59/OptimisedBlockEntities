package fr.madu59.obe.client.util.blockentity;

import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
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
        return ResourceUtil.entityTextureFormatter(CopperGolemOxidationLevels.getOxidationLevel(oxydationLevel).texture());
    }

    public static ModelLayerLocation getCopperGolemStatueModelLayerLocation(BlockState state){
        return switch(state.getValue(CopperGolemStatueBlock.POSE)) {
            case CopperGolemStatueBlock.Pose.STANDING-> ModelLayers.COPPER_GOLEM;
            case CopperGolemStatueBlock.Pose.RUNNING-> ModelLayers.COPPER_GOLEM_RUNNING;
            case CopperGolemStatueBlock.Pose.SITTING-> ModelLayers.COPPER_GOLEM_SITTING;
            case CopperGolemStatueBlock.Pose.STAR-> ModelLayers.COPPER_GOLEM_STAR;
        };
    }
}

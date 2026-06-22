package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockUtil {
    public static Identifier getSkullBlockMaterial(BlockState state) {
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        Identifier id = SkullBlockRenderer.SKIN_BY_TYPE.get(type);
        return ResourceUtil.entityTextureFormatter(id);
    }

    public static ModelLayerLocation getSkullBlockModelLayerLocation(BlockState state){
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        if (type instanceof SkullBlock.Types vanillaType) {
            return switch (vanillaType) {
                case SKELETON -> ModelLayers.SKELETON_SKULL;
                case WITHER_SKELETON -> ModelLayers.WITHER_SKELETON_SKULL;
                case PLAYER -> ModelLayers.PLAYER_HEAD;
                case ZOMBIE -> ModelLayers.ZOMBIE_HEAD;
                case CREEPER -> ModelLayers.CREEPER_HEAD;
                case DRAGON -> ModelLayers.DRAGON_SKULL;
                case PIGLIN -> ModelLayers.PIGLIN_HEAD;
            };
        }
        else return null;
    }

    public static void transformSkullBlock(BlockState state, PoseStack poseStack){
        if (state.getBlock() instanceof WallSkullBlock) {
            Direction facing = state.getValue(WallSkullBlock.FACING);
            poseStack.mulPose(SkullBlockRenderer.TRANSFORMATIONS.wallTransformation(facing));
        } else {
            poseStack.mulPose(SkullBlockRenderer.TRANSFORMATIONS.freeTransformations(state.getValue(SkullBlock.ROTATION)));
        }
    }
}

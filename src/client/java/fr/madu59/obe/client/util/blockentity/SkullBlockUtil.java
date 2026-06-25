package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class SkullBlockUtil {
    public static ResourceLocation getSkullBlockMaterial(BlockState state) {
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        ResourceLocation id = SkullBlockRenderer.SKIN_BY_TYPE.get(type);
        return ResourceUtil.entityTextureFormatter(id);
    }

    public static ModelLayerLocation getSkullBlockModelLayerLocation(BlockState state){
        if(state.getBlock() instanceof AbstractSkullBlock block){
            SkullBlock.Type type = block.getType();
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
        else return null;
    }

    public static void transformSkullBlock(BlockState state, PoseStack poseStack){
        poseStack.pushPose();
        boolean bl = state.getBlock() instanceof WallSkullBlock;
        Direction direction = bl ? (Direction)state.getValue(WallSkullBlock.FACING) : null;
        if (direction == null) {
            poseStack.translate(0.5F, 0.0F, 0.5F);
        } else {
            float h = 0.25F;
            poseStack.translate(0.5F - (float)direction.getStepX() * 0.25F, 0.25F, 0.5F - (float)direction.getStepZ() * 0.25F);
        }

        int i = bl ? RotationSegment.convertToSegment(direction.getOpposite()) : state.getValue(SkullBlock.ROTATION);
        poseStack.mulPose(Axis.YP.rotationDegrees(-RotationSegment.convertToDegrees(i)));

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        if(type == SkullBlock.Types.DRAGON){
            poseStack.translate(0.0F, -0.374375F, 0.0F);
            poseStack.scale(0.75F, 0.75F, 0.75F);
        }
    }
}

package fr.madu59.obe.client.util.blockentity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.resources.ResourceUtil;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class SkullBlockUtil {

    public static Map<String, ResourceLocation> BUILT_IN_TEXTURES = new HashMap<>();

    public static ResourceLocation getMaterial(BlockState state) {
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        ResourceLocation id = SkullBlockRenderer.SKIN_BY_TYPE.get(type);
        return ResourceUtil.entityTextureFormatter(id);
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
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

    public static void transform(BlockState state, PoseStack poseStack){
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

    public static void transform(BlockState state, BlockEntity be, PoseStack poseStack){
        transform(state, poseStack);
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state, BlockEntity be){
        return getModelLayerLocation(state);
    }

    public static ResourceLocation getMaterial(BlockState state, BlockEntity be) {
        if (be instanceof SkullBlockEntity skullBe) {
            return getBuiltInTexture(skullBe);
        }
        return getMaterial(state);
    }

    public static boolean hasBuiltInTexture(BlockEntity be) {
        if(!SettingsManager.CUSTOM_SKULLS.getValue()) return false;
        String textureValue = getBuiltInTextureValue(be);
        if(textureValue == null) {
            ((BlockEntityExt) be).hasSpecialRenderer(false);
            return false;
        }
        return SkullBlockUtil.BUILT_IN_TEXTURES.containsKey(textureValue);
    }

    public static ResourceLocation getBuiltInTexture(BlockEntity be) {
        return SkullBlockUtil.BUILT_IN_TEXTURES.get(getBuiltInTextureValue(be));
    }

    public static String getBuiltInTextureValue(BlockEntity be) {
        if(be instanceof SkullBlockEntity skullBe) {
            if(skullBe.getOwnerProfile() == null || !SettingsManager.CUSTOM_SKULLS.getValue()) return null;
            Collection<Property> properties = skullBe.getOwnerProfile().gameProfile().getProperties().get("textures");
            String textureValue = properties.stream()
            .map(Property::value)
            .findFirst()
            .orElse(null);
            return textureValue;
        }
        else return null;
    }
}

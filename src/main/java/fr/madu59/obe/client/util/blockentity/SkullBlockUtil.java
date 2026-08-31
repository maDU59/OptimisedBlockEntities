package fr.madu59.obe.client.util.blockentity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.resources.ResourceUtil;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SkullBlockUtil {

    public static Map<String, Identifier> BUILT_IN_TEXTURES = new HashMap<>();

    public static Identifier getMaterial(BlockState state) {
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        Identifier id = SkullBlockRenderer.SKIN_BY_TYPE.get(type);
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
        if (state.getBlock() instanceof WallSkullBlock) {
            Direction facing = state.getValue(WallSkullBlock.FACING);
            poseStack.mulPose(SkullBlockRenderer.TRANSFORMATIONS.wallTransformation(facing));
        } else {
            poseStack.mulPose(SkullBlockRenderer.TRANSFORMATIONS.freeTransformations(state.getValue(SkullBlock.ROTATION)));
        }
    }

    public static void transform(BlockState state, BlockEntity be, PoseStack poseStack){
        transform(state, poseStack);
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state, BlockEntity be){
        return getModelLayerLocation(state);
    }

    public static Identifier getMaterial(BlockState state, BlockEntity be) {
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

    public static Identifier getBuiltInTexture(BlockEntity be) {
        return SkullBlockUtil.BUILT_IN_TEXTURES.get(getBuiltInTextureValue(be));
    }

    public static String getBuiltInTextureValue(BlockEntity be) {
        if(be instanceof SkullBlockEntity skullBe) {
            if(skullBe.getOwnerProfile() == null || !SettingsManager.CUSTOM_SKULLS.getValue()) return null;
            Collection<Property> properties = skullBe.getOwnerProfile().partialProfile().properties().get("textures");
            String textureValue = properties.stream()
            .map(Property::value)
            .findFirst()
            .orElse(null);
            return textureValue;
        }
        else return null;
    }
}

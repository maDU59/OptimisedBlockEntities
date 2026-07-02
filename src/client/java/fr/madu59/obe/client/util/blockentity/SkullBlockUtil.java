package fr.madu59.obe.client.util.blockentity;

import java.util.Collection;
import java.util.Map;

import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.util.ResourceUtil;
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

    public static Map<String, Identifier> BUILT_IN_TEXTURES = Map.ofEntries(
        Map.entry("eyJ0aW1lc3RhbXAiOjE1ODY2NjcxNjgzNzksInByb2ZpbGVJZCI6ImJlY2RkYjI4YTJjODQ5YjRhOWIwOTIyYTU4MDUxNDIwIiwicHJvZmlsZU5hbWUiOiJTdFR2Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTllYTEyMGJkODNkMGM4MWEzYzQ2MjdmNWJjZTFiMTJmYjAzYmNiNTc3NzljNjNkY2M3N2UzZjRhZThhNzkzIn19fQ==", Identifier.tryParse("obe:block/skull/fairy_soul"))
    );

    public static Identifier getSkullBlockMaterial(BlockState state) {
        SkullBlock.Type type = ((AbstractSkullBlock)state.getBlock()).getType();
        Identifier id = SkullBlockRenderer.SKIN_BY_TYPE.get(type);
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
        if (state.getBlock() instanceof WallSkullBlock) {
            Direction facing = state.getValue(WallSkullBlock.FACING);
            poseStack.mulPose(SkullBlockRenderer.TRANSFORMATIONS.wallTransformation(facing));
        } else {
            poseStack.mulPose(SkullBlockRenderer.TRANSFORMATIONS.freeTransformations(state.getValue(SkullBlock.ROTATION)));
        }
    }

    public static Identifier getSkullBlockMaterial(BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof SkullBlockEntity skullBe) {
            Identifier builtInTexture = getBuiltInTexture(skullBe);
            if (builtInTexture != null) {
                return builtInTexture;
            }
        }
        return getSkullBlockMaterial(state);
    }

    public static ModelLayerLocation getSkullBlockModelLayerLocation(BlockState state, BlockEntity blockEntity) {
        return getSkullBlockModelLayerLocation(state);
    }

    public static void transformSkullBlock(BlockState state, BlockEntity blockEntity, PoseStack poseStack) {
        transformSkullBlock(state, poseStack);
    }

    public static boolean hasBuiltInTexture(BlockEntity be) {
        if(SettingsManager.CUSTOM_SKULLS.getValue() == false) return false;
        String textureValue = getBuiltInTextureValue(be);
        if(textureValue == null) return false;
        return SkullBlockUtil.BUILT_IN_TEXTURES.containsKey(textureValue);
    }

    public static Identifier getBuiltInTexture(BlockEntity be) {
        return SkullBlockUtil.BUILT_IN_TEXTURES.get(getBuiltInTextureValue(be));
    }

    public static String getBuiltInTextureValue(BlockEntity be) {
        if(be instanceof SkullBlockEntity skullBe) {
            if(skullBe.getOwnerProfile() == null) return null;
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

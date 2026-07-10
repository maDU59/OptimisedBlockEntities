package fr.madu59.obe.client.util.blockentity;

import java.util.Collection;
import java.util.Map;

import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.util.ResourceUtil;
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

    public static Map<String, ResourceLocation> BUILT_IN_TEXTURES = Map.ofEntries(
        Map.entry("eyJ0aW1lc3RhbXAiOjE1ODY2NjcxNjgzNzksInByb2ZpbGVJZCI6ImJlY2RkYjI4YTJjODQ5YjRhOWIwOTIyYTU4MDUxNDIwIiwicHJvZmlsZU5hbWUiOiJTdFR2Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yOTllYTEyMGJkODNkMGM4MWEzYzQ2MjdmNWJjZTFiMTJmYjAzYmNiNTc3NzljNjNkY2M3N2UzZjRhZThhNzkzIn19fQ==", ResourceLocation.tryParse("obe:block/skull/fairy_soul")),
        Map.entry("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjAyMjZkNGMxZDMwZmJlYmVjYWU5MzlkYTkwMDYwM2U0Y2QwZmVkODU5MmExYmIzZTExZjlhYzkyMzkxYTQ1YSJ9fX0=", ResourceLocation.tryParse("obe:block/skull/multicolored_potion")),
        Map.entry("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjhkMDlkYmIyZWVhNTAyZjE4YTg0OTY0OTFiM2ZjYzA3Y2Q3N2I1OWUzNmY0OGZiYTMyYWEzNzMzNTNjZDA1MiJ9fX0=", ResourceLocation.tryParse("obe:block/skull/skeleton")),
        Map.entry("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM3UxZTFlODJiZjQzNzhhN2IxMzkyMjliNTYxYzhmMDExOWJmNTY1NTEyODAxNGQzYzU0MzlkODk4MzAzZjFiMCJ9fX0=", ResourceLocation.tryParse("obe:block/skull/skeleton_2")),
        Map.entry("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzBlMGFiZDJlNDQ2YzI1MWI0YzA0YTQwMjU2ZDBkOGIzNjdiZGQ3NTdiZDcyZjI1MGI2YmI3YjkwOTg3NjQzIn19fQ==", ResourceLocation.tryParse("obe:block/skull/skeleton_3")),
        Map.entry("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV₀L3RleHR1cmUvZmE2ZGY2Y2E5YTQ1NGYxODNjMzUxM2RjODUxNTkwMTYzY2M5YTZkODc3MzY5MzE1M2VmZjgxODlhZjYyMDRmIn19fQ==", ResourceLocation.tryParse("obe:block/skull/empty_potion")),
        Map.entry("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTgzYjMwZTlkMTM1YjA1MTkwZWVhMmMzYWM2MWUyYWI1NWEyZDgxZTFhNThkYmIyNjk4M2ExNDA4MjY2NCJ9fX0=", ResourceLocation.tryParse("obe:block/skull/spider_egg")),
        Map.entry("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE2MzE0ZWFjMzQ0MTZjZTEwYWIyMmMyZTFjNGRjYjQ3MmEzZmViOThkNGUwNGQzZmJiYjg1YTlhNDcxYjE4In19fQ==", ResourceLocation.tryParse("obe:block/skull/rotten_skeleton")),
        Map.entry("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFlMzg1NWY5NTJjZDRhMDNjMTQ4YTk0NmUzZjgxMmE1OTU1YWQzNWNiY2I1MjYyN2VhNGFjZDQ3ZDMwODEifX19", ResourceLocation.tryParse("obe:block/skull/skeleton_4")),
        Map.entry("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2JjYmJmOTRkNjAzNzQzYTFlNzE0NzAyNmUxYzEyNDBiZDk4ZmU4N2NjNGVmMDRkY2FiNTFhMzFjMzA5MTRmZCJ9fX0=", ResourceLocation.tryParse("obe:block/skull/skeleton_dark"))
    );

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
    }

    public static ResourceLocation getSkullBlockMaterial(BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof SkullBlockEntity skullBe) {
            ResourceLocation builtInTexture = getBuiltInTexture(skullBe);
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

    public static ResourceLocation getBuiltInTexture(BlockEntity be) {
        return SkullBlockUtil.BUILT_IN_TEXTURES.get(getBuiltInTextureValue(be));
    }

    public static String getBuiltInTextureValue(BlockEntity be) {
        if(be instanceof SkullBlockEntity skullBe) {
            if(skullBe.getOwnerProfile() == null) return null;
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

package fr.madu59.obe.client.compat.sophisticatedstorage.shulker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.api.model.SpecialBakedModelContext;
import fr.madu59.obe.client.api.model.SpecialBakedModelProvider;
import fr.madu59.obe.client.model.LayeredBakedModel;
import fr.madu59.obe.client.model.ModelPartQuadBaker;
import fr.madu59.obe.client.resources.ResourceUtil;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedStorageDiagnostics;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.client.render.ShulkerBoxRenderer;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;

public final class SophisticatedShulkerModelProvider implements SpecialBakedModelProvider<SophisticatedShulkerAppearance> {
    private final BooleanSupplier ambientOcclusion;

    public SophisticatedShulkerModelProvider(BooleanSupplier ambientOcclusion) {
        this.ambientOcclusion = ambientOcclusion;
    }

    @Override
    public SophisticatedShulkerAppearance resolveAppearance(SpecialBakedModelContext context) {
        if (!(context.blockEntity() instanceof ShulkerBoxBlockEntity shulker)) {
            throw new IllegalArgumentException("Expected Sophisticated Storage shulker block entity");
        }
        Direction facing = context.state().getValue(ShulkerBoxBlock.FACING);
        StorageWrapper wrapper = shulker.getStorageWrapper();
        int main = wrapper.getMainColor();
        int accent = wrapper.getAccentColor();
        boolean hasMain = main != -1;
        boolean hasAccent = accent != -1;
        List<ShulkerMaterialPass> passes = new ArrayList<>();
        for (ShulkerMaterialPass.Kind kind : ShulkerPassSelection.select(hasMain, hasAccent, shulker.shouldShowTier())) {
            Material material = switch (kind) {
                case BASE_SHELL -> ShulkerBoxRenderer.NO_TINT_MATERIAL;
                case MAIN_SHELL -> ShulkerBoxRenderer.TINTABLE_MAIN_MATERIAL;
                case ACCENT_SHELL -> ShulkerBoxRenderer.TINTABLE_ACCENT_MATERIAL;
                case TIER_SHELL -> tierMaterial(context.state().getBlock());
            };
            int color = switch (kind) {
                case MAIN_SHELL -> 0xFF000000 | main;
                case ACCENT_SHELL -> 0xFF000000 | accent;
                default -> 0xFFFFFFFF;
            };
            passes.add(new ShulkerMaterialPass(kind, material.texture(), color));
        }
        SophisticatedStorageDiagnostics.resolved();
        return new SophisticatedShulkerAppearance(
                BuiltInRegistries.BLOCK.getKey(context.state().getBlock()), facing, passes, ambientOcclusion.getAsBoolean());
    }

    @Override
    public BakedModel bake(SophisticatedShulkerAppearance appearance, SpecialBakedModelContext context) {
        ModelPart root = ShulkerModel.createBodyLayer().bakeRoot();
        ModelPart lid = root.getChild("lid");
        ModelPart base = root.getChild("base");
        lid.setPos(0.0F, 24.0F, 0.0F);
        lid.yRot = 0.0F;

        PoseStack basePose = new PoseStack();
        basePose.translate(0.5D, 0.5D, 0.5D);
        basePose.scale(0.9995F, 0.9995F, 0.9995F);
        basePose.mulPose(appearance.facing().getRotation());
        basePose.scale(1.0F, -1.0F, -1.0F);
        basePose.translate(0.0D, -1.0D, 0.0D);

        List<LayeredBakedModel.Layer> layers = new ArrayList<>();
        for (ShulkerMaterialPass pass : appearance.passes()) {
            TextureAtlasSprite sprite = ResourceUtil.getSprite(pass.texture());
            if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
                SophisticatedStorageDiagnostics.missingSprite();
                throw new IllegalStateException("Missing stitched shulker sprite " + pass.texture());
            }
            List<BakedQuad> quads = new ArrayList<>();
            // ShulkerModel.parts() is exactly base + lid; the separate entity head is not rendered here.
            ModelPartQuadBaker.append(quads, base, basePose, sprite, pass.color(), true, appearance.ambientOcclusion());
            ModelPartQuadBaker.append(quads, lid, basePose, sprite, pass.color(), true, appearance.ambientOcclusion());
            layers.add(new LayeredBakedModel.Layer(pass.texture(), quads));
        }
        SophisticatedStorageDiagnostics.baked();
        return new LayeredBakedModel(layers, context.particleSprite(), appearance.ambientOcclusion());
    }

    private static Material tierMaterial(net.minecraft.world.level.block.Block block) {
        if (block == ModBlocks.COPPER_SHULKER_BOX.get()) return ShulkerBoxRenderer.COPPER_TIER_MATERIAL;
        if (block == ModBlocks.IRON_SHULKER_BOX.get()) return ShulkerBoxRenderer.IRON_TIER_MATERIAL;
        if (block == ModBlocks.GOLD_SHULKER_BOX.get()) return ShulkerBoxRenderer.GOLD_TIER_MATERIAL;
        if (block == ModBlocks.DIAMOND_SHULKER_BOX.get()) return ShulkerBoxRenderer.DIAMOND_TIER_MATERIAL;
        if (block == ModBlocks.NETHERITE_SHULKER_BOX.get()) return ShulkerBoxRenderer.NETHERITE_TIER_MATERIAL;
        return ShulkerBoxRenderer.BASE_TIER_MATERIAL;
    }
}

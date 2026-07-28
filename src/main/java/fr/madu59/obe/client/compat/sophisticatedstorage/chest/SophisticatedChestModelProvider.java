package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.api.model.SpecialBakedModelContext;
import fr.madu59.obe.client.api.model.SpecialBakedModelProvider;
import fr.madu59.obe.client.model.LayeredBakedModel;
import fr.madu59.obe.client.model.ModelPartQuadBaker;
import fr.madu59.obe.client.resources.ResourceUtil;
import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedStorageDiagnostics;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.p3pp3rf1y.sophisticatedstorage.client.render.ChestRenderer;

/** Bakes the target renderer's closed, static shell while leaving overlays in its BER. */
public final class SophisticatedChestModelProvider implements SpecialBakedModelProvider<SophisticatedChestAppearance> {
    private final SophisticatedChestAppearanceResolver resolver;

    public SophisticatedChestModelProvider(SophisticatedChestAppearanceResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public SophisticatedChestAppearance resolveAppearance(SpecialBakedModelContext context) {
        SophisticatedChestAppearance appearance = resolver.resolve(context);
        SophisticatedStorageDiagnostics.resolved();
        return appearance;
    }

    @Override
    public BakedModel bake(SophisticatedChestAppearance appearance, SpecialBakedModelContext context) {
        ModelPart root = switch (appearance.chestType()) {
            case LEFT -> ChestRenderer.createDoubleBodyLeftLayer().bakeRoot();
            case RIGHT -> ChestRenderer.createDoubleBodyRightLayer().bakeRoot();
            case SINGLE -> ChestRenderer.createSingleBodyLayer(true).bakeRoot();
        };
        ModelPart lid = root.getChild("lid");
        ModelPart bottom = root.getChild("bottom");
        ModelPart lock = root.getChild("lock");

        PoseStack basePose = new PoseStack();
        basePose.translate(0.5D, 0.5D, 0.5D);
        basePose.mulPose(Axis.YP.rotationDegrees(-appearance.facing().toYRot()));
        basePose.translate(-0.5D, -0.5D, -0.5D);

        List<LayeredBakedModel.Layer> layers = new ArrayList<>(appearance.passes().size());
        for (ChestMaterialPass pass : appearance.passes()) {
            TextureAtlasSprite sprite = ResourceUtil.getSprite(pass.texture());
            if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
                SophisticatedStorageDiagnostics.missingSprite();
                throw new IllegalStateException("Missing stitched chest sprite " + pass.texture());
            }
            PoseStack pose = copyPose(basePose);
            if (pass.kind() == ChestMaterialPass.Kind.PACKED_SHELL) {
                pose.translate(-0.005D, -0.005D, -0.005D);
                pose.scale(1.01F, 1.01F, 1.01F);
            }
            List<BakedQuad> quads = new ArrayList<>();
            if (pass.kind() == ChestMaterialPass.Kind.LATCH) {
                ModelPartQuadBaker.append(quads, lock, pose, sprite, pass.color(), true, appearance.ambientOcclusion());
            } else {
                ModelPartQuadBaker.append(quads, lid, pose, sprite, pass.color(), true, appearance.ambientOcclusion());
                ModelPartQuadBaker.append(quads, bottom, pose, sprite, pass.color(), true, appearance.ambientOcclusion());
            }
            layers.add(new LayeredBakedModel.Layer(pass.texture(), quads));
        }
        SophisticatedStorageDiagnostics.baked();
        return new LayeredBakedModel(layers, context.particleSprite(), appearance.ambientOcclusion());
    }

    private static PoseStack copyPose(PoseStack source) {
        PoseStack copy = new PoseStack();
        copy.last().pose().set(source.last().pose());
        copy.last().normal().set(source.last().normal());
        return copy;
    }
}

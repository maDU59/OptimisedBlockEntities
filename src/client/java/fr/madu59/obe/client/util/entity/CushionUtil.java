package fr.madu59.obe.client.util.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.renderer.entity.MeshableEntityTracker.CushionSnapshot;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CushionRenderer;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;

public class CushionUtil{
    public static ModelLayerLocation getModelLayerLocation(){
        return ModelLayers.CUSHION;
    }

    public static Identifier getMaterial(DyeColor color){
        return ResourceUtil.entityTextureFormatter(CushionRenderer.TEXTURES_BY_COLOR.get(color));
    }

    public static BlockEntityStateModel getModel(CushionSnapshot snapshot){
        PoseStack poseStack = new PoseStack();
        poseStack.translate(snapshot.pos().subtract(Vec3.atLowerCornerOf(snapshot.blockPos())));
        poseStack.mulPose(Axis.YP.rotationDegrees(snapshot.rotation().y));
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.translate(0.0, -0.25, 0.0);
        return new BlockEntityStateModel(getModelLayerLocation(), getMaterial(snapshot.color()), poseStack, SettingsManager.OPTIMISED_CUSHIONS.getValue(), null, null);
    }
}
package fr.madu59.obe.client.util.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.renderer.entity.MeshableEntityTracker.CushionSnapshot;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CushionRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

public class CushionUtil{
    private static Map<CacheKey, BlockEntityStateModel> MODEL_CACHE = new ConcurrentHashMap<>();

    public static ModelLayerLocation getModelLayerLocation(){
        return ModelLayers.CUSHION;
    }

    public static Identifier getMaterial(DyeColor color){
        return ResourceUtil.entityTextureFormatter(CushionRenderer.TEXTURES_BY_COLOR.get(color));
    }

    public static BlockEntityStateModel getModel(CushionSnapshot snapshot){

        double relativeX = snapshot.pos().x - snapshot.blockPos().getX();
        double relativeY = snapshot.pos().y - snapshot.blockPos().getY();
        double relativeZ = snapshot.pos().z - snapshot.blockPos().getZ();

        return MODEL_CACHE.computeIfAbsent(new CacheKey(relativeX, relativeY, relativeZ, snapshot.rotation().y, snapshot.color().getId()), (k) -> {
            PoseStack poseStack = new PoseStack();
            poseStack.translate(k.relativeX, k.relativeY, k.relativeZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(k.yRot));
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            poseStack.translate(0.0, -0.25, 0.0);
            return new BlockEntityStateModel(getModelLayerLocation(), getMaterial(snapshot.color()), poseStack, SettingsManager.CUSHION_AMBIENT_OCCLUSION.getValue(), null, null);
        });
    }

    public static void clearCache(){
        MODEL_CACHE.clear();
    }

    private static record CacheKey(double relativeX, double relativeY, double relativeZ, float yRot, int colorId){}
}
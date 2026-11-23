package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.SpecialModelBuilderGetter;
import fr.madu59.obe.client.registry.TransformationGetter;
import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DecoratedPotUtil {
    public static ResourceLocation getMaterial(BlockState state){
        return Sheets.DECORATED_POT_BASE.texture();
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_BASE;
    }

    public static ModelLayerLocation getSideModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_SIDES;
    }

    public static void transform(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        poseStack.pushPose();
        poseStack.translate((double)0.5F, (double)0.0F, (double)0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        poseStack.translate((double)-0.5F, (double)0.0F, (double)-0.5F);
    }

    public static BakedModel getModel(BlockState state, BakedModel originalModel){
        return getModel(state, originalModel, null);
    }

    public static BakedModel getModel(BlockState state, BakedModel originalModel, @Nullable BlockEntity blockEntity) {
        String group = "decorated_pot";

        PoseStack poseStack = new PoseStack();

        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, group);
        if(layerLocation == null) return null;

        ResourceLocation material = MaterialGetter.getMaterial(state, group);
        if(material == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, group);

        boolean ao = BlockEntityModelsManager.getAmbientOcclusion(group);
        TextureAtlasSprite particleMaterial = originalModel.getParticleIcon();

        BakedModel model = new BlockEntityStateModel(layerLocation, material, poseStack, ao, state, particleMaterial);

        layerLocation = DecoratedPotUtil.getSideModelLayerLocation(state);
        PotDecorations decorations = (blockEntity instanceof DecoratedPotBlockEntity decoratedPotBlockEntity)? decoratedPotBlockEntity.getDecorations() : PotDecorations.EMPTY;

        model = appendRotatedSideModel(model, state, poseStack, ao, particleMaterial, layerLocation, decorations.front(), 0.0F);
        model = appendRotatedSideModel(model, state, poseStack, ao, particleMaterial, layerLocation, decorations.right(), (float) (Math.PI / 2));
        model = appendRotatedSideModel(model, state, poseStack, ao, particleMaterial, layerLocation, decorations.back(),  (float) Math.PI);
        model = appendRotatedSideModel(model, state, poseStack, ao, particleMaterial, layerLocation, decorations.left(),  (float) (-Math.PI / 2));

        if(state.getRenderShape() == RenderShape.MODEL) model = new CompositeBlockStateModel(model, originalModel);

        return model;
    }

    private static BakedModel appendRotatedSideModel(BakedModel model, BlockState state, PoseStack poseStack, boolean ao, TextureAtlasSprite particleMaterial, ModelLayerLocation frontSideLayer, Optional<Item> sherdItem, float rot) {
        poseStack.pushPose();

        if (rot != 0.0F)  poseStack.rotateAround(Axis.YP.rotation(rot), 0.5F, 0.5F, 0.5F);

        BakedModel sideModel = new BlockEntityStateModel(frontSideLayer, resolveSideTexture(sherdItem), poseStack, ao, state, particleMaterial);

        poseStack.popPose();

        return new CompositeBlockStateModel(model, sideModel);
    }

    private static ResourceLocation resolveSideTexture(Optional<Item> item) {
        if (item.isPresent()) {
            Material result = Sheets.getDecoratedPotMaterial(DecoratedPotPatterns.getPatternFromItem(item.get()));
            if (result != null) {
                return result.texture();
            }
        }

        return Sheets.DECORATED_POT_SIDE.texture();
    }

    private static PotDecorations getCacheKey(BlockEntity be){
        if(be instanceof DecoratedPotBlockEntity decoratedPotBlockEntity){
            return decoratedPotBlockEntity.getDecorations();
        }
        return PotDecorations.EMPTY;
    }

    public static class PotWithDecorationsBuilder implements SpecialModelBuilderGetter.SpecialModelBuilder{

        @Override
        public @Nullable BakedModel build(BlockState state, BlockEntity be, BakedModel originalModel) {
            return DecoratedPotUtil.getModel(state, originalModel, be);
        }

        @Override
        public Object getCacheKey(BlockEntity be) {
            return DecoratedPotUtil.getCacheKey(be);
        }
    }
}

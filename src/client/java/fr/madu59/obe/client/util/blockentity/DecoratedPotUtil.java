package fr.madu59.obe.client.util.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.ModelLayerLocationGetter;
import fr.madu59.obe.client.registry.TransformationGetter;
import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;

public class DecoratedPotUtil {
    public static Identifier getMaterial(BlockState state){
        return Sheets.DECORATED_POT_BASE.texture();
    }

    public static ModelLayerLocation getModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_BASE;
    }

    public static ModelLayerLocation getSideModelLayerLocation(BlockState state){
        return ModelLayers.DECORATED_POT_SIDES;
    }

    public static void transform(BlockState state, PoseStack poseStack){
        Direction facing = state.getValue(DecoratedPotBlock.HORIZONTAL_FACING);
        poseStack.mulPose(DecoratedPotRenderer.modelTransformation(facing));
    }

    public static BlockStateModel getModel(BlockState state, BlockStateModel originalModel){
        BlockStateModel model;

        String group = "decorated_pot";

        PoseStack poseStack = new PoseStack();

        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, group);
        if(layerLocation == null) return null;

        Identifier material = MaterialGetter.getMaterial(state, group);
        if(material == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, group);

        model = new BlockEntityStateModel(layerLocation, material, poseStack, BlockEntityModelsManager.getAmbientOcclusion(group), state, originalModel.particleMaterial());

        layerLocation = DecoratedPotUtil.getSideModelLayerLocation(state);

        BlockStateModel sideModel = new BlockEntityStateModel(layerLocation, Sheets.getDecoratedPotSprite(DecoratedPotPatterns.BLANK).texture(), poseStack, BlockEntityModelsManager.getAmbientOcclusion(group), state, originalModel.particleMaterial());

        model = new CompositeBlockStateModel(model, sideModel);

        if(state.getRenderShape() == RenderShape.MODEL) model = new CompositeBlockStateModel(model, originalModel);

        return model;
    }
}

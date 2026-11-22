package fr.madu59.obe.client.util.blockentity;

import com.google.common.collect.ImmutableMap;
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
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class DecoratedPotUtil {

    private static final Map<ResourceKey<Item>, SpriteId> DECORATED_POT_SPRITES = Util.make(() -> {
        ImmutableMap.Builder<ResourceKey<Item>, SpriteId> builder = ImmutableMap.builder();
        DecoratedPotPatterns.itemToPatternMappings((itemId, patternId) -> {
            Holder.Reference<DecoratedPotPattern> pattern = BuiltInRegistries.DECORATED_POT_PATTERN.getOrThrow(patternId);
            builder.put(itemId, Sheets.DECORATED_POT_MAPPER.apply(pattern.value().assetId()));
        });
        return builder.buildOrThrow();
    });

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
        return getModel(state, originalModel, null);
    }

    public static BlockStateModel getModel(BlockState state, BlockStateModel originalModel, @Nullable BlockEntity blockEntity) {
        String group = "decorated_pot";

        PoseStack poseStack = new PoseStack();

        ModelLayerLocation layerLocation = ModelLayerLocationGetter.getModelLayerLocation(state, group);
        if(layerLocation == null) return null;

        Identifier material = MaterialGetter.getMaterial(state, group);
        if(material == null) return null;

        TransformationGetter.applyTransformation(state, poseStack, group);

        boolean ao = BlockEntityModelsManager.getAmbientOcclusion(group);
        Material.Baked particleMaterial = originalModel.particleMaterial();

        BlockStateModel model = new BlockEntityStateModel(layerLocation, material, poseStack, ao, state, particleMaterial);

        layerLocation = DecoratedPotUtil.getSideModelLayerLocation(state);
        PotDecorations decorations = (blockEntity instanceof DecoratedPotBlockEntity decoratedPotBlockEntity)? decoratedPotBlockEntity.getDecorations() : PotDecorations.EMPTY;

        model = appendRotatedSideModel(model, state, poseStack, ao, particleMaterial, layerLocation, decorations.front(), 0.0F);
        model = appendRotatedSideModel(model, state, poseStack, ao, particleMaterial, layerLocation, decorations.right(), (float) (Math.PI / 2));
        model = appendRotatedSideModel(model, state, poseStack, ao, particleMaterial, layerLocation, decorations.back(),  (float) Math.PI);
        model = appendRotatedSideModel(model, state, poseStack, ao, particleMaterial, layerLocation, decorations.left(),  (float) (-Math.PI / 2));

        if(state.getRenderShape() == RenderShape.MODEL) model = new CompositeBlockStateModel(model, originalModel);

        return model;
    }

    private static BlockStateModel appendRotatedSideModel(BlockStateModel model, BlockState state, PoseStack poseStack, boolean ao, Material.Baked particleMaterial, ModelLayerLocation frontSideLayer, Optional<Item> sherdItem, float rot) {
        poseStack.pushPose();

        if (rot != 0.0F)  poseStack.rotateAround(Axis.YP.rotation(rot), 0.5F, 0.5F, 0.5F);

        BlockStateModel sideModel = new BlockEntityStateModel(frontSideLayer, resolveSideTexture(sherdItem), poseStack, ao, state, particleMaterial);

        poseStack.popPose();

        return new CompositeBlockStateModel(model, sideModel);
    }

    private static Identifier resolveSideTexture(Optional<Item> item) {
        if (item.isPresent()) {
            Identifier result = DECORATED_POT_SPRITES.get((item.get()).builtInRegistryHolder().key()).texture();
            if (result != null) {
                return result;
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
        public @Nullable BlockStateModel build(BlockState state, BlockEntity be, BlockStateModel originalModel) {
            return DecoratedPotUtil.getModel(state, originalModel, be);
        }

        @Override
        public Object getCacheKey(BlockEntity be) {
            return DecoratedPotUtil.getCacheKey(be);
        }
    }
}

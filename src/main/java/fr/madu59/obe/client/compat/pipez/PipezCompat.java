package fr.madu59.obe.client.compat.pipez;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.pipez.ModelRegistry;
import de.maxhenkel.pipez.blocks.tileentity.*;
import de.maxhenkel.pipez.blocks.tileentity.render.PipeRenderer;
import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.SpecialModelBuilderGetter;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;

public class PipezCompat {
    public static void init() {
        RegistryApi.registerGroup("pipez_pipes");
        RegistryApi.registerBlockEntityType("pipez_pipes", ModTileEntities.ITEM_PIPE.get(), ModTileEntities.FLUID_PIPE.get(), ModTileEntities.GAS_PIPE.get(), ModTileEntities.UNIVERSAL_PIPE.get(), ModTileEntities.ENERGY_PIPE.get());
        RegistryApi.registerSpecialModelBuilder("pipez_pipes", new PipezSpecialModelBuilder());
    }

    private static Quaternionf getRotation(Direction direction) {
        Quaternionf q = new Quaternionf();
        switch (direction) {
            case NORTH:
                return q;
            case SOUTH:
                q.mul(Axis.YP.rotationDegrees(180F));
                return q;
            case WEST:
                q.mul(Axis.YP.rotationDegrees(90F));
                return q;
            case EAST:
                q.mul(Axis.YP.rotationDegrees(270F));
                return q;
            case UP:
                q.mul(Axis.XP.rotationDegrees(90F));
                return q;
            case DOWN:
            default:
                q.mul(Axis.XP.rotationDegrees(270F));
                return q;
        }
    }

    public static class PipezSpecialModelBuilder implements SpecialModelBuilderGetter.SpecialModelBuilder {
        RandomSource random = RandomSource.create();

        @Override
        public @Nullable BakedModel build(BlockState state, BlockEntity entity, BakedModel originalModel){
            PoseStack poseStack = new PoseStack();
            if(entity instanceof PipeTileEntity pipeBe){
                BakedModel model;
                if(pipeBe instanceof EnergyPipeTileEntity){
                    model = ModelRegistry.Model.ENERGY_PIPE_EXTRACT.getCachedModel().get();
                }
                else if(pipeBe instanceof GasPipeTileEntity){
                    model = ModelRegistry.Model.GAS_PIPE_EXTRACT.getCachedModel().get();
                }
                else if(pipeBe instanceof ItemPipeTileEntity){
                    model = ModelRegistry.Model.ITEM_PIPE_EXTRACT.getCachedModel().get();
                }
                else if(pipeBe instanceof FluidPipeTileEntity){
                    model = ModelRegistry.Model.FLUID_PIPE_EXTRACT.getCachedModel().get();
                }
                else{
                    model = ModelRegistry.Model.UNIVERSAL_PIPE_EXTRACT.getCachedModel().get();
                }
                List<BakedQuad> quadList = model.getQuads(state, null, random, ModelData.EMPTY, RenderType.solid());
                for (Direction side : Direction.values()) {
                    if(pipeBe.isExtracting(side)){
                        poseStack.pushPose();
                        poseStack.translate(side.getStepX() * 0.001D, side.getStepY() * 0.001D, side.getStepZ() * 0.001D);
                        poseStack.translate(0.5D, 0.5D, 0.5D);
                        poseStack.mulPose(getRotation(side));
                        poseStack.translate(-0.5D, -0.5D, -0.5D);
                        BlockEntityStateModel extractingModel = new BlockEntityStateModel(quadList, poseStack, true, pipeBe.getBlockState(), null);
                        originalModel = new CompositeBlockStateModel(originalModel, extractingModel);
                        poseStack.popPose();
                    }
                }
            }
            return originalModel;
        }

        @Override
        public Object getCacheKey(BlockEntity entity){
            if(entity instanceof PipeTileEntity pipeBe){
                int key = 0;
                for (Direction side : Direction.values()) {
                    key += pipeBe.isExtracting(side) ? 1 << side.ordinal() : 0;
                }
                return key;
            }
            else{
                return -1;
            }
        }
    }
}
package fr.madu59.obe.client.compat.pipez;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.maxhenkel.pipez.ModelRegistry;
import de.maxhenkel.pipez.blocks.tileentity.ModTileEntities;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.model.CompositeBlockStateModel;
import fr.madu59.obe.client.registry.SpecialModelBuilderGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class PipezCompat {
    public static void init() {
        RegistryApi.registerGroup("pipez_pipes");
        RegistryApi.registerBlockEntityType("pipez_pipes", ModTileEntities.ITEM_PIPE.get());
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
        @Override
        public @Nullable BlockStateModel build(BlockState state, BlockEntity entity, BlockStateModel originalModel){
            PoseStack poseStack = new PoseStack();
            if(entity instanceof PipeTileEntity pipeBe){
                for (Direction side : Direction.values()) {
                    if(pipeBe.isExtracting(side)){
                        poseStack.pushPose();
                        poseStack.translate(side.getStepX() * 0.001D, side.getStepY() * 0.001D, side.getStepZ() * 0.001D);
                        poseStack.translate(0.5D, 0.5D, 0.5D);
                        poseStack.mulPose(getRotation(side));
                        poseStack.translate(-0.5D, -0.5D, -0.5D);
                        BlockEntityStateModel extractingModel = new BlockEntityStateModel(ModelRegistry.Model.ITEM_PIPE_EXTRACT.getModel().get(), poseStack, true, pipeBe.getBlockState(), null);
                        originalModel = new CompositeBlockStateModel(originalModel, extractingModel);
                        poseStack.popPose();
                    }
                }
            }
            System.out.println("Built model for pipe with state: " + state + " and entity: " + entity);
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

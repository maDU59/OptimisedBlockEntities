package fr.madu59.obe.client.renderer.blockentity.chest;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState.ChestMaterialType;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperChestBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.phys.Vec3;

public class OBEChestRenderer<T extends BlockEntity & LidBlockEntity> extends ChestRenderer<T> {

    public OBEChestRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(final ChestRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        if(RenderModeManager.shouldRenderEntity(state)) super.submit(state, poseStack, submitNodeCollector, camera);
    }

    public static ChestRenderState.ChestMaterialType getChestMaterial(Block block, boolean bl) {
        if (block instanceof CopperChestBlock copperChestBlock) {
            ChestRenderState.ChestMaterialType var10000;
            switch (copperChestBlock.getState()) {
                case UNAFFECTED -> var10000 = ChestMaterialType.COPPER_UNAFFECTED;
                case EXPOSED -> var10000 = ChestMaterialType.COPPER_EXPOSED;
                case WEATHERED -> var10000 = ChestMaterialType.COPPER_WEATHERED;
                case OXIDIZED -> var10000 = ChestMaterialType.COPPER_OXIDIZED;
                default -> throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
        } else if (block instanceof EnderChestBlock) {
            return ChestMaterialType.ENDER_CHEST;
        } else if (bl) {
            return ChestMaterialType.CHRISTMAS;
        } else {
            return block instanceof TrappedChestBlock ? ChestMaterialType.TRAPPED : ChestMaterialType.REGULAR;
        }
    }

    @Override
    public void extractRenderState(final T blockEntity, final ChestRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        ((BlockEntityRenderStateExt)state).blockEntity(blockEntity);
        if(RenderModeManager.shouldRenderEntity(blockEntity)) super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
    }
}

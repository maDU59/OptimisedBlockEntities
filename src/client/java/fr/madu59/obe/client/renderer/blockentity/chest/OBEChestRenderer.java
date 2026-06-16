package fr.madu59.obe.client.renderer.blockentity.chest;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;

public class OBEChestRenderer<T extends BlockEntity & LidBlockEntity> extends ChestRenderer<T> {

    public OBEChestRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        if(RenderModeManager.shouldRenderEntity(blockEntity)) super.render(blockEntity, f, poseStack, multiBufferSource, i, j);
    }

    public static Material getChestMaterial(Block block, ChestType chestType, boolean bl) {
        if (block instanceof EnderChestBlock) {
            return Sheets.ENDER_CHEST_LOCATION;
        } else if (bl) {
            return chooseMaterial(chestType, Sheets.CHEST_XMAS_LOCATION, Sheets.CHEST_XMAS_LOCATION_LEFT, Sheets.CHEST_XMAS_LOCATION_RIGHT);
        } else {
            return block instanceof TrappedChestBlock ? chooseMaterial(chestType, Sheets.CHEST_TRAP_LOCATION, Sheets.CHEST_TRAP_LOCATION_LEFT, Sheets.CHEST_TRAP_LOCATION_RIGHT) : chooseMaterial(chestType, Sheets.CHEST_LOCATION, Sheets.CHEST_LOCATION_LEFT, Sheets.CHEST_LOCATION_RIGHT);
        }
    }

    private static Material chooseMaterial(ChestType chestType, Material material, Material material2, Material material3) {
        return switch (chestType) {
            case ChestType.RIGHT -> material3;
            case ChestType.LEFT -> material2;
            case null, default -> material;
        };
    }
}

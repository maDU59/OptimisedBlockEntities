package fr.madu59.obe.client.mixin.blockentity.ext;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityRenderStateExt;

@Mixin(BlockEntityRenderState.class)
public class BlockEntityRenderStateMixin implements BlockEntityRenderStateExt {
    @Unique private BlockEntity blockEntity;

    @Override public void blockEntity(BlockEntity blockEntity) { this.blockEntity = blockEntity; }
    @Override public BlockEntity blockEntity() { return this.blockEntity; }
}

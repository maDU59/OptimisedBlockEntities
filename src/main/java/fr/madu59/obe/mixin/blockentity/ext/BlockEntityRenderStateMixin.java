package fr.madu59.obe.mixin.blockentity.ext;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fr.madu59.obe.renderer.blockentity.ext.BlockEntityRenderStateExt;

@Mixin(BlockEntityRenderState.class)
public abstract class BlockEntityRenderStateMixin implements BlockEntityRenderStateExt {
    // Thanks to Ceeden and Better Block Entities for this tricks, it allows to pass the block entity from the extraction to rendering stage
    @Unique private BlockEntity blockEntity;

    @Override public void blockEntity(BlockEntity blockEntity) { this.blockEntity = blockEntity; }
    @Override public BlockEntity blockEntity() { return this.blockEntity; }
}

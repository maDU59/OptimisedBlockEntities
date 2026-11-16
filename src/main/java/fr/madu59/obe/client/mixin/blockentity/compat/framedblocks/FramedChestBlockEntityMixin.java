package fr.madu59.obe.client.mixin.blockentity.compat.framedblocks;

import fr.madu59.obe.client.compat.framedblocks.FramedBlocksCompat;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.framedblocks.common.blockentity.special.FramedChestBlockEntity;

@Mixin(value = FramedChestBlockEntity.class, remap = false)
public class FramedChestBlockEntityMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$register(CallbackInfo ci){

        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;

        ext.obe$isSupported(Registry.isSupported("chest", be));
        FramedBlocksCompat.update(be);
    }
}
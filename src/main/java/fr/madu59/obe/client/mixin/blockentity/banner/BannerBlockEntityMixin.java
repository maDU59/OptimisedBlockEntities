package fr.madu59.obe.client.mixin.blockentity.banner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(BannerBlockEntity.class)
public class BannerBlockEntityMixin{

    @Inject(method = "<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/DyeColor;)V", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {

        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;

        ext.isSupportedBlockEntity(Registry.isSupported("banner", be.getType()));
        ext.renderBoth(true);
    }
}

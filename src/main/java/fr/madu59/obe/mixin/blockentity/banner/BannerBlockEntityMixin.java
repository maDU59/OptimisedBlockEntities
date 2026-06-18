package fr.madu59.obe.mixin.blockentity.banner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Mixin(BannerBlockEntity.class)
public class BannerBlockEntityMixin{

    @Inject(method = "<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/DyeColor;)V", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {

        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;

        ext.isSupportedBlockEntity(be.getType() == BlockEntityType.BANNER);
        ext.renderBoth(true);
    }

}

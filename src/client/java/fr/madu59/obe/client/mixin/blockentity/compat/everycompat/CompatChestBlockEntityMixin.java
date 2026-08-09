package fr.madu59.obe.client.mixin.blockentity.compat.everycompat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.mehvahdjukaar.every_compat.common_classes.CompatChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

@Pseudo
@Mixin(value = CompatChestBlockEntity.class, remap = false)
public class CompatChestBlockEntityMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$register(CallbackInfo ci){
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.hasSpecialRenderer(true);
    }
}

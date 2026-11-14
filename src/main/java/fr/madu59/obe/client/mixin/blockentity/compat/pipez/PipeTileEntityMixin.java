package fr.madu59.obe.client.mixin.blockentity.compat.pipez;

import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = PipeTileEntity.class, remap = false)
public class PipeTileEntityMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void obe$register(CallbackInfo ci){

        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;

        ext.obe$hasSpecialRenderer(true);
        ext.obe$isSupported(Registry.isSupported("pipez_pipes", be));
    }
}

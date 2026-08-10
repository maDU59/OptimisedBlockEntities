package fr.madu59.obe.client.mixin.blockentity.renderers.compat.everycompat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.compat.everycompat.EveryCompatCompat;
import fr.madu59.obe.client.registry.MaterialGetter;
import fr.madu59.obe.client.registry.Registry;
import net.mehvahdjukaar.every_compat.common_classes.CompatChestBlockEntity;
import net.mehvahdjukaar.every_compat.common_classes.CompatChestBlockRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Pseudo
@Mixin(value = CompatChestBlockRenderer.class, remap = false)
public class CompatChestBlockRendererMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private static void obe$register(CallbackInfo ci, @Local(argsOnly = true) BlockEntityType<CompatChestBlockEntity> tile){
        Registry.addBlockEntityTypeInGroup("chest", tile);
        MaterialGetter.register(tile, EveryCompatCompat::getChestMaterial);
    }
}

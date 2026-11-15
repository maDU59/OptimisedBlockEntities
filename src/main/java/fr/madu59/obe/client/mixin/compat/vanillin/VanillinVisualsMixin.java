package fr.madu59.obe.client.mixin.compat.vanillin;

import dev.engine_room.vanillin.VanillaVisuals;
import fr.madu59.obe.client.compat.ModCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = VanillaVisuals.class, remap = false)
public class VanillinVisualsMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$onVanillaVisualsInit(CallbackInfo ci) {
        ModCompat.preventConflictsWithVanillin();
    }
}
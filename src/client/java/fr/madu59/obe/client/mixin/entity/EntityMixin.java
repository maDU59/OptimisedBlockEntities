package fr.madu59.obe.client.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.renderer.entity.MeshableEntityTracker;
import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "onSyncedDataUpdated", at = @At("TAIL"))
    private void optimizedcushions$onDataUpdated(final CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;

        MeshableEntityTracker.updateMeshableEntity(entity, entity.blockPosition());
    }
}

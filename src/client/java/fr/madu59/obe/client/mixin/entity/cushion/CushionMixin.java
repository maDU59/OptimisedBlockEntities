package fr.madu59.obe.client.mixin.entity.cushion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.renderer.entity.MeshableEntityTracker;
import fr.madu59.obe.client.renderer.entity.ext.EntityExt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.item.DyeColor;

@Mixin(Cushion.class)
public class CushionMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci, @Local EntityType<Cushion> entityType){

        Entity entity = (Entity)(Object)this;
        EntityExt ext = (EntityExt)entity;

        ext.isSupported(entityType == EntityTypes.CUSHION);
    }

    @Inject(method = "setPos(DDD)V", at = @At("HEAD"))
    private void obe$beforeMove(double x, double y, double z, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        MeshableEntityTracker.deregisterMeshableEntity(entity, entity.blockPosition());
    }

    @Inject(method = "setPos(DDD)V", at = @At("TAIL"))
    private void obe$afterMove(double x, double y, double z, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        MeshableEntityTracker.registerMeshableEntity(entity, entity.blockPosition());
    }

    @Inject(method = "setColor", at = @At("TAIL"))
    private void obe$onColorChange(DyeColor color, CallbackInfo ci) {
        Entity entity = (Entity)(Object)this;
        MeshableEntityTracker.updateMeshableEntity(entity, entity.blockPosition());
    }
}

package fr.madu59.obe.client.mixin.entity.ext;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import fr.madu59.obe.client.renderer.entity.ext.EntityRenderStateExt;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements EntityRenderStateExt {
    @Unique private Entity entity;

    @Override public void entity(Entity entity) { this.entity = entity; }
    @Override public Entity entity() { return this.entity; }
}

package fr.madu59.obe.client.mixin.accessor;

import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FaceBakery.class)
public interface FaceBakeryInvoker {

    @Invoker("recalculateWinding")
    void obe$recalculateWinding(int[] vertices, Direction direction);
}
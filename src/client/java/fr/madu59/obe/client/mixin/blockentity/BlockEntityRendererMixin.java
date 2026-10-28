package fr.madu59.obe.client.mixin.blockentity;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import fr.madu59.obe.client.config.SettingsManager;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(BlockEntityRenderer.class)
public interface BlockEntityRendererMixin<T extends BlockEntity> {
    @ModifyReturnValue(method = "getViewDistance", at = @At("RETURN"))
    public default int obe$onGetViewDistance(int original) {
        return SettingsManager.BLOCK_ENTITY_RENDER_DISTANCE.getValue();
    }
}
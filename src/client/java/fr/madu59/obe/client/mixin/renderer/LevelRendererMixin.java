package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.renderer.blockentity.SpecialBlockEntityRenderingManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.entity.ext.EntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Cushion;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

@Mixin(LevelExtractor.class)
public class LevelRendererMixin {

    @WrapOperation(
        method = "extractVisibleBlockEntities",
        at = @At(
            value = "INVOKE", 
            target = "getRenderableBlockEntities" 
        )
    )
    private <T extends SectionMesh> List<BlockEntity> obe$redirectGetRenderableBlockEntities(T sectionMeshInstance, Operation<List<BlockEntity>> originalCall) {
        List<BlockEntity> original = originalCall.call(sectionMeshInstance);

        if (original == null || original.isEmpty())  return original;

        for (int i = original.size() - 1; i >= 0; i--) {
            if (original.get(i) instanceof BlockEntityExt ext){
                if(ext.isEnabled() && (!RenderModeManager.shouldRenderEntityFast(ext) || ext.shouldSkipRendering() || SpecialBlockEntityRenderingManager.shouldSkipRendering((BlockEntity)ext))){
                    original.remove(i);
                }
            }
        }

        return original;
    }

    @Inject(
        method = "isEntityVisible",
        at = @At("HEAD"),
        cancellable = true
    )
    private void obe$shouldRenderEntity(CallbackInfoReturnable<Boolean> cir, @Local Entity entity) {
        if(entity instanceof EntityExt ext && ext.isSupported()){
            boolean modToggle = SettingsManager.MOD_TOGGLE.getValue();
            if(entity instanceof Cushion) ext.isEnabled(SettingsManager.OPTIMISED_CUSHIONS.getValue() && modToggle);
            if(ext.isEnabled() && ext.renderMode() == RenderMode.TERRAIN && !entity.shouldShowName()){
                cir.setReturnValue(false);
            }
        }
    }
}

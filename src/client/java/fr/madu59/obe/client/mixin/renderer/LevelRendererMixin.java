package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

@Mixin(LevelRenderer.class)
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
                if(ext.isEnabled() && (!RenderModeManager.shouldRenderEntityFast(ext) || ext.shouldSkipBeRendering())) {
                    original.remove(i);
                }
                else{
                    RenderModeManager.updateOnRender(ext);
                }
            }
        }

        return original;
    }
}

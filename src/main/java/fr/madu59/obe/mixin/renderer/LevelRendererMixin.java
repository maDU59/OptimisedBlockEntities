package fr.madu59.obe.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Redirect(
        method = "Lnet/minecraft/client/renderer/LevelRenderer;extractVisibleBlockEntities(Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/culling/Frustum;)V",
        at = @At(
            value = "INVOKE", 
            target = "getRenderableBlockEntities" 
        )
    )
    private <T extends SectionMesh> List<BlockEntity> obe$redirectGetRenderableBlockEntities(T sectionMeshInstance) {
        List<BlockEntity> original = sectionMeshInstance.getRenderableBlockEntities();

        if (original == null || original.isEmpty())  return original;

        for (int i = original.size() - 1; i >= 0; i--) {
            BlockEntityExt ext = (BlockEntityExt) original.get(i);
            if (ext != null && ext.isEnabled() && (!RenderModeManager.shouldRenderEntity(ext, original.get(i)) || ext.shouldSkipBeRendering())) {
                original.remove(i);
            }
        }

        return original;
    }
}

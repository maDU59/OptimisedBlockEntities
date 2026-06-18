package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Redirect(
        method = "extractVisibleBlockEntities",
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/client/renderer/chunk/SectionMesh;getRenderableBlockEntities()Ljava/util/List;" 
        )
    )
    private <T extends SectionMesh> List<BlockEntity> obe$redirectGetRenderableBlockEntities(T sectionMeshInstance) {
        List<BlockEntity> original = sectionMeshInstance.getRenderableBlockEntities();

        if (original == null || original.isEmpty())  return original;

        for (int i = original.size() - 1; i >= 0; i--) {
            BlockEntityExt ext = (BlockEntityExt) original.get(i);
            if (ext != null && !RenderModeManager.shouldRenderEntity(ext, original.get(i))) {
                original.remove(i);
            }
        }

        return original;
    }
}

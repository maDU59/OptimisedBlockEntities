package fr.madu59.obe.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.renderer.blockentity.misc.RenderModeManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.CompiledSection;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

@Mixin(value = LevelRenderer.class, priority = 1001) //We don't want to prevent sodium from injecting here
public class LevelRendererMixin {

    @Redirect(
        method = "renderLevel",
        at = @At(
            value = "INVOKE", 
            target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$CompiledSection;getRenderableBlockEntities()Ljava/util/List;" 
        ),
        require = 0
    )
    private <T extends CompiledSection> List<BlockEntity> obe$redirectGetRenderableBlockEntities(T sectionMeshInstance) {
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

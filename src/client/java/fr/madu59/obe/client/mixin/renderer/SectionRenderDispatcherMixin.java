package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.core.SectionPos;

@Mixin(RenderSection.class)
public class SectionRenderDispatcherMixin {
    @Inject(method = "setSectionMesh", at = @At("RETURN"))
    private void obe$onTaskDone(CallbackInfo ci){
        ChunkTaskHolder.executeTasks(SectionPos.of(((RenderSection) (Object) this).getRenderOrigin()));
    }
}

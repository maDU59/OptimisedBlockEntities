package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.core.SectionPos;

@Mixin(RenderSection.class)
public class SectionRenderDispatcherMixin {
    @Inject(method = "setSectionMesh", at = @At("RETURN"))
    private void obe$onTaskDone(CallbackInfoReturnable<SectionMesh> ci){
        ChunkTaskHolder.executeTasks(SectionPos.of(((RenderSection) (Object) this).getRenderOrigin()));
    }
}

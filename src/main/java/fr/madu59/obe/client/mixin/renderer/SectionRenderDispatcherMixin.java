package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.core.SectionPos;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk$RebuildTask")
public class SectionRenderDispatcherMixin {
    @Inject(method = "doTask", at = @At("RETURN"))
    private void obe$onTaskDone(CallbackInfo ci){
        ChunkTaskHolder.executeTasks(SectionPos.of(((RenderChunk) (Object) this).getOrigin()));
    }
}

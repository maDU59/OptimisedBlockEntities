package fr.madu59.obe.client.mixin.renderer;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.core.SectionPos;

@Mixin(targets = "net.minecraft.client.renderer.chunk.ChunkRenderDispatcher$RenderChunk$RebuildTask")
public class SectionRenderDispatcherMixin {

    @Shadow @Final private RenderChunk this$1;

    @Inject(method = "doTask", at = @At("RETURN"))
    private void obe$onTaskDone(CallbackInfoReturnable<?> ci){
        ChunkTaskHolder.executeTasks(SectionPos.of(this$1.getOrigin()));
    }
}

package fr.madu59.obe.client.mixin.renderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection.SectionTask.SectionTaskResult;
import net.minecraft.core.SectionPos;

@Mixin(RenderSection.CompileTask.class)
public class SectionRenderDispatcherMixin {
    @Inject(method = "doTask", at = @At("RETURN"))
    private void obe$onTaskDone(CallbackInfoReturnable<SectionTaskResult> ci, @Local SectionPos sectionPos){
        ChunkTaskHolder.executeTasks(sectionPos);
    }
}

package fr.madu59.obe.client.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;

@Mixin(value = RenderSectionManager.class, remap = false)
public class RenderSectionManagerMixin {
    @Inject(method = "updateSectionInfo", at = @At("HEAD"))
    private void obe$executeChunkTasks(CallbackInfo ci, @Local RenderSection section) {
        ChunkTaskHolder.executeTasks(section.getPosition());
    }
}

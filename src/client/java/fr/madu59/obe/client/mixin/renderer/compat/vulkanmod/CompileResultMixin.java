package fr.madu59.obe.client.mixin.renderer.compat.vulkanmod;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.vulkanmod.render.chunk.RenderSection;
import net.vulkanmod.render.chunk.build.task.CompileResult;

@Pseudo
@Mixin(value = CompileResult.class, remap = false)
public abstract class CompileResultMixin {

    @Shadow
    @Final
    public RenderSection renderSection;

    @Inject(method = "updateSection", at = @At("HEAD"))
    private void obe$executeChunkTasks(CallbackInfo ci) {
        SectionPos sectionPos = SectionPos.of(new BlockPos(
                this.renderSection.xOffset(),
                this.renderSection.yOffset(),
                this.renderSection.zOffset()
        ));
        ChunkTaskHolder.executeTasks(sectionPos);
    }
}
package fr.madu59.obe.client.mixin.renderer.compat.vulkanmod;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import fr.madu59.obe.client.chunk.ChunkTaskHolder;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.entity.MeshableEntityTracker;
import fr.madu59.obe.client.renderer.entity.MeshableEntityTracker.MeshableEntityData;
import fr.madu59.obe.client.renderer.entity.ext.EntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.misc.RenderModeManager.RenderMode;
import fr.madu59.obe.client.util.meshing.SectionMeshingUtil;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.vulkanmod.render.chunk.build.RenderRegion;
import net.vulkanmod.render.chunk.build.renderer.BlockRenderer;
import net.vulkanmod.render.chunk.build.task.BuildTask;
import net.vulkanmod.render.chunk.build.task.CompileResult;

@Pseudo
@Mixin(value = BuildTask.class, remap = false)
public class BuildTaskMixin {

    @Shadow RenderRegion region;

    @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

    @WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/vulkanmod/render/chunk/build/RenderRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState obe$getBlockState(RenderRegion region, BlockPos pos, Operation<BlockState> original, @Share("be") LocalRef<BlockEntity> beRef){
        beRef.set(region.getBlockEntity(pos));
        return original.call(region, pos);
    }

    @WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, Operation<RenderShape> original, @Share("be") LocalRef<BlockEntity> beRef, @Local(ordinal = 0) BlockPos startPos){
        return SectionMeshingUtil.getCorrectedRenderShape(state, beRef.get(), SectionPos.of(startPos), original.call(state));
    }

    @Inject(method = "handleBlockEntity", at = @At("HEAD"), cancellable = true)
    private void obe$suppressEntityRender(CompileResult compileResult, BlockEntity be, CallbackInfo ci) {
        BlockEntityExt ext = (BlockEntityExt) be;
        if(ext != null && ext.isSupported()){
            RenderModeManager.updateBlockEntityOnChunkRemesh(ext, SectionPos.of(be.getBlockPos()));
        }
        if(ext != null && ext.isEnabled() && (!(ext.forceEntity() || !ext.isSupported() || ext.renderModeDelayed() == RenderMode.ENTITY || ext.renderBoth()) || ext.shouldSkipRendering())) {
            ci.cancel();
        }
    }

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/vulkanmod/render/chunk/build/RenderRegion;loadBlockStates()V"))
    private void obe$appendMeshData(CallbackInfo ci, @Local BlockRenderer blockRenderer, @Local LevelSlice slice, @Local(ordinal = 0) BlockPos startPos){
        if(!SettingsManager.MOD_TOGGLE.getValue()) return;

        SectionPos sectionPos = SectionPos.of(startPos);

        Collection<MeshableEntityData> entitiesData = MeshableEntityTracker.getMeshableEntities(sectionPos);
        if(entitiesData == null) return;

        MutableBlockPos pos = new MutableBlockPos();
        MutableBlockPos modelOffset = new MutableBlockPos();

        for(MeshableEntityData data : entitiesData){
            if(!data.isEnabled()) continue;
            if(data.level() != Minecraft.getInstance().level){
                MeshableEntityTracker.deleteInvalidMeshableEntity(data.id(), data.blockPos());
                continue;
            }
            BlockEntityStateModel model = data.getModel();
            ChunkTaskHolder.addTask(sectionPos, () -> {
                EntityExt ext = ((EntityExt)Minecraft.getInstance().level.getEntity(data.id()));
                if(ext != null) ext.renderMode(RenderMode.TERRAIN);
            });
            pos.set(data.blockPos());

            int localX = pos.getX() & 15;
            int localY = pos.getY() & 15;
            int localZ = pos.getZ() & 15;
            modelOffset.set(localX, localY, localZ);

            model.emitQuads(blockRenderer.getEmitter(), this.region, pos, region.getBlockState(pos), RandomSource.create(42), (direction) -> false);
        }
    }
}
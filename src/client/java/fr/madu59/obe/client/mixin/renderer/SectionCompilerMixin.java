package fr.madu59.obe.client.mixin.renderer;

import java.util.Collection;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexSorting;

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import fr.madu59.obe.client.resources.ResourceUtil;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionCompiler.Results;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SectionCompiler.class)
public abstract class SectionCompilerMixin {

    @Shadow
    protected abstract BufferBuilder getOrBeginLayer(Map<ChunkSectionLayer,BufferBuilder> map, SectionBufferBuilderPack buffer, ChunkSectionLayer layer);
    @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();

    @WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState obe$getBlockState(RenderSectionRegion region, BlockPos pos, Operation<BlockState> original, @Share("be") LocalRef<BlockEntity> beRef){
        beRef.set(region.getBlockEntity(pos));
        return original.call(region, pos);
    }

    @WrapOperation(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"))
    private RenderShape obe$getRenderShape(BlockState state, Operation<RenderShape> original, @Share("be") LocalRef<BlockEntity> beRef, @Local SectionPos sectionPos){
        if(state.hasBlockEntity()){
            BlockEntity be = beRef.get();
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupported()) {
                RenderModeManager.updateBlockEntityOnChunkRemesh(ext, sectionPos);
                if(ext.isEnabled() && ext.renderModeDelayed() == RenderMode.TERRAIN){
                    return RenderShape.MODEL;
                }
            }
        }
        return original.call(state);
    }

    @WrapOperation(
        method = "compile",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/block/ModelBlockRenderer;tesselateBlock(Lnet/minecraft/client/renderer/block/BlockQuadOutput;FFFLnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;J)V"
        )
    )
    public void obe$wrapRenderModel(ModelBlockRenderer instance, BlockQuadOutput output, float x, float y, float z, BlockAndTintGetter level, BlockPos pos, BlockState state, BlockStateModel originalModel, long seed, Operation<Void> original, @Share("be") LocalRef<BlockEntity> beRef) {
        if(state.hasBlockEntity()){

            BlockStateModel model = originalModel;
            BlockEntity be = beRef.get();
            BlockEntityExt ext = (BlockEntityExt) be;

            if(ext != null){
                if(ext.renderModeDelayed() != RenderMode.TERRAIN || !ext.isSupported() || !ext.isEnabled() || ext.forceEntity()){
                    model = ResourceUtil.getDefaultModel(be.getBlockState());
                }
                else if(ext.hasSpecialRenderer()) model = blockEntityModelsManager.getModel(state, pos, state.getSeed(pos), originalModel, be);
            }

            original.call(instance, output, x, y, z, level, pos, state, model, seed);
        }
        else original.call(instance, output, x, y, z, level, pos, state, originalModel, seed);
    }

    @Inject(
        method = "compile",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;entrySet()Ljava/util/Set;", 
            ordinal = 0
        )
    )
    private void obe$appendCustomDataToCutout(
        SectionPos sectionPos, 
        RenderSectionRegion region, 
        VertexSorting vertexSorting, 
        SectionBufferBuilderPack builders, 
        CallbackInfoReturnable<Results> cir,
        @Local(ordinal = 0) Map<ChunkSectionLayer, BufferBuilder> startedLayers,
        @Local(ordinal = 0) ModelBlockRenderer blockRenderer
    ) {
        if(!SettingsManager.MOD_TOGGLE.getValue()) return;

        Collection<MeshableEntityData> entitiesData = MeshableEntityTracker.getMeshableEntities(sectionPos);
        if(entitiesData == null) return;

        BufferBuilder cutoutBuilder = getOrBeginLayer(startedLayers, builders, ChunkSectionLayer.CUTOUT);

        BlockQuadOutput quadOutput = (x, y, z, quad, instance) -> {
            cutoutBuilder.putBlockBakedQuad(x, y, z, quad, instance);
        };

        MutableBlockPos pos = new MutableBlockPos();

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
            blockRenderer.tesselateBlock(quadOutput, SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()), region, pos, region.getBlockState(data.blockPos()), model, 42);
        }
    }
}

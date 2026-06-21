package fr.madu59.obe.client.mixin.renderer.compat.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.renderer.OBEBlockRenderer;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Pseudo
@Mixin(value = BlockRenderer.class, remap = false)
public class BlockRendererMixin {
    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();

    @ModifyVariable(method = "renderModel", at = @At("HEAD"), argsOnly = true)
    public BlockStateModel renderModel(BlockStateModel model, BlockStateModel originalModel, BlockState state, BlockPos pos, BlockPos origin) {
        if(state.hasBlockEntity()){
            BlockEntity be = Minecraft.getInstance().level.getBlockEntity(pos);
            BlockEntityExt ext = (BlockEntityExt) be;
            if(ext != null && ext.isSupportedBlockEntity()) {
                RenderModeManager.updateBlockEntity(ext, be);
                if(ext.isSupportedBlockEntity() && !ext.hasSpecialRenderer() && ext.renderMode() != RenderMode.TERRAIN){
                    return new BlockEntityStateModel();
                }
                model = obeBlockRenderer.getModel(state, pos, state.getSeed(pos), model);
            }
        }
        return model == null? originalModel : model;
    }
}

package fr.madu59.obe.client.mixin.renderer;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {

    @Inject(method = "getChunkRenderType", at = @At("RETURN"), cancellable = true)
    private static void onGetChunkRenderType(BlockState blockState, CallbackInfoReturnable<ChunkSectionLayer> cir) {
        if(blockState.hasBlockEntity()){
            if(cir.getReturnValue() == ChunkSectionLayer.SOLID){
                Block block = blockState.getBlock();
                if(block instanceof ShulkerBoxBlock || block instanceof AbstractSkullBlock || block instanceof CeilingHangingSignBlock || block instanceof WallHangingSignBlock || block instanceof ChestBlock){
                    cir.setReturnValue(ChunkSectionLayer.CUTOUT);
                    return;
                }
            }
        }
    }
}
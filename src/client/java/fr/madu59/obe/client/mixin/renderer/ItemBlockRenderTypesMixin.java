package fr.madu59.obe.client.mixin.renderer;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {

    @Inject(method = "getChunkRenderType", at = @At("RETURN"), cancellable = true)
    private static void onGetChunkRenderType(BlockState blockState, CallbackInfoReturnable<RenderType> cir) {
        if(RenderModeManager.hasBlockEntity(blockState)){
            if(cir.getReturnValue() == RenderType.solid()){
                Block block = blockState.getBlock();
                if(block instanceof ShulkerBoxBlock || block instanceof AbstractSkullBlock || block instanceof CeilingHangingSignBlock || block instanceof WallHangingSignBlock){
                    cir.setReturnValue(RenderType.cutoutMipped());
                    return;
                }
            }
        }
    }
}
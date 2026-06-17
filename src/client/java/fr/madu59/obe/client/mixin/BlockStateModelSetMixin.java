package fr.madu59.obe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.renderer.OBEBlockRenderer;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockStateModelSet.class)
public class BlockStateModelSetMixin {

    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void obe$getBlockStateModel(BlockState state, CallbackInfoReturnable<BlockStateModel> cir){
        if (!state.hasBlockEntity()) return;

        Block block = state.getBlock();
        RandomSource random = RandomSource.create(42);

        if(block instanceof AbstractSkullBlock){
            cir.setReturnValue(obeBlockRenderer.getSkullBlockModel(state, random));
        }
        else if(block instanceof AbstractChestBlock){
            cir.setReturnValue(obeBlockRenderer.getChestModel(state, random));
        }
        else if(block instanceof BannerBlock || block instanceof WallBannerBlock){
            cir.setReturnValue(obeBlockRenderer.getBannerModel(state, random));
        }
        // else if(block instanceof BellBlock){
        //     BlockStateModelSet set = ((BlockStateModelSet)(Object)this);
        //     OBEBlockRenderer.originalBellModel = (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
        //     cir.setReturnValue(new CompositeBlockStateModel(obeBlockRenderer.getBellModel(state, random), (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel())));
        // }
        else if(block instanceof CopperGolemStatueBlock){
            cir.setReturnValue(obeBlockRenderer.getCopperGolemStatueModel(state, random));
        }
        else if(block instanceof ShulkerBoxBlock){
            cir.setReturnValue(obeBlockRenderer.getShulkerBoxModel(state, random));
        }
        else if(block instanceof DecoratedPotBlock){
            cir.setReturnValue(obeBlockRenderer.getDecoratedPotModel(state, random));
        }
    }
}

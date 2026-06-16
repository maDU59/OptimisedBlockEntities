package fr.madu59.obe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.renderer.OBEBlockRenderer;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockModelShaper.class)
public class BlockStateModelSetMixin {

    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();
    @Unique private final RandomSource random = RandomSource.create();

    @Inject(method = "getBlockModel", at = @At("HEAD"), cancellable = true)
    public void obe$getBlockStateModel(BlockState state, CallbackInfoReturnable<BakedModel> cir){
        if (!RenderModeManager.hasBlockEntity(state)) return;

        Block block = state.getBlock();
        random.setSeed(42);

        if(block instanceof CeilingHangingSignBlock || block instanceof WallHangingSignBlock){
            cir.setReturnValue(obeBlockRenderer.getHangingSignModel(state, random));
        }
        else if(block instanceof StandingSignBlock || block instanceof WallSignBlock){
            cir.setReturnValue(obeBlockRenderer.getStandingSignModel(state, random));
        }
        else if(block instanceof AbstractSkullBlock){
            cir.setReturnValue(obeBlockRenderer.getSkullBlockModel(state, random));
        }
        else if(block instanceof BedBlock){
            cir.setReturnValue(obeBlockRenderer.getBedModel(state, random));
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
        else if(block instanceof ShulkerBoxBlock){
            cir.setReturnValue(obeBlockRenderer.getShulkerBoxModel(state, random));
        }
        else if(block instanceof DecoratedPotBlock){
            cir.setReturnValue(obeBlockRenderer.getDecoratedPotModel(state, random));
        }
    }
}

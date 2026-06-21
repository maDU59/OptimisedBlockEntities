package fr.madu59.obe.client.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.OBEBlockRenderer;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockStateModelSet.class)
public class BlockStateModelSetMixin {

    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();

    @Shadow
    public Map<BlockState, BlockStateModel> modelByState;

    @Shadow
    private BlockStateModel missingModel;

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void obe$getBlockStateModel(BlockState state, CallbackInfoReturnable<BlockStateModel> cir){
        if (!state.hasBlockEntity()) return;

        RandomSource random = RandomSource.create(42);


        if(Registry.isSupported("skull", state)){
            cir.setReturnValue(obeBlockRenderer.getSkullBlockModel(state, random, obe$getOriginalModel(state)));
        }
        else if(Registry.isSupported("chest", state)){
            cir.setReturnValue(obeBlockRenderer.getChestModel(state, random, obe$getOriginalModel(state)));
        }
        else if(Registry.isSupported("banner", state)){
            cir.setReturnValue(obeBlockRenderer.getBannerModel(state, random, obe$getOriginalModel(state)));
        }
        // else if(Registry.isSupported("bell", state)){
        //     BlockStateModelSet set = ((BlockStateModelSet)(Object)this);
        //     OBEBlockRenderer.originalBellModel = (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
        //     cir.setReturnValue(new CompositeBlockStateModel(obeBlockRenderer.getBellModel(state, random, obe$getOriginalModel(state)), (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel())));
        // }
        else if(Registry.isSupported("copper_golem_statue", state)){
            cir.setReturnValue(obeBlockRenderer.getCopperGolemStatueModel(state, random, obe$getOriginalModel(state)));
        }
        else if(Registry.isSupported("shulker_box", state)){
            cir.setReturnValue(obeBlockRenderer.getShulkerBoxModel(state, random, obe$getOriginalModel(state)));
        }
        else if(Registry.isSupported("decorated_pot", state)){
            cir.setReturnValue(obeBlockRenderer.getDecoratedPotModel(state, random, obe$getOriginalModel(state)));
        }
    }

    @Unique
    public BlockStateModel obe$getOriginalModel(BlockState state){
        return modelByState.getOrDefault(state, this.missingModel);
    }
}

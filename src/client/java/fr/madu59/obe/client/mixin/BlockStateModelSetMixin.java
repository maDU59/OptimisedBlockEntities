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

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockModelShaper.class)
public class BlockStateModelSetMixin {

    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();

    @Shadow
    public Map<BlockState, BlockStateModel> modelByStateCache;

    @Shadow
    private ModelManager modelManager;

    @Inject(method = "getBlockModel", at = @At("HEAD"), cancellable = true)
    public void obe$getBlockStateModel(BlockState state, CallbackInfoReturnable<BlockStateModel> cir){
        if (!state.hasBlockEntity()) return;

        RandomSource random = RandomSource.create(42);

        BlockStateModel model;

        if(Registry.isSupported("sign", state)){
            model = obeBlockRenderer.getStandingSignModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("hanging_sign", state)){
            model = obeBlockRenderer.getHangingSignModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("bed", state)){
            model = obeBlockRenderer.getBedModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("skull", state)){
            model = obeBlockRenderer.getSkullBlockModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("chest", state)){
            model = obeBlockRenderer.getChestModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("banner", state)){
            model = obeBlockRenderer.getBannerModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        // else if(Registry.isSupported("bell", state)){
        //     BlockStateModelSet set = ((BlockStateModelSet)(Object)this);
        //     OBEBlockRenderer.originalBellModel = (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
        //     model = new CompositeBlockStateModel(obeBlockRenderer.getBellModel(state, random, obe$getOriginalModel(state)), (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
        //     if(model != null) cir.setReturnValue(model);
        // }
        else if(Registry.isSupported("copper_golem_statue", state)){
            model = obeBlockRenderer.getCopperGolemStatueModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("shulker_box", state)){
            model = obeBlockRenderer.getShulkerBoxModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("decorated_pot", state)){
            model = obeBlockRenderer.getDecoratedPotModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
    }

    @Unique
    public BlockStateModel obe$getOriginalModel(BlockState state){
        BlockStateModel blockStateModel = (BlockStateModel)this.modelByStateCache.get(state);
        if (blockStateModel == null) {
            blockStateModel = this.modelManager.getMissingBlockStateModel();
        }

        return blockStateModel;
    }
}

package fr.madu59.obe.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.OBEBlockRenderer;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockModelShaper.class)
public class BlockStateModelSetMixin {

    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();

    @Inject(method = "getBlockModel", at = @At("HEAD"), cancellable = true)
    public void obe$getBlockStateModel(BlockState state, CallbackInfoReturnable<BakedModel> cir){
        if (!state.hasBlockEntity()) return;

        RandomSource random = RandomSource.create(42);

        if(Registry.isSupported("sign", state)){
            if(state.getBlock() instanceof StandingSignBlock || state.getBlock() instanceof WallSignBlock){
                cir.setReturnValue(obeBlockRenderer.getStandingSignModel(state, random));
            }
            else cir.setReturnValue(obeBlockRenderer.getHangingSignModel(state, random));
        }
        else if(Registry.isSupported("bed", state)){
            cir.setReturnValue(obeBlockRenderer.getBedModel(state, random));
        }
        else if(Registry.isSupported("skull", state)){
            cir.setReturnValue(obeBlockRenderer.getSkullBlockModel(state, random));
        }
        else if(Registry.isSupported("chest", state)){
            cir.setReturnValue(obeBlockRenderer.getChestModel(state, random));
        }
        else if(Registry.isSupported("banner", state)){
            cir.setReturnValue(obeBlockRenderer.getBannerModel(state, random));
        }
        // else if(Registry.isSupported("bell", state)){
        //     BlockStateModelSet set = ((BlockStateModelSet)(Object)this);
        //     OBEBlockRenderer.originalBellModel = (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
        //     cir.setReturnValue(new CompositeBlockStateModel(obeBlockRenderer.getBellModel(state, random), (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel())));
        // }
        else if(Registry.isSupported("shulker_box", state)){
            cir.setReturnValue(obeBlockRenderer.getShulkerBoxModel(state, random));
        }
        else if(Registry.isSupported("decorated_pot", state)){
            cir.setReturnValue(obeBlockRenderer.getDecoratedPotModel(state, random));
        }
    }
}

package fr.madu59.obe.client.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.OBEBlockRenderer;
import fr.madu59.obe.client.util.ResourceUtil;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockStateModelSet.class)
public class BlockStateModelSetMixin {

    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();
    @Unique private final Identifier missingTexture = Identifier.tryParse("minecraft:missingno");

    @Shadow
    public Map<BlockState, BlockStateModel> modelByState;

    @Shadow
    private BlockStateModel missingModel;

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void obe$getBlockStateModel(BlockState state, CallbackInfoReturnable<BlockStateModel> cir){
        if (!state.hasBlockEntity()) return;

        if(!SettingsManager.MOD_TOGGLE.getValue()) return;
        RandomSource random = RandomSource.create(42);

        BlockStateModel model;

        if(Registry.isSupported("sign", state) && SettingsManager.OPTIMISED_SIGNS.getValue()){
            model = obeBlockRenderer.getStandingSignModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("hanging_sign", state) && SettingsManager.OPTIMISED_SIGNS.getValue()){
            model = obeBlockRenderer.getHangingSignModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("bed", state) && SettingsManager.OPTIMISED_BEDS.getValue()){
            model = obeBlockRenderer.getBedModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("skull", state) && SettingsManager.OPTIMISED_SKULLS.getValue()){
            model = obeBlockRenderer.getSkullBlockModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("chest", state) && SettingsManager.OPTIMISED_CHESTS.getValue()){
            model = obeBlockRenderer.getChestModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("banner", state) && SettingsManager.OPTIMISED_BANNERS.getValue()){
            model = obeBlockRenderer.getBannerModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        // else if(Registry.isSupported("bell", state)){
        //     BlockStateModelSet set = ((BlockStateModelSet)(Object)this);
        //     OBEBlockRenderer.originalBellModel = (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
        //     model = new CompositeBlockStateModel(obeBlockRenderer.getBellModel(state, random, obe$getOriginalModel(state)), (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
        //     if(model != null) cir.setReturnValue(model);
        // }
        else if(Registry.isSupported("copper_golem_statue", state) && SettingsManager.OPTIMISED_COPPER_GOLEMS.getValue()){
            model = obeBlockRenderer.getCopperGolemStatueModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("shulker_box", state) && SettingsManager.OPTIMISED_SHULKER_BOXES.getValue()){
            model = obeBlockRenderer.getShulkerBoxModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
        else if(Registry.isSupported("decorated_pot", state) && SettingsManager.OPTIMISED_DECORATED_POTS.getValue()){
            model = obeBlockRenderer.getDecoratedPotModel(state, random, obe$getOriginalModel(state));
            if(model != null) cir.setReturnValue(model);
        }
    }

    @Unique
    public BlockStateModel obe$getOriginalModel(BlockState state){
        BlockStateModel model = modelByState.getOrDefault(state, this.missingModel);
        return model == null? new BlockEntityStateModel(ResourceUtil.getSprite(missingTexture)) : model;
    }
}

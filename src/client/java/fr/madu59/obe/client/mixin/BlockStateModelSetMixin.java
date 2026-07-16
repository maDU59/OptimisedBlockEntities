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
import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.resources.ResourceUtil;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockStateModelSet.class)
public class BlockStateModelSetMixin {

    @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();
    @Unique private final Identifier missingTexture = Identifier.tryParse("minecraft:missingno");

    @Shadow
    public Map<BlockState, BlockStateModel> modelByState;

    @Shadow
    private BlockStateModel missingModel;

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void obe$getBlockStateModel(BlockState state, CallbackInfoReturnable<BlockStateModel> cir){
        if (!state.hasBlockEntity()) return;

        if(!SettingsManager.MOD_TOGGLE.getValue()) return;
        try{ // In case of another mod trying to access the block states before the entity model set is populated
            RandomSource random = RandomSource.create(42);
            BlockStateModel model;

            String group = Registry.getGroup(state);
            if(group == null) return;

            if(group.equals("skull") && SettingsManager.OPTIMISED_SKULLS.getValue()){
                model = blockEntityModelsManager.getBlockModel(state, random, obe$getOriginalModel(state), group);
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("chest") && SettingsManager.OPTIMISED_CHESTS.getValue()){
                model = blockEntityModelsManager.getBlockModel(state, random, obe$getOriginalModel(state), group);
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("banner") && SettingsManager.OPTIMISED_BANNERS.getValue()){
                model = blockEntityModelsManager.getBlockModel(state, random, obe$getOriginalModel(state), group);
                if(model != null) cir.setReturnValue(model);
            }
            // else if(group.equals("bell")){
            //     BlockStateModelSet set = ((BlockStateModelSet)(Object)this);
            //     blockEntityModelsManager.originalBellModel = (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
            //     model = new CompositeBlockStateModel(blockEntityModelsManager.getBellModel(state, random, obe$getOriginalModel(state)), (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
            //     if(model != null) cir.setReturnValue(model);
            // }
            else if(group.equals("copper_golem_statue") && SettingsManager.OPTIMISED_COPPER_GOLEMS.getValue()){
                model = blockEntityModelsManager.getBlockModel(state, random, obe$getOriginalModel(state), group);
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("shulker_box") && SettingsManager.OPTIMISED_SHULKER_BOXES.getValue()){
                model = blockEntityModelsManager.getBlockModel(state, random, obe$getOriginalModel(state), group);
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("decorated_pot") && SettingsManager.OPTIMISED_DECORATED_POTS.getValue()){
                model = blockEntityModelsManager.getDecoratedPotModel(state, random, obe$getOriginalModel(state));
                if(model != null) cir.setReturnValue(model);
            }
        }
        catch(Exception e){

        }
    }

    @Unique
    public BlockStateModel obe$getOriginalModel(BlockState state){
        BlockStateModel model = modelByState.getOrDefault(state, this.missingModel);
        return model == null? new BlockEntityStateModel(ResourceUtil.getSprite(missingTexture)) : model;
    }
}

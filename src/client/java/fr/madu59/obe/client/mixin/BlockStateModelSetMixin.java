package fr.madu59.obe.client.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import fr.madu59.obe.client.config.Option;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.resources.ResourceUtil;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.resources.Identifier;
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
            String group = Registry.getGroup(state);
            if(group == null) return;

            Option<Boolean> toggle = switch (group) {
                case "skull" -> SettingsManager.OPTIMISED_SKULLS;
                case "chest" -> SettingsManager.OPTIMISED_CHESTS;
                case "banner" -> SettingsManager.OPTIMISED_BANNERS;
                case "bell" -> SettingsManager.OPTIMISED_BELLS;
                case "copper_golem_statue" -> SettingsManager.OPTIMISED_COPPER_GOLEMS;
                case "shulker_box" -> SettingsManager.OPTIMISED_SHULKER_BOXES;
                case "decorated_pot" -> SettingsManager.OPTIMISED_DECORATED_POTS;
                default -> null;
            };
            if(toggle != null && !toggle.getValue()) return;

            BlockStateModel model = ResourceUtil.getModel(state);
            if(model == null){
                if(group.equals("decorated_pot")) model = blockEntityModelsManager.getDecoratedPotModel(state, null, obe$getOriginalModel(state));
                else model = blockEntityModelsManager.getBlockModel(state, null, obe$getOriginalModel(state), group);
            }
            if(model != null) cir.setReturnValue(model);
        }
        catch(Exception e){

        }
    }

    @Unique
    public BlockStateModel obe$getOriginalModel(BlockState state){
        BlockStateModel model = ResourceUtil.getDefaultModel(state);
        return model == null? new BlockEntityStateModel(ResourceUtil.getSprite(missingTexture)) : model;
    }
}

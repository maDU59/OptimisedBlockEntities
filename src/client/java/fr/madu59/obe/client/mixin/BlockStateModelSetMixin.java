package fr.madu59.obe.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import fr.madu59.obe.client.config.Option;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.model.BlockEntityStateModel;
import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.BlockEntityModelsManager;
import fr.madu59.obe.client.resources.ResourceUtil;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockModelShaper.class)
public abstract class BlockStateModelSetMixin {

    @Unique private final BlockEntityModelsManager blockEntityModelsManager = new BlockEntityModelsManager();
    @Unique private final Identifier missingTexture = Identifier.tryParse("minecraft:missingno");

    @Shadow
    private ModelManager modelManager;

    @ModifyReturnValue(method = "getBlockModel", at = @At("RETURN"))
    public BlockStateModel obe$getBlockStateModel(BlockStateModel original, BlockState state){
        if (!state.hasBlockEntity() || !SettingsManager.MOD_TOGGLE.getValue()) return original;

        try{ // In case of another mod trying to access the block states before the entity model set is populated
            String group = Registry.getGroup(state);
            if(group == null) return original;

            Option<Boolean> toggle = switch (group) {
                case "sign" -> SettingsManager.OPTIMISED_SIGNS;
                case "hanging_sign" -> SettingsManager.OPTIMISED_SIGNS;
                case "bed" -> SettingsManager.OPTIMISED_BEDS;
                case "skull" -> SettingsManager.OPTIMISED_SKULLS;
                case "chest" -> SettingsManager.OPTIMISED_CHESTS;
                case "banner" -> SettingsManager.OPTIMISED_BANNERS;
                case "bell" -> SettingsManager.OPTIMISED_BELLS;
                case "copper_golem_statue" -> SettingsManager.OPTIMISED_COPPER_GOLEMS;
                case "shulker_box" -> SettingsManager.OPTIMISED_SHULKER_BOXES;
                case "decorated_pot" -> SettingsManager.OPTIMISED_DECORATED_POTS;
                default -> null;
            };
            if(toggle != null && !toggle.getValue()) return original;

            BlockStateModel model = ResourceUtil.getModel(state);
            if(model == null){
                if(original == null) original = new BlockEntityStateModel(ResourceUtil.getSprite(missingTexture));
                model = blockEntityModelsManager.getBlockModel(state, original, group);
            }
            if(model != null) return model;
            return original;
        }
        catch(Exception e){
            return original;
        }
    }
}

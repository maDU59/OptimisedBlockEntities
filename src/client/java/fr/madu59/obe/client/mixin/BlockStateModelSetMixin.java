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

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockModelShaper.class)
public class BlockStateModelSetMixin {

    @Unique private final OBEBlockRenderer obeBlockRenderer = new OBEBlockRenderer();
    @Unique private final ResourceLocation missingTexture = ResourceLocation.tryParse("minecraft:missingno");

    @Shadow
    public Map<BlockState, BakedModel> modelByStateCache;

    @Shadow
    private ModelManager modelManager;

    @Inject(method = "getBlockModel", at = @At("HEAD"), cancellable = true)
    public void obe$getBlockStateModel(BlockState state, CallbackInfoReturnable<BakedModel> cir){
        if (!state.hasBlockEntity()) return;

        if(!SettingsManager.MOD_TOGGLE.getValue()) return;
        try{ // In case of another mod trying to access the block states before the entity model set is populated
            RandomSource random = RandomSource.create(42);

            BakedModel model;

            String group = Registry.getGroup(state);
            if(group == null) return;

            if(group.equals("sign") && SettingsManager.OPTIMISED_SIGNS.getValue()){
                model = obeBlockRenderer.getStandingSignModel(state, random, obe$getOriginalModel(state));
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("hanging_sign") && SettingsManager.OPTIMISED_SIGNS.getValue()){
                model = obeBlockRenderer.getHangingSignModel(state, random, obe$getOriginalModel(state));
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("bed") && SettingsManager.OPTIMISED_BEDS.getValue()){
                model = obeBlockRenderer.getBedModel(state, random, obe$getOriginalModel(state));
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("skull") && SettingsManager.OPTIMISED_SKULLS.getValue()){
                model = obeBlockRenderer.getSkullBlockModel(state, random, obe$getOriginalModel(state));
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("chest") && SettingsManager.OPTIMISED_CHESTS.getValue()){
                model = obeBlockRenderer.getChestModel(state, random, obe$getOriginalModel(state));
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("banner") && SettingsManager.OPTIMISED_BANNERS.getValue()){
                model = obeBlockRenderer.getBannerModel(state, random, obe$getOriginalModel(state));
                if(model != null) cir.setReturnValue(model);
            }
            // else if(group.equals("bell")){
            //     BlockStateModelSet set = ((BlockStateModelSet)(Object)this);
            //     OBEBlockRenderer.originalBellModel = (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
            //     model = new CompositeBlockStateModel(obeBlockRenderer.getBellModel(state, random, obe$getOriginalModel(state)), (BlockStateModel)set.modelByState.getOrDefault(state, new BlockEntityStateModel());
            //     if(model != null) cir.setReturnValue(model);
            // }
            else if(group.equals("shulker_box") && SettingsManager.OPTIMISED_SHULKER_BOXES.getValue()){
                model = obeBlockRenderer.getShulkerBoxModel(state, random, obe$getOriginalModel(state));
                if(model != null) cir.setReturnValue(model);
            }
            else if(group.equals("decorated_pot") && SettingsManager.OPTIMISED_DECORATED_POTS.getValue()){
                model = obeBlockRenderer.getDecoratedPotModel(state, random, obe$getOriginalModel(state));
                if(model != null) cir.setReturnValue(model);
            }
        }
        catch(Exception e){

        }
    }

    @Unique
    public BakedModel obe$getOriginalModel(BlockState state){
        BakedModel bakedModel = (BakedModel)this.modelByStateCache.get(state);
        if (bakedModel == null) {
            bakedModel = this.modelManager.getMissingModel();
        }
        if (bakedModel == null) {
            bakedModel = new BlockEntityStateModel(ResourceUtil.getSprite(missingTexture));
        }

      return bakedModel;
    }
}

package fr.madu59.obe.client.compat.emf;

import fr.madu59.obe.OBE;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import traben.entity_model_features.EMFAnimationApi;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.parts.EMFModelPartRoot;
import traben.entity_model_features.utils.EMFEntity;
import traben.entity_model_features.utils.UEntityTypes;

public class EMFCompat {

    // Thanks to Traben, EMF/ETF dev for helping fixing issues with the compatibility  :D
    
    public static void applyRestPose(ModelPart root, BlockState blockState) {
        if (root instanceof EMFModelPartRoot && blockState.getBlock() instanceof EntityBlock entityBlock) {
            applyRestPose(root, entityBlock.newBlockEntity(BlockPos.ZERO, blockState));
        }
    }

    public static <T extends BlockEntity> void applyRestPose(ModelPart root, BlockEntity be) {
        try{
            if (root instanceof EMFModelPartRoot emfRoot) {
                EMFAnimationApi.animateModelForEntity((EMFEntity) be, emfRoot, true);
            }
        }
        catch(Exception e){
            OBE.LOGGER.error("Error occurred while applying rest pose to EMF model", e);
        }
    }

    public static void setCurrentSpecifiedModelLoading(BlockEntityType<?> type) {
        if (type == null) return;
        EMFManager.getInstance().currentBlockEntityTypeLoading = type;
        if (UEntityTypes.ENCHANTING_TABLE.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading.set("enchanting_book");
        else if (UEntityTypes.LECTERN.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading.set("lectern_book");
        else if (UEntityTypes.CHEST.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading.set("chest");
        else if (UEntityTypes.ENDER_CHEST.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading.set("ender_chest");
        else if (UEntityTypes.TRAPPED_CHEST.equals(type))
            EMFManager.getInstance().currentSpecifiedModelLoading.set("trapped_chest");
        else if (type.builtInRegistryHolder() != null && type.builtInRegistryHolder().unwrapKey().isPresent()) {
            ResourceLocation id = type.builtInRegistryHolder().unwrapKey().get().location();
            if (id.getNamespace().equals("minecraft")) {
                EMFManager.getInstance().currentSpecifiedModelLoading.set(id.getPath());
            } else {
                EMFManager.getInstance().currentSpecifiedModelLoading.set(id.getNamespace() + ":" + id.getPath());
            }
        }
        else EMFManager.getInstance().currentSpecifiedModelLoading.set("");
    }

    public static void resetCurrentSpecifiedModelLoading() {
        EMFManager.getInstance().currentSpecifiedModelLoading.set("");
    }
}

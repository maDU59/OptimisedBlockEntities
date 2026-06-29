package fr.madu59.obe.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.apache.logging.log4j.util.TriConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.OBE;
import fr.madu59.obe.util.blockentity.BellUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SpecialModelGetter {

    private static Map<BlockEntityType<?>, SpecialModelProvider> specialModelGetterProvider = new ConcurrentHashMap<>();
    private static Map<String, SpecialModelProvider> defaultspecialModelGetterProvider = new ConcurrentHashMap<>();

    public static void init(){
        registerDefault("bell", new SpecialModelProvider(BellUtil::getBellModelLayerLocation, BellUtil::getBellMaterial, BellUtil::transformBell).keepOriginalModel().showOriginalWhenHidden());
    }

    public static void registerDefault(String group, SpecialModelProvider getter){
        if(!Registry.hasGroup(group)){
            OBE.LOGGER.error("An external mod tried registering a default material getter in a non existing group: " + group);
        }
        else{
            defaultspecialModelGetterProvider.put(group, getter);
        }
    }

    public static void register(BlockEntityType<?> beType, SpecialModelProvider getter){
        specialModelGetterProvider.put(beType, getter);
    }

    public static SpecialModelProvider getSpecialModelProvider(BlockState state){
        return getSpecialModelProvider(state, null);
    }

    public static SpecialModelProvider getSpecialModelProvider(BlockState state, String group){
        if(!state.hasBlockEntity()) return null;
        BlockEntityType<?> beType = Registry.getBlockEntityType(state);
        if (beType == null) return null;
        SpecialModelProvider provider = specialModelGetterProvider.get(beType);
        if (provider != null) return provider;
        if (group == null) group = Registry.getGroup(beType);
        if (group != null) provider = defaultspecialModelGetterProvider.get(group);
        if (provider != null) return provider;
        return null;
    }

    public static class SpecialModelProvider{

        private final BiFunction<BlockState, BlockEntity, ModelLayerLocation> modelLayerLocationProvider;
        private final BiFunction<BlockState, BlockEntity, ResourceLocation> materialProvider;
        private final TriConsumer<BlockState, BlockEntity, PoseStack> transformationProvider;
        private boolean keepOriginalModel = false;
        private boolean showOriginalWhenHidden = false;

        public SpecialModelProvider(BiFunction<BlockState, BlockEntity, ModelLayerLocation> modelLayerLocationProvider, BiFunction<BlockState, BlockEntity, ResourceLocation> materialProvider, TriConsumer<BlockState, BlockEntity, PoseStack> transformationProvider){
            this.modelLayerLocationProvider = modelLayerLocationProvider;
            this.materialProvider = materialProvider;
            this.transformationProvider = transformationProvider;
        }

        public SpecialModelProvider keepOriginalModel(){
            this.keepOriginalModel = true;
            return this;
        }

        public SpecialModelProvider showOriginalWhenHidden(){
            this.showOriginalWhenHidden = true;
            return this;
        }

        public BiFunction<BlockState, BlockEntity, ModelLayerLocation> getModelLayerLocationProvider(){
            return modelLayerLocationProvider;
        }

        public BiFunction<BlockState, BlockEntity, ResourceLocation> getMaterialProvider(){
            return materialProvider;
        }

        public TriConsumer<BlockState, BlockEntity, PoseStack> getTransformationProvider(){
            return transformationProvider;
        }

        public boolean shouldKeepOriginalModel(){
            return keepOriginalModel;
        }

        public boolean shouldShowOriginalWhenHidden(){
            return showOriginalWhenHidden;
        }
    }
}

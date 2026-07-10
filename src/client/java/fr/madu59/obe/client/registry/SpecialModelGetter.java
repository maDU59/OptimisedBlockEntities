package fr.madu59.obe.client.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.logging.log4j.util.TriConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.OBE;
import fr.madu59.obe.client.util.blockentity.BellUtil;
import fr.madu59.obe.client.util.blockentity.SkullBlockUtil;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SpecialModelGetter {

    private static Map<BlockEntityType<?>, SpecialModelProvider> specialModelGetterProvider = new ConcurrentHashMap<>();
    private static Map<String, SpecialModelProvider> defaultspecialModelGetterProvider = new ConcurrentHashMap<>();

    public static void init(){
        registerDefault("bell", new SpecialModelProvider(BellUtil::getBellModelLayerLocation, BellUtil::getBellMaterial, BellUtil::transformBell, SpecialModelProvider::getDummyCacheKey).keepOriginalModel().showOriginalWhenHidden());
        registerDefault("skull", new SpecialModelProvider(SkullBlockUtil::getSkullBlockModelLayerLocation, SkullBlockUtil::getSkullBlockMaterial, SkullBlockUtil::transformSkullBlock, SkullBlockUtil::getBuiltInTexture));
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
        private final Function<BlockEntity, Object> cacheKeyProvider;
        private boolean keepOriginalModel = false;
        private boolean showOriginalWhenHidden = false;

        public SpecialModelProvider(BiFunction<BlockState, BlockEntity, ModelLayerLocation> modelLayerLocationProvider, BiFunction<BlockState, BlockEntity, ResourceLocation> materialProvider, TriConsumer<BlockState, BlockEntity, PoseStack> transformationProvider, Function<BlockEntity, Object> cacheKeyProvider){
            this.modelLayerLocationProvider = modelLayerLocationProvider;
            this.materialProvider = materialProvider;
            this.transformationProvider = transformationProvider;
            this.cacheKeyProvider = cacheKeyProvider;
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

        public Function<BlockEntity, Object> getCacheKeyProvider(){
            return cacheKeyProvider;
        }

        public boolean shouldKeepOriginalModel(){
            return keepOriginalModel;
        }

        public boolean shouldShowOriginalWhenHidden(){
            return showOriginalWhenHidden;
        }

        public static boolean getDummyCacheKey(BlockEntity be){
            return true;
        }
    }
}

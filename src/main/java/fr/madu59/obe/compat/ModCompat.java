package fr.madu59.obe.compat;

import java.util.Arrays;
import java.util.List;

import fr.madu59.obe.config.SettingsManager;
import fr.madu59.obe.platform.PlatformHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModCompat {
    private final static boolean isIrisLoaded = PlatformHelper.isModLoaded("iris") || PlatformHelper.isModLoaded("oculus");
    private final static boolean isSodiumLoaded = PlatformHelper.isModLoaded("sodium") || PlatformHelper.isModLoaded("embeddium");
    private final static boolean isEMFLoaded = PlatformHelper.isModLoaded("entity_model_features");
    private final static boolean isPunchyLoaded = PlatformHelper.isModLoaded("punchy");

    private static final List<String> incompatibleMods = Arrays.asList("vulkanmod","optifine","embeddium","optifabric");


    public static boolean isIrisLoaded(){
        return isIrisLoaded;
    }

    public static boolean isSodiumLoaded(){
        return isSodiumLoaded;
    }

    public static boolean isEMFLoaded(){
        return isEMFLoaded;
    }

    public static boolean isPunchyLoaded(){
        return isPunchyLoaded;
    }

    public static boolean isShadowPass(){
        if(isIrisLoaded()) return IrisCompat.isShadowPass();
        else return false;
    }

    public static ModelPart applyEMFRestPose(ModelPart root, BlockState state){
        if(isEMFLoaded() && SettingsManager.EMF_COMPAT.getValue()) return EMFCompat.applyRestPose(root, state);
        else return root;
    }

    public static boolean shouldRenderEntity(BlockEntity be){
        if(isPunchyLoaded()) return be.getBlockPos() == BlockPos.ZERO;
        return false;
    }

    public static boolean isIncompatibilityDetected(){
        for(String mod : incompatibleMods){
            if(PlatformHelper.isModLoaded(mod)) return true;
        }
        return false;
    }

    public static String getIncompatibleMod(){
        for(String mod : incompatibleMods){
            if(PlatformHelper.isModLoaded(mod)) return PlatformHelper.getModName(mod);
        }
        return null;
    }
}

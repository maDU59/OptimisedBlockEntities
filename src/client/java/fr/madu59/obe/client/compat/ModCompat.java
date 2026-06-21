package fr.madu59.obe.client.compat;

import fr.madu59.obe.client.platform.PlatformHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.level.block.state.BlockState;

public class ModCompat {
    private static boolean isIrisLoaded = PlatformHelper.isModLoaded("iris") || PlatformHelper.isModLoaded("oculus");
    private static boolean isSodiumLoaded = PlatformHelper.isModLoaded("sodium") || PlatformHelper.isModLoaded("embeddium");
    private static boolean isEMFLoaded = PlatformHelper.isModLoaded("entity_model_features");


    public static boolean isIrisLoaded(){
        return isIrisLoaded;
    }

    public static boolean isSodiumLoaded(){
        return isSodiumLoaded;
    }

    public static boolean isEMFLoaded(){
        return isEMFLoaded;
    }

    public static boolean isShadowPass(){
        if(isIrisLoaded()) return IrisCompat.isShadowPass();
        else return false;
    }

    public static ModelPart applyEMFRestPose(ModelPart root, BlockState state){
        if(isEMFLoaded()) return EMFCompat.applyRestPose(root, state);
        else return root;
    }
}

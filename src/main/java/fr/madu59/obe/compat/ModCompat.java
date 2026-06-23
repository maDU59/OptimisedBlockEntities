package fr.madu59.obe.compat;

import fr.madu59.obe.config.SettingsManager;
import fr.madu59.obe.platform.PlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ModCompat {
    private static boolean isIrisLoaded = PlatformHelper.isModLoaded("iris") || PlatformHelper.isModLoaded("oculus");
    private static boolean isSodiumLoaded = PlatformHelper.isModLoaded("sodium") || PlatformHelper.isModLoaded("embeddium");
    private static boolean isEMFLoaded = PlatformHelper.isModLoaded("entity_model_features");
    private static boolean isPunchyLoaded = PlatformHelper.isModLoaded("punchy");


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
}

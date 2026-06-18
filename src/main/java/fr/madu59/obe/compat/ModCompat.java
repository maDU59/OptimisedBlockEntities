package fr.madu59.obe.compat;

import fr.madu59.obe.platform.PlatformHelper;

public class ModCompat {
    private static boolean isIrisLoaded = PlatformHelper.isModLoaded("iris") || PlatformHelper.isModLoaded("oculus");
    private static boolean isSodiumLoaded = PlatformHelper.isModLoaded("sodium") || PlatformHelper.isModLoaded("embeddium");


    public static boolean isIrisLoaded(){
        return isIrisLoaded;
    }

    public static boolean isSodiumLoaded(){
        return isSodiumLoaded;
    }

    public static boolean isShadowPass(){
        if(isIrisLoaded()) return IrisCompat.isShadowPass();
        else return false;
    }
}

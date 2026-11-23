package fr.madu59.obe.client.compat;

import java.util.Arrays;
import java.util.List;

import fr.madu59.obe.client.compat.amendments.AmendmentsCompat;
import fr.madu59.obe.client.compat.biomeswevegone.BiomesWeveGoneCompat;
import fr.madu59.obe.client.compat.blueprint.BlueprintCompat;
import fr.madu59.obe.client.compat.carvedwood.CarvedWoodCompat;
import fr.madu59.obe.OBE;
import fr.madu59.obe.client.compat.bclib.BclibCompat;
import fr.madu59.obe.client.compat.biomeswevegone.BiomesWeveGoneCompat;
import fr.madu59.obe.client.compat.ecologics.EcologicsCompat;
import fr.madu59.obe.client.compat.emf.EMFCompat;
import fr.madu59.obe.client.compat.entityculling.EntityCullingCompat;
import fr.madu59.obe.client.compat.framedblocks.FramedBlocksCompat;
import fr.madu59.obe.client.compat.fancyworldanimations.FancyWorldAnimationsCompat;
import fr.madu59.obe.client.compat.immediatelyfast.ImmediatelyFastCompat;
import fr.madu59.obe.client.compat.iris.IrisCompat;
import fr.madu59.obe.client.compat.ironchest.IronChestCompat;
import fr.madu59.obe.client.compat.lootr.LootrCompat;
import fr.madu59.obe.client.compat.morechestvariants.MoreChestVariantsCompat;
import fr.madu59.obe.client.compat.pipez.PipezCompat;
import fr.madu59.obe.client.compat.twilightforest.TwilightForestCompat;
import fr.madu59.obe.client.compat.vanillabackport.VanillaBackportCompat;
import fr.madu59.obe.client.compat.vanillin.VanillinCompat;
import fr.madu59.obe.client.config.SettingsManager;
import fr.madu59.obe.client.platform.PlatformHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.level.block.state.BlockState;

public class ModCompat {
    private final static boolean isIrisLoaded = PlatformHelper.isModLoaded("iris") || PlatformHelper.isModLoaded("oculus");
    private final static boolean isSodiumLoaded = PlatformHelper.isModLoaded("sodium") || PlatformHelper.isModLoaded("embeddium");
    private static boolean isEMFLoaded = PlatformHelper.isModLoaded("entity_model_features");
    private final static boolean isAmendmentsLoaded = PlatformHelper.isModLoaded("amendments");
    private final static boolean isVanillinLoaded = PlatformHelper.isModLoaded("vanillin");

    private static final List<String> incompatibleMods = Arrays.asList("optifine","embeddium","optifabric");

    public static void init(){
        runSafe("lootr", () -> LootrCompat.init());
        runSafe("lolmcv", () -> MoreChestVariantsCompat.init());
        runSafe("bclib", () -> BclibCompat.init());
        runSafe("carved_wood", () -> CarvedWoodCompat.init());
        runSafe("blueprint", () -> BlueprintCompat.init());
        runSafe("entityculling", () -> EntityCullingCompat.init());
        runSafe("twilightforest", () -> TwilightForestCompat.init());
        runSafe("ironchest", () -> IronChestCompat.init());
        runSafe("framedblocks", () -> FramedBlocksCompat.init());
        runSafe("pipez", () -> PipezCompat.init());
        runSafe("vanillabackport", () -> VanillaBackportCompat.init());
        runSafe("biomeswevegone", () -> BiomesWeveGoneCompat.init());
        runSafe("fwa", () -> FancyWorldAnimationsCompat.init());
        runSafe("amendments", () -> AmendmentsCompat.init());
        runSafe("ecologics", () -> EcologicsCompat.init());
    }

    private static void runSafe(String modId, Runnable runnable){
        if(PlatformHelper.isModLoaded(modId)) {
            try{
                runnable.run();
            }
            catch(Error e){
                OBE.LOGGER.warn("Incompatible version of " + PlatformHelper.getModName(modId) + " used, if you are using the latest one, please report that to OBE's author.");
            }
        }
    }

    public static void onWorldLoad(){

    }

    public static boolean isIrisLoaded(){
        return isIrisLoaded;
    }

    public static boolean isSodiumLoaded(){
        return isSodiumLoaded;
    }

    public static boolean isEMFLoaded(){
        return isEMFLoaded;
    }

    public static boolean isAmendmentsLoaded(){
        return isAmendmentsLoaded;
    }

    public static boolean isVanillinLoaded(){
        return isVanillinLoaded;
    }

    public static boolean isShadowPass(){
        if(isIrisLoaded()) return IrisCompat.isShadowPass();
        else return false;
    }

    public static void applyEMFRestPose(ModelPart root, BlockState state){
        if(isEMFLoaded() && SettingsManager.EMF_COMPAT.getValue() && state != null) {
            try{
                EMFCompat.applyRestPose(root, state);
            }
            catch(Error e){
                OBE.LOGGER.warn("Incompatible version of EMF used, if you are using the latest one, please report that to OBE's author.");
                isEMFLoaded = false;
            }
        }
    }

    public static boolean isSignTextBufferingEnabled() {
        if(PlatformHelper.isModLoaded("immediatelyfast")) return ImmediatelyFastCompat.isSignTextBufferingEnabled();
        else return false;
    }

    public static void preventConflictsWithVanillin() {
        if(isVanillinLoaded() && SettingsManager.VANILLIN_COMPAT.getValue()) {
            try{
                VanillinCompat.preventConflicts();
            }
            catch(Error e){
                OBE.LOGGER.warn("Incompatible version of vanillin used, if you are using the latest one, please report that to OBE's author.");
            }
        }
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

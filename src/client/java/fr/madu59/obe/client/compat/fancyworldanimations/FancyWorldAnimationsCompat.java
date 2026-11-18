package fr.madu59.obe.client.compat.fancyworldanimations;

import fr.madu59.fwa.config.SettingsManager;
import fr.madu59.obe.client.api.rendering.MeshingPredicatesAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BellRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

public class FancyWorldAnimationsCompat {
    public static void init(){
        MeshingPredicatesAPI.registerPredicate(blockEntity -> {
            BlockEntityRenderer<?, ?> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
            if(renderer instanceof BellRenderer){
                return !SettingsManager.BELL_STATE.getValue();
            }
//            else if(renderer instanceof HangingSignRenderer){
//                return !SettingsManager.HANGING_SIGN_STATE.getValue();
//            }
            else return true;
        });
    }
}

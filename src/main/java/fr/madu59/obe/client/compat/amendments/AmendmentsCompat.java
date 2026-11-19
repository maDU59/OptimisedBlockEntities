package fr.madu59.obe.client.compat.amendments;

import fr.madu59.obe.client.api.rendering.MeshingPredicatesAPI;
import net.mehvahdjukaar.amendments.common.ExtendedHangingSign;
import net.mehvahdjukaar.amendments.common.IBellConnection;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AmendmentsCompat {
    public static boolean isPixelConsistentSign(BlockState state) {
        return ClientConfigs.isPixelConsistentSign(state);
    }

    public static boolean isSwingingHangingSign(BlockEntity be){
        return (ClientConfigs.SIGN_ATTACHMENT.get() || ClientConfigs.SWINGING_SIGNS.get() ||
                CommonConfigs.HANGING_SIGN_ITEM.get()) && isExtendedHangingSign(be);
    }

    public static boolean isExtendedHangingSign(BlockEntity be){
        return be instanceof ExtendedHangingSign;
    }

    public static boolean isConnectedBell(BlockEntity be){
        return ClientConfigs.BELL_CONNECTION.get() && isExtendedBell(be);
    }

    public static boolean isExtendedBell(BlockEntity be){
        return be instanceof IBellConnection bell && !bell.amendments$getConnection().isEmpty();
    }

    public static void init(){
        MeshingPredicatesAPI.registerPredicate(blockEntity -> {
            if (AmendmentsCompat.isSwingingHangingSign(blockEntity)) return false;
            if (AmendmentsCompat.isConnectedBell(blockEntity)) return false;
            return true;
        });
    }
}

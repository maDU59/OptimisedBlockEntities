package fr.madu59.obe.client.compat.entityculling;

import dev.tr7zw.entityculling.EntityCullingModBase;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.misc.RenderModeManager;
import net.minecraft.client.Minecraft;

public class EntityCullingCompat {
    public static void init() {
        Minecraft.getInstance().submit(() -> EntityCullingModBase.instance.addDynamicBlockEntityWhitelist(
                (be) -> !(be instanceof BlockEntityExt ext) || (ext.obe$isSupported() && ext.obe$renderMode() == RenderModeManager.RenderMode.TERRAIN && !ext.obe$renderBoth() && !ext.obe$forceEntity())
            )
        );
    }
}

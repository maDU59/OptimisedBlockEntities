package fr.madu59.obe.client.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import fr.madu59.obe.client.util.ResourceUtil;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Mixin(ReloadableResourceManager.class)
public class ReloadableResourceManagerMixin {

    @Inject(method = "createReload", at = @At("HEAD"))
    public void obe$reloadResources(CallbackInfoReturnable<ReloadInstance> ci){
        ResourceUtil.clearCache();
    }
    
}

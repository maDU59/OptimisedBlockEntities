package fr.madu59.obe.client.mixin.blockentity.sign;

import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.util.blockentity.HangingSignUtil;
import fr.madu59.obe.client.util.blockentity.SignUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin {

    @Inject(method = "<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.obe$renderBoth(true);
        ext.obe$isSupported(Registry.isSupported("sign", be));
        if(!ext.obe$isSupported()) ext.obe$isSupported(Registry.isSupported("hanging_sign", be));

        if(!ext.obe$isSupported()) {
            BlockEntityType<?> type = be.getType();
            if(type == null || type == BlockEntityType.SIGN || type == BlockEntityType.HANGING_SIGN) {
                return;
            }
            BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            if(dispatcher == null) return;
            BlockEntityRenderer<?, ?> renderer = dispatcher.getRenderer(be);
            BlockState state = be.getBlockState();
            if(SignUtil.getMaterial(state) != null) {
                if(renderer != null && renderer.getClass() == StandingSignRenderer.class) {
                    Registry.addBlockEntityTypeInGroup("sign", type);
                    ext.obe$isSupported(Registry.isSupported("sign", be));
                    return;
                }
            }
            if(HangingSignUtil.getMaterial(state) != null) {
                if(renderer != null && renderer.getClass() == HangingSignRenderer.class) {
                    Registry.addBlockEntityTypeInGroup("hanging_sign", type);
                    ext.obe$isSupported(Registry.isSupported("hanging_sign", be));
                    return;
                }
            }
        }
    }
}

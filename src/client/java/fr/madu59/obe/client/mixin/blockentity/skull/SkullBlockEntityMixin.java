package fr.madu59.obe.client.mixin.blockentity.skull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(SkullBlockEntity.class)
public abstract class SkullBlockEntityMixin{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci) {
        
        SkullBlockEntity be = (SkullBlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        if(be.getOwnerProfile() != null) ext.renderMode(RenderMode.ENTITY);
        ext.isSupportedBlockEntity(Registry.isSupported("skull", be.getType()));
    }

    @Inject(method = "animation", at = @At("TAIL"))
    private static void obe$updateAnimation(final Level level, final BlockPos pos, final BlockState state, final SkullBlockEntity entity, CallbackInfo ci){
        SkullBlockEntity be = (SkullBlockEntity) entity;
        if(be.isAnimating || be.getOwnerProfile() != null){
            RenderModeManager.setRenderModeDelayed(be, RenderMode.ENTITY, be.getBlockPos());
        }
        else{
            RenderModeManager.setRenderModeDelayed(be, RenderMode.TERRAIN, be.getBlockPos());
        }
    }

    @Inject(method = "applyImplicitComponents", at = @At("RETURN"))
    private void obe$checkProfile(CallbackInfo ci){
        BlockEntityExt ext = (BlockEntityExt)(Object)this;
        SkullBlockEntity skullBe = (SkullBlockEntity)(Object)this;
        if(skullBe.getOwnerProfile() != null) ext.renderMode(RenderMode.ENTITY);
    }

    @Inject(method = "loadAdditional", at = @At("RETURN"))
    private void obe$checkProfileBis(CallbackInfo ci){
        BlockEntityExt ext = (BlockEntityExt)(Object)this;
        SkullBlockEntity skullBe = (SkullBlockEntity)(Object)this;
        if(skullBe.getOwnerProfile() != null) ext.renderMode(RenderMode.ENTITY);
    }
}

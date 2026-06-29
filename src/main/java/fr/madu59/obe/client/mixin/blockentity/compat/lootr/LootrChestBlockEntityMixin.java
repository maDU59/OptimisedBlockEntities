package fr.madu59.obe.client.mixin.blockentity.compat.lootr;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.client.renderer.blockentity.ext.BlockEntityExt;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager;
import fr.madu59.obe.client.renderer.blockentity.misc.RenderModeManager.RenderMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.block.entities.LootrChestBlockEntity;

@Pseudo
@Mixin(value = LootrChestBlockEntity.class, remap = false)
public class LootrChestBlockEntityMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$register(CallbackInfo ci, @Local BlockEntityType<?> beType){
        
        BlockEntity be = (BlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.hasSpecialRenderer(true);
    }

    @Inject(method = "lootrLidAnimateTick", at = @At("HEAD"))
    private static <T extends BlockEntity> void obe$lidAnimateTick(Level level, BlockPos pos, BlockState state, T be, CallbackInfo ci) {
        BlockEntityExt ext = (BlockEntityExt)be;
        if(((LootrChestBlockEntity)be).getOpenNess(0.5f) > 0.0001){
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.ENTITY, pos);
        }
        else{
            RenderModeManager.setRenderModeDelayed(ext, RenderMode.TERRAIN, pos);
        }
    }
}

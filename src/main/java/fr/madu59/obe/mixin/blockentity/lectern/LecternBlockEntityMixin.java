package fr.madu59.obe.mixin.blockentity.lectern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import fr.madu59.obe.config.SettingsManager;
import fr.madu59.obe.renderer.blockentity.ext.BlockEntityExt;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin{

    @Shadow
    private ItemStack book;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void obe$init(CallbackInfo ci, @Local BlockState state) {
        if (!Minecraft.getInstance().isSameThread()) return;
        LecternBlockEntity be = (LecternBlockEntity)(Object)this;
        BlockEntityExt ext = (BlockEntityExt)be;
        
        ext.shouldSkipBeRendering(!state.getValueOrElse(LecternBlock.HAS_BOOK, true) && SettingsManager.OPTIMISED_LECTERNS.getValue() &&  SettingsManager.MOD_TOGGLE.getValue());
    }
}

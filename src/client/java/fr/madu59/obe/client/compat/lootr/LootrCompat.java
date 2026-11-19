package fr.madu59.obe.client.compat.lootr;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.fabric.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.fabric.client.block.LootrShulkerBlockRenderer;

public class LootrCompat {

    public static final ResourceLocation CHEST_MATERIAL = LootrChestBlockRenderer.MATERIAL.texture();
    public static final ResourceLocation CHEST_MATERIAL2 = LootrChestBlockRenderer.MATERIAL2.texture();
    public static final ResourceLocation CHEST_MATERIAL3 = LootrChestBlockRenderer.MATERIAL3.texture();
    public static final ResourceLocation CHEST_MATERIAL4 = LootrChestBlockRenderer.MATERIAL4.texture();

    public static final ResourceLocation SHULKER_BOX_MATERIAL = LootrShulkerBlockRenderer.MATERIAL.texture();
    public static final ResourceLocation SHULKER_BOX_MATERIAL2 = LootrShulkerBlockRenderer.MATERIAL2.texture();

    public static void init(){
        RegistryApi.registerBlockEntityType("chest", LootrRegistry.getChestBlockEntity(), LootrRegistry.getTrappedChestBlockEntity());
        RegistryApi.registerSpecialModelProvider(LootrRegistry.getChestBlockEntity(), ChestUtil::getModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey);
        RegistryApi.registerSpecialModelProvider(LootrRegistry.getTrappedChestBlockEntity(), ChestUtil::getModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey);

        RegistryApi.registerBlockEntityType("shulker_box", LootrRegistry.getShulkerBlockEntity());
        RegistryApi.registerSpecialModelProvider(LootrRegistry.getShulkerBlockEntity(), ShulkerBoxUtil::getModelLayerLocation, LootrCompat::getShulkerBoxMaterial, ShulkerBoxUtil::transform, LootrCompat::getShulkerBoxCacheKey);

        // While this is not mandatory, it helps when an LOD mod is being used (e.g. Voxy or Distant Horizons), it avoids the LOD mod from using the default chest texture for the Lootr chests
        RegistryApi.registerMaterialProvider(LootrRegistry.getChestBlockEntity(), LootrCompat::getChestMaterial);
        RegistryApi.registerMaterialProvider(LootrRegistry.getTrappedChestBlockEntity(), LootrCompat::getChestMaterial);
        RegistryApi.registerMaterialProvider(LootrRegistry.getShulkerBlockEntity(), LootrCompat::getShulkerBoxMaterial);
    }

    public static ResourceLocation getChestMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrChestBlockEntity lootrChestBe){
            boolean isOpened = Minecraft.getInstance().player != null && lootrChestBe.hasClientOpened(Minecraft.getInstance().player);
            return getChestMaterial(state, isOpened);
        }
        return ChestUtil.getMaterial(state);
   }

    public static ResourceLocation getChestMaterial(BlockState state) {
        return getChestMaterial(state, false);
    }

    public static ResourceLocation getChestMaterial(BlockState state, boolean isOpened) {
        boolean isTrapped = state.is(LootrTags.Blocks.TRAPPED_CHESTS);
        if (LootrAPI.isVanillaTextures()) {
            if (isTrapped) {
                return Sheets.CHEST_TRAP_LOCATION.texture();
            } else {
                return Sheets.CHEST_LOCATION.texture();
            }
        }
        if (isOpened) {
            return isTrapped ? CHEST_MATERIAL4 : CHEST_MATERIAL2;
        } else {
            return isTrapped ? CHEST_MATERIAL3 : CHEST_MATERIAL;
        }
    }

    public static ResourceLocation getShulkerBoxMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrShulkerBlockEntity lootrShulkerBoxBe){
            boolean isOpened = lootrShulkerBoxBe.isClientOpened();
            return getShulkerBoxMaterial(state, isOpened);
        }
        return ShulkerBoxUtil.getMaterial(state);
   }

   public static ResourceLocation getShulkerBoxMaterial(BlockState state) {
        return getShulkerBoxMaterial(state, false);
   }

   public static ResourceLocation getShulkerBoxMaterial(BlockState state, boolean isOpened) {
       if (LootrAPI.isVanillaTextures()) {
           return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture();
       } else {
           return isOpened ? SHULKER_BOX_MATERIAL2 : SHULKER_BOX_MATERIAL;
       }
   }

    public static void transformChest(BlockState state, BlockEntity be, PoseStack poseStack) {
        poseStack.pushPose();
        ChestUtil.transform(state, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.popPose();
    }

    public static Object getChestCacheKey(BlockEntity be) {
        return be instanceof LootrChestBlockEntity lootrChestBe && Minecraft.getInstance().player != null && lootrChestBe.hasClientOpened(Minecraft.getInstance().player);
    }

    public static Object getShulkerBoxCacheKey(BlockEntity be) {
        return be instanceof LootrShulkerBlockEntity lootrShulkerBoxBe && lootrShulkerBoxBe.isClientOpened();
    }
}

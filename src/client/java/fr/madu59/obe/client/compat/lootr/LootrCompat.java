package fr.madu59.obe.client.compat.lootr;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.zestyblaze.lootr.LootrTags;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.block.entities.LootrChestBlockEntity;
import net.zestyblaze.lootr.block.entities.LootrShulkerBlockEntity;
import net.zestyblaze.lootr.client.block.LootrChestBlockRenderer;
import net.zestyblaze.lootr.client.block.LootrShulkerBlockRenderer;
import net.zestyblaze.lootr.config.ConfigManager;
import net.zestyblaze.lootr.init.ModBlockEntities;

public class LootrCompat {

    public static final ResourceLocation MATERIAL = LootrChestBlockRenderer.MATERIAL.texture();
    public static final ResourceLocation MATERIAL2 = LootrChestBlockRenderer.MATERIAL2.texture();
    public static final ResourceLocation MATERIAL3 = LootrChestBlockRenderer.MATERIAL3.texture();
    public static final ResourceLocation MATERIAL4 = LootrChestBlockRenderer.MATERIAL4.texture();
    public static final ResourceLocation OLD_MATERIAL = LootrChestBlockRenderer.OLD_MATERIAL.texture();
    public static final ResourceLocation OLD_MATERIAL2 = LootrChestBlockRenderer.OLD_MATERIAL2.texture();

    public static final ResourceLocation SHULKER_BOX_MATERIAL = LootrShulkerBlockRenderer.MATERIAL.texture();
    public static final ResourceLocation SHULKER_BOX_MATERIAL2 = LootrShulkerBlockRenderer.MATERIAL2.texture();
    public static final ResourceLocation SHULKER_BOX_OLD_MATERIAL = LootrShulkerBlockRenderer.OLD_MATERIAL.texture();
    public static final ResourceLocation SHULKER_BOX_OLD_MATERIAL2 = LootrShulkerBlockRenderer.OLD_MATERIAL2.texture();

    private static UUID playerId = null;

    public static void init(){
        RegistryApi.registerBlockEntityType("chest", ModBlockEntities.SPECIAL_LOOT_CHEST, ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST);
        RegistryApi.registerSpecialModelProvider(ModBlockEntities.SPECIAL_LOOT_CHEST, ChestUtil::getModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey);
        RegistryApi.registerSpecialModelProvider(ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST, ChestUtil::getModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey);

        RegistryApi.registerBlockEntityType("shulker_box", ModBlockEntities.SPECIAL_LOOT_SHULKER);
        RegistryApi.registerSpecialModelProvider(ModBlockEntities.SPECIAL_LOOT_SHULKER, ShulkerBoxUtil::getModelLayerLocation, LootrCompat::getShulkerBoxMaterial, ShulkerBoxUtil::transform, LootrCompat::getShulkerBoxCacheKey);

        // While this is not mandatory, it helps when an LOD mod is being used (e.g. Voxy or Distant Horizons), it avoids the LOD mod from using the default chest texture for the Lootr chests
        RegistryApi.registerMaterialProvider(ModBlockEntities.SPECIAL_LOOT_CHEST, LootrCompat::getChestMaterial);
        RegistryApi.registerMaterialProvider(ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST, LootrCompat::getChestMaterial);
        RegistryApi.registerMaterialProvider(ModBlockEntities.SPECIAL_LOOT_SHULKER, LootrCompat::getShulkerBoxMaterial);
    }

    public static <T extends LootrChestBlockEntity & ILootBlockEntity> ResourceLocation getChestMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrChestBlockEntity lootrChestBe){
            if (playerId == null) {
                Player player = Minecraft.getInstance().player;
                if (player != null)  playerId = player.getUUID();
            }
            boolean isOpened = lootrChestBe.isOpened() || (playerId != null && lootrChestBe.getOpeners().contains(playerId));
            return getChestMaterial(state, isOpened);
        }
        return ChestUtil.getMaterial(state);
   }

    public static ResourceLocation getChestMaterial(BlockState state) {
        return getChestMaterial(state, false);
    }

    public static ResourceLocation getChestMaterial(BlockState state, boolean isOpened) {
        boolean isTrapped = state.is(LootrTags.Blocks.TRAPPED_CHESTS);
        if (ConfigManager.isVanillaTextures()) {
            return isTrapped ? Sheets.CHEST_TRAP_LOCATION.texture() : Sheets.CHEST_LOCATION.texture();
        } else if (ConfigManager.get().client.old_textures) {
            return isOpened ? OLD_MATERIAL2 : OLD_MATERIAL;
        } else {
            if (isOpened) {
                return isTrapped ? MATERIAL4 : MATERIAL2;
            } else {
                return isTrapped ? MATERIAL3 : MATERIAL;
            }
        }
    }

    public static ResourceLocation getShulkerBoxMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrShulkerBlockEntity lootrShulkerBoxBe){
            if (playerId == null) {
                Player player = Minecraft.getInstance().player;
                if (player != null)  playerId = player.getUUID();
            }
            boolean isOpened = playerId!= null && lootrShulkerBoxBe.getOpeners().contains(playerId);
            return getShulkerBoxMaterial(state, isOpened);
        }
        return ShulkerBoxUtil.getMaterial(state);
   }

   public static ResourceLocation getShulkerBoxMaterial(BlockState state) {
        return getShulkerBoxMaterial(state, false);
   }

   public static ResourceLocation getShulkerBoxMaterial(BlockState state, boolean isOpened) {
            if (ConfigManager.get().client.vanilla_textures) {
                return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture();
            } else if (ConfigManager.get().client.old_textures) {
                return isOpened ? SHULKER_BOX_OLD_MATERIAL2 : SHULKER_BOX_OLD_MATERIAL;
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
        return be instanceof LootrChestBlockEntity lootrChestBe && Minecraft.getInstance().player != null && lootrChestBe.getOpeners().contains(Minecraft.getInstance().player.getUUID());
    }

    public static Object getShulkerBoxCacheKey(BlockEntity be) {
        return be instanceof LootrShulkerBlockEntity lootrShulkerBoxBe && Minecraft.getInstance().player != null && lootrShulkerBoxBe.getOpeners().contains(Minecraft.getInstance().player.getUUID());
    }
}

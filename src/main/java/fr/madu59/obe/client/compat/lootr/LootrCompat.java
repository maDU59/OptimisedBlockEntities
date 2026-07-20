package fr.madu59.obe.client.compat.lootr;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.block.entities.LootrChestBlockEntity;
import noobanidus.mods.lootr.block.entities.LootrShulkerBlockEntity;
import noobanidus.mods.lootr.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.client.block.LootrShulkerBlockRenderer;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlockEntities;

public class LootrCompat {

    public static final ResourceLocation MATERIAL = LootrChestBlockRenderer.MATERIAL.texture();
    public static final ResourceLocation MATERIAL2 = LootrChestBlockRenderer.MATERIAL2.texture();
    public static final ResourceLocation MATERIAL3 = LootrChestBlockRenderer.MATERIAL3.texture();
    public static final ResourceLocation MATERIAL4 = LootrChestBlockRenderer.MATERIAL4.texture();
    public static final ResourceLocation OLD_MATERIAL = LootrChestBlockRenderer.OLD_MATERIAL.texture();
    public static final ResourceLocation OLD_MATERIAL2 = LootrChestBlockRenderer.OLD_MATERIAL2.texture();

    public static final ResourceLocation SHULKER_BOX_MATERIAL = LootrShulkerBlockRenderer.MATERIAL.texture();
    public static final ResourceLocation SHULKER_BOX_MATERIAL2 = LootrShulkerBlockRenderer.MATERIAL2.texture();

    private static UUID playerId = null;

    public static void init(){
        Registry.addBlockEntityTypeInGroup("chest", ModBlockEntities.LOOTR_CHEST.get(), ModBlockEntities.LOOTR_TRAPPED_CHEST.get());
        SpecialModelGetter.register(ModBlockEntities.LOOTR_CHEST.get(), new SpecialModelProvider(ChestUtil::getModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey));
        SpecialModelGetter.register(ModBlockEntities.LOOTR_TRAPPED_CHEST.get(), new SpecialModelProvider(ChestUtil::getModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey));

        Registry.addBlockEntityTypeInGroup("shulker_box", ModBlockEntities.LOOTR_SHULKER.get());
        SpecialModelGetter.register(ModBlockEntities.LOOTR_SHULKER.get(), new SpecialModelProvider(ShulkerBoxUtil::getModelLayerLocation, LootrCompat::getShulkerBoxMaterial, ShulkerBoxUtil::transform, LootrCompat::getShulkerBoxCacheKey));
    }

    public static <T extends LootrChestBlockEntity & ILootBlockEntity> ResourceLocation getChestMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrChestBlockEntity lootrChestBe){
            if (ConfigManager.isVanillaTextures()) {
                return lootrChestBe.getType() == ModBlockEntities.LOOTR_TRAPPED_CHEST.get() ? Sheets.CHEST_TRAP_LOCATION.texture() : Sheets.chooseMaterial(lootrChestBe, ChestType.SINGLE, false).texture();
            } else if (ConfigManager.isOldTextures()) {
                if (playerId == null) {
                    Player player = Minecraft.getInstance().player;
                    if (player == null) {
                    return OLD_MATERIAL;
                    }

                    playerId = player.getUUID();
                }

                if (lootrChestBe.isOpened()) {
                    return OLD_MATERIAL2;
                } else {
                    return lootrChestBe.getOpeners().contains(playerId) ? OLD_MATERIAL2 : OLD_MATERIAL;
                }
            } else {
                if (playerId == null) {
                    Player player = Minecraft.getInstance().player;
                    if (player == null) {
                    if (lootrChestBe.getType() == ModBlockEntities.LOOTR_TRAPPED_CHEST.get()) {
                        return MATERIAL3;
                    }

                    return MATERIAL;
                    }

                    playerId = player.getUUID();
                }

                if (lootrChestBe.isOpened()) {
                    return lootrChestBe.getType() == ModBlockEntities.LOOTR_TRAPPED_CHEST.get() ? MATERIAL4 : MATERIAL2;
                } else if (lootrChestBe.getOpeners().contains(playerId)) {
                    return lootrChestBe.getType() == ModBlockEntities.LOOTR_TRAPPED_CHEST.get() ? MATERIAL4 : MATERIAL2;
                } else {
                    return lootrChestBe.getType() == ModBlockEntities.LOOTR_TRAPPED_CHEST.get() ? MATERIAL3 : MATERIAL;
                }
            }
        }
        else{
            return ChestUtil.getMaterial(state);
        }
   }

    public static ResourceLocation getShulkerBoxMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrShulkerBlockEntity lootrShulkerBe){
            if (ConfigManager.isVanillaTextures()) {
                return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture();
            }
            else {
                if (playerId == null) {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.player == null) {
                        return SHULKER_BOX_MATERIAL;
                    }

                    playerId = mc.player.getUUID();
                }

                return lootrShulkerBe.getOpeners().contains(playerId) ? SHULKER_BOX_MATERIAL2 : SHULKER_BOX_MATERIAL;
            }
        }
        else return ShulkerBoxUtil.getMaterial(state);
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

package fr.madu59.obe.client.compat.lootr;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.zestyblaze.lootr.LootrTags;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.block.entities.LootrChestBlockEntity;
import net.zestyblaze.lootr.client.block.LootrChestBlockRenderer;
import net.zestyblaze.lootr.config.ConfigManager;
import net.zestyblaze.lootr.init.ModBlockEntities;

public class LootrCompat {

    public static final ResourceLocation MATERIAL = LootrChestBlockRenderer.MATERIAL.texture();
    public static final ResourceLocation MATERIAL2 = LootrChestBlockRenderer.MATERIAL2.texture();
    public static final ResourceLocation MATERIAL3 = LootrChestBlockRenderer.MATERIAL3.texture();
    public static final ResourceLocation MATERIAL4 = LootrChestBlockRenderer.MATERIAL4.texture();
    public static final ResourceLocation OLD_MATERIAL = LootrChestBlockRenderer.OLD_MATERIAL.texture();
    public static final ResourceLocation OLD_MATERIAL2 = LootrChestBlockRenderer.OLD_MATERIAL2.texture();

    private static UUID playerId = null;

    public static void init(){
        Registry.addBlockEntityTypeInGroup("chest", ModBlockEntities.SPECIAL_LOOT_CHEST);
        SpecialModelGetter.register(ModBlockEntities.SPECIAL_LOOT_CHEST, new SpecialModelProvider(ChestUtil::getChestModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey));
    }

    public static <T extends LootrChestBlockEntity & ILootBlockEntity> ResourceLocation getChestMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrChestBlockEntity lootrChestBe){
            if (ConfigManager.isVanillaTextures()) {
                return lootrChestBe.getType() == ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST ? Sheets.CHEST_TRAP_LOCATION.texture() : Sheets.chooseMaterial(lootrChestBe, ChestType.SINGLE, false).texture();
            } else if (ConfigManager.get().client.old_textures) {
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
                    if (lootrChestBe.getType() == ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST) {
                        return MATERIAL3;
                    }

                    return MATERIAL;
                    }

                    playerId = player.getUUID();
                }

                if (lootrChestBe.isOpened()) {
                    return lootrChestBe.getType() == ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST ? MATERIAL4 : MATERIAL2;
                } else if (lootrChestBe.getOpeners().contains(playerId)) {
                    return lootrChestBe.getType() == ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST ? MATERIAL4 : MATERIAL2;
                } else {
                    return lootrChestBe.getType() == ModBlockEntities.SPECIAL_TRAPPED_LOOT_CHEST ? MATERIAL3 : MATERIAL;
                }
            }
        }
        else{
            return ChestUtil.getChestMaterial(state);
        }
   }

    public static void transformChest(BlockState state, BlockEntity be, PoseStack poseStack) {
        poseStack.pushPose();
        ChestUtil.transformChest(state, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.popPose();
    }

    public static Object getChestCacheKey(BlockEntity be) {
        return be instanceof LootrChestBlockEntity lootrChestBe && Minecraft.getInstance().player != null && lootrChestBe.getOpeners().contains(Minecraft.getInstance().player.getUUID());
    }
}

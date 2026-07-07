package fr.madu59.obe.client.compat.lootr;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBoxBlockEntity;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBoxRenderer;

public class LootrCompat {

    public static final Identifier CHEST_MATERIAL = LootrChestBlockRenderer.MATERIAL.texture();
    public static final Identifier CHEST_MATERIAL2 = LootrChestBlockRenderer.MATERIAL2.texture();
    public static final Identifier CHEST_MATERIAL3 = LootrChestBlockRenderer.MATERIAL3.texture();
    public static final Identifier CHEST_MATERIAL4 = LootrChestBlockRenderer.MATERIAL4.texture();

    public static final Identifier SHULKER_BOX_MATERIAL = LootrShulkerBoxRenderer.MATERIAL.texture();
    public static final Identifier SHULKER_BOX_MATERIAL2 = LootrShulkerBoxRenderer.MATERIAL2.texture();

    public static void init(){
        Registry.addBlockEntityTypeInGroup("chest", LootrRegistry.getChestBlockEntity(), LootrRegistry.getTrappedChestBlockEntity());
        SpecialModelGetter.register(LootrRegistry.getChestBlockEntity(), new SpecialModelProvider(ChestUtil::getChestModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey));
        SpecialModelGetter.register(LootrRegistry.getTrappedChestBlockEntity(), new SpecialModelProvider(ChestUtil::getChestModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey));
    
        Registry.addBlockEntityTypeInGroup("shulker_box", LootrRegistry.getShulkerBoxBlockEntity());
        SpecialModelGetter.register(LootrRegistry.getShulkerBoxBlockEntity(), new SpecialModelProvider(ShulkerBoxUtil::getShulkerBoxModelLayerLocation, LootrCompat::getShulkerBoxMaterial, ShulkerBoxUtil::transformShulkerBox, LootrCompat::getShulkerBoxCacheKey));
    }

    public static Identifier getChestMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrChestBlockEntity lootrChestBe){
            boolean isTrapped = be.getBlockState().is(LootrTags.Blocks.TRAPPED_CHESTS);
            boolean isOpened = Minecraft.getInstance().player != null && lootrChestBe.hasClientOpened(Minecraft.getInstance().player.getUUID());
            if (LootrAPI.isVanillaTextures()) {
                if (isTrapped) {
                    return Sheets.CHEST_TRAPPED.single().texture();
                } else {
                    return Sheets.CHEST_REGULAR.single().texture();
                }
            }
            if (isOpened) {
                return isTrapped ? CHEST_MATERIAL4 : CHEST_MATERIAL2;
            } else {
                return isTrapped ? CHEST_MATERIAL3 : CHEST_MATERIAL;
            }
        }
        return ChestUtil.getChestMaterial(state);
    }

    public static Identifier getShulkerBoxMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrShulkerBoxBlockEntity lootrShulkerBoxBe){
            if (LootrAPI.isVanillaTextures()) {
                return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture();
            } else {
                return Minecraft.getInstance().player != null && lootrShulkerBoxBe.hasClientOpened(Minecraft.getInstance().player.getUUID()) ? SHULKER_BOX_MATERIAL2 : SHULKER_BOX_MATERIAL;
            }
        }
        return ShulkerBoxUtil.getShulkerBoxMaterial(state);
   }

    public static void transformChest(BlockState state, BlockEntity be, PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.mulPose(ChestRenderer.modelTransformation(state.getValue(ChestBlock.FACING).getClockWise()));
        poseStack.popPose();
    }

    public static Object getChestCacheKey(BlockEntity be) {
        return be instanceof LootrChestBlockEntity lootrChestBe && Minecraft.getInstance().player != null && lootrChestBe.hasClientOpened(Minecraft.getInstance().player.getUUID());
    }

    public static Object getShulkerBoxCacheKey(BlockEntity be) {
        return be instanceof LootrShulkerBoxBlockEntity lootrShulkerBoxBe && Minecraft.getInstance().player != null && lootrShulkerBoxBe.hasClientOpened(Minecraft.getInstance().player.getUUID());
    }
}

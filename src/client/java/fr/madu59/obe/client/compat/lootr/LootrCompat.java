package fr.madu59.obe.client.compat.lootr;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;

public class LootrCompat {

    public static final ResourceLocation MATERIAL = LootrAPI.rl("chest");
    public static final ResourceLocation MATERIAL2 = LootrAPI.rl("chest_opened");
    public static final ResourceLocation MATERIAL3 = LootrAPI.rl("chest_trapped");
    public static final ResourceLocation MATERIAL4 = LootrAPI.rl("chest_trapped_opened");

    public static void init(){
        Registry.addBlockEntityTypeInGroup("chest", LootrRegistry.getChestBlockEntity());
        SpecialModelGetter.register(LootrRegistry.getChestBlockEntity(), new SpecialModelProvider(ChestUtil::getChestModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey));
    }

    public static ResourceLocation getChestMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrChestBlockEntity lootrChestBe){
            boolean isTrapped = be.getBlockState().is(LootrTags.Blocks.TRAPPED_CHESTS);
            boolean isOpened = Minecraft.getInstance().player != null && lootrChestBe.hasClientOpened(Minecraft.getInstance().player.getUUID());
            if (LootrAPI.isVanillaTextures()) {
                if (isTrapped) {
                    return Sheets.CHEST_TRAP_LOCATION.texture();
                } else {
                    return Sheets.CHEST_LOCATION.texture();
                }
            }
            if (isOpened) {
                return isTrapped ? MATERIAL4 : MATERIAL2;
            } else {
                return isTrapped ? MATERIAL3 : MATERIAL;
            }
        }
        return ChestUtil.getChestMaterial(state);
    }

    public static void transformChest(BlockState state, BlockEntity be, PoseStack poseStack) {
        poseStack.pushPose();
        ChestUtil.transformChest(state, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.popPose();
    }

    public static Object getChestCacheKey(BlockEntity be) {
        return be instanceof LootrChestBlockEntity lootrChestBe && Minecraft.getInstance().player != null && lootrChestBe.hasClientOpened(Minecraft.getInstance().player.getUUID());
    }
}

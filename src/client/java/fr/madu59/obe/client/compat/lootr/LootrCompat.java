package fr.madu59.obe.client.compat.lootr;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
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

public class LootrCompat {

    public static final Identifier MATERIAL = LootrAPI.rl("entity/chest/normal");
    public static final Identifier MATERIAL2 = LootrAPI.rl("entity/chest/normal_opened");
    public static final Identifier MATERIAL3 = LootrAPI.rl("entity/chest/trapped");
    public static final Identifier MATERIAL4 = LootrAPI.rl("entity/chest/trapped_opened");

    public static void init(){
        Registry.addBlockEntityTypeInGroup("chest", LootrRegistry.getChestBlockEntity(), LootrRegistry.getTrappedChestBlockEntity());
        SpecialModelGetter.register(LootrRegistry.getChestBlockEntity(), new SpecialModelProvider(ChestUtil::getChestModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey));
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
                return isTrapped ? MATERIAL4 : MATERIAL2;
            } else {
                return isTrapped ? MATERIAL3 : MATERIAL;
            }
        }
        return ChestUtil.getChestMaterial(state);
    }

    public static void transformChest(BlockState state, BlockEntity be, PoseStack poseStack) {
        poseStack.pushPose();
        poseStack.mulPose(ChestRenderer.modelTransformation(state.getValue(ChestBlock.FACING).getClockWise()));
        poseStack.popPose();
    }

    public static Object getChestCacheKey(BlockEntity be) {
        return be instanceof LootrChestBlockEntity lootrChestBe && Minecraft.getInstance().player != null && lootrChestBe.hasClientOpened(Minecraft.getInstance().player.getUUID());
    }
}

package fr.madu59.obe.client.compat.lootr;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import fr.madu59.obe.client.util.blockentity.ShulkerBoxUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrChestType;
import noobanidus.mods.lootr.common.api.LootrRegistry;
import noobanidus.mods.lootr.common.block.entity.LootrChestBlockEntity;
import noobanidus.mods.lootr.common.block.entity.LootrShulkerBoxBlockEntity;
import noobanidus.mods.lootr.common.client.block.LootrChestBlockRenderer;
import noobanidus.mods.lootr.common.client.block.LootrShulkerBoxRenderer;

public class LootrCompat {

    public static final Identifier SHULKER_BOX_MATERIAL = LootrShulkerBoxRenderer.MATERIAL.texture();
    public static final Identifier SHULKER_BOX_MATERIAL2 = LootrShulkerBoxRenderer.MATERIAL2.texture();

    public static void init(){
        Registry.addBlockEntityTypeInGroup("chest", LootrRegistry.getChestBlockEntity(), LootrRegistry.getTrappedChestBlockEntity());
        SpecialModelGetter.register(LootrRegistry.getChestBlockEntity(), new SpecialModelProvider(ChestUtil::getModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey));
        SpecialModelGetter.register(LootrRegistry.getTrappedChestBlockEntity(), new SpecialModelProvider(ChestUtil::getModelLayerLocation, LootrCompat::getChestMaterial, LootrCompat::transformChest, LootrCompat::getChestCacheKey));
    
        Registry.addBlockEntityTypeInGroup("shulker_box", LootrRegistry.getShulkerBoxBlockEntity());
        SpecialModelGetter.register(LootrRegistry.getShulkerBoxBlockEntity(), new SpecialModelProvider(ShulkerBoxUtil::getModelLayerLocation, LootrCompat::getShulkerBoxMaterial, ShulkerBoxUtil::transform, LootrCompat::getShulkerBoxCacheKey));
    }

    public static Identifier getChestMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrChestBlockEntity lootrChestBe){
            boolean isOpened = Minecraft.getInstance().player != null && lootrChestBe.hasClientOpened(Minecraft.getInstance().player.getUUID());
            if (LootrAPI.isVanillaTextures()) {
                Identifier id;
                switch (LootrChestType.fromState(state)) {
                    case NORMAL -> id = Sheets.CHEST_REGULAR.single().texture();
                    case COPPER -> id = Sheets.CHEST_COPPER_UNAFFECTED.single().texture();
                    case WEATHERED -> id = Sheets.CHEST_COPPER_WEATHERED.single().texture();
                    case EXPOSED -> id = Sheets.CHEST_COPPER_EXPOSED.single().texture();
                    case OXIDIZED -> id = Sheets.CHEST_COPPER_OXIDIZED.single().texture();
                    case TRAPPED -> id = Sheets.CHEST_TRAPPED.single().texture();
                    default -> throw new MatchException((String)null, (Throwable)null);
                }

                return id;
            } else if (isOpened) {
                Identifier id;
                switch (LootrChestType.fromState(state)) {
                    case NORMAL -> id = LootrChestBlockRenderer.NORMAL_OPENED.texture();
                    case COPPER -> id = LootrChestBlockRenderer.COPPER_OPENED.texture();
                    case WEATHERED -> id = LootrChestBlockRenderer.COPPER_WEATHERED_OPENED.texture();
                    case EXPOSED -> id = LootrChestBlockRenderer.COPPER_EXPOSED_OPENED.texture();
                    case OXIDIZED -> id = LootrChestBlockRenderer.COPPER_OXIDIZED_OPENED.texture();
                    case TRAPPED -> id = LootrChestBlockRenderer.TRAPPED_OPENED.texture();
                    default -> throw new MatchException((String)null, (Throwable)null);
                }

                return id;
            } else {
                Identifier id;
                switch (LootrChestType.fromState(state)) {
                    case NORMAL -> id = LootrChestBlockRenderer.NORMAL.texture();
                    case COPPER -> id = LootrChestBlockRenderer.COPPER.texture();
                    case WEATHERED -> id = LootrChestBlockRenderer.COPPER_WEATHERED.texture();
                    case EXPOSED -> id = LootrChestBlockRenderer.COPPER_EXPOSED.texture();
                    case OXIDIZED -> id = LootrChestBlockRenderer.COPPER_OXIDIZED.texture();
                    case TRAPPED -> id = LootrChestBlockRenderer.TRAPPED.texture();
                    default -> throw new MatchException((String)null, (Throwable)null);
                }

                return id;
            }
        }
        return ChestUtil.getMaterial(state);
   }

    public static Identifier getShulkerBoxMaterial(BlockState state, BlockEntity be) {
        if(be instanceof LootrShulkerBoxBlockEntity lootrShulkerBoxBe){
            if (LootrAPI.isVanillaTextures()) {
                return Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture();
            } else {
                return Minecraft.getInstance().player != null && lootrShulkerBoxBe.hasClientOpened(Minecraft.getInstance().player.getUUID()) ? SHULKER_BOX_MATERIAL2 : SHULKER_BOX_MATERIAL;
            }
        }
        return ShulkerBoxUtil.getMaterial(state);
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

package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.p3pp3rf1y.sophisticatedcore.renderdata.DisplaySide;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageWrapper;

record ChestAppearanceFingerprint(
        ResourceLocation block,
        ChestType chestType,
        Direction facing,
        String wood,
        boolean mainPresent,
        int mainColor,
        boolean accentPresent,
        int accentColor,
        boolean showTier,
        boolean packed,
        boolean frontDisplay
) {
    static ChestAppearanceFingerprint capture(ChestBlockEntity chest) {
        StorageWrapper wrapper = chest.getMainStorageWrapper();
        boolean frontDisplay = wrapper.getRenderInfo().getItemDisplayRenderInfo().getDisplayItem()
                .map(item -> item.getDisplaySide() == DisplaySide.FRONT)
                .orElse(false);
        return new ChestAppearanceFingerprint(
                BuiltInRegistries.BLOCK.getKey(chest.getBlockState().getBlock()),
                chest.getBlockState().getValue(ChestBlock.TYPE),
                chest.getBlockState().getValue(ChestBlock.FACING),
                chest.getWoodType().map(type -> type.name()).orElse("<generic>"),
                wrapper.hasMainColor(), wrapper.getMainColor(),
                wrapper.hasAccentColor(), wrapper.getAccentColor(),
                chest.shouldShowTier(), chest.isPacked(), frontDisplay);
    }
}

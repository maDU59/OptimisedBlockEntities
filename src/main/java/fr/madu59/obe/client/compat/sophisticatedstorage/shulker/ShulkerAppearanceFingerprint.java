package fr.madu59.obe.client.compat.sophisticatedstorage.shulker;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.ShulkerBoxBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageWrapper;

record ShulkerAppearanceFingerprint(
        ResourceLocation block,
        Direction facing,
        int mainColor,
        int accentColor,
        boolean showTier
) {
    static ShulkerAppearanceFingerprint capture(ShulkerBoxBlockEntity shulker) {
        StorageWrapper wrapper = shulker.getStorageWrapper();
        return new ShulkerAppearanceFingerprint(
                BuiltInRegistries.BLOCK.getKey(shulker.getBlockState().getBlock()),
                shulker.getBlockState().getValue(ShulkerBoxBlock.FACING),
                wrapper.getMainColor(), wrapper.getAccentColor(), shulker.shouldShowTier());
    }
}

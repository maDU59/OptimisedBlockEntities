package fr.madu59.obe.client.compat.sophisticatedstorage;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.settings.itemdisplay.ItemDisplaySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;

/**
 * Binary bridge for legacy Storage call sites (present in the supplied
 * 1.5.70--1.5.73 jars) paired with Core versions that removed the six-argument
 * constructor. The plugin verifies both ABIs before applying the redirect.
 * The old category allowed deselection, so the compatibility value is
 * {@code true}.
 */
public final class SophisticatedCoreConstructorBridge {
    private SophisticatedCoreConstructorBridge() {}

    public static ItemDisplaySettingsCategory create(
            Supplier<InventoryHandler> inventoryHandler,
            Supplier<RenderInfo> renderInfo,
            CompoundTag categoryNbt,
            Consumer<CompoundTag> saveNbt,
            int itemNumberLimit,
            Supplier<MemorySettingsCategory> memorySettings) {
        return new ItemDisplaySettingsCategory(
                inventoryHandler,
                renderInfo,
                categoryNbt,
                saveNbt,
                itemNumberLimit,
                true,
                memorySettings);
    }
}

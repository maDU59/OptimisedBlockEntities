package fr.madu59.obe.client.mixin.compat.sophisticatedstorage;

import java.util.function.Consumer;
import java.util.function.Supplier;

import fr.madu59.obe.client.compat.sophisticatedstorage.SophisticatedCoreConstructorBridge;
import net.minecraft.nbt.CompoundTag;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.settings.itemdisplay.ItemDisplaySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedstorage.settings.StorageSettingsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Adapts the capability-verified legacy Storage call to Core's newer ABI. */
@Mixin(StorageSettingsHandler.class)
public abstract class StorageSettingsHandlerMixin {
    @Redirect(
            method = "lambda$addItemDisplayCategory$1",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/util/function/Supplier;Ljava/util/function/Supplier;Lnet/minecraft/nbt/CompoundTag;Ljava/util/function/Consumer;ILjava/util/function/Supplier;)Lnet/p3pp3rf1y/sophisticatedcore/settings/itemdisplay/ItemDisplaySettingsCategory;"))
    private ItemDisplaySettingsCategory obe$adaptCoreConstructor(
            Supplier<InventoryHandler> inventoryHandler,
            Supplier<RenderInfo> renderInfo,
            CompoundTag categoryNbt,
            Consumer<CompoundTag> saveNbt,
            int itemNumberLimit,
            Supplier<MemorySettingsCategory> memorySettings) {
        return SophisticatedCoreConstructorBridge.create(
                inventoryHandler, renderInfo, categoryNbt, saveNbt, itemNumberLimit, memorySettings);
    }
}

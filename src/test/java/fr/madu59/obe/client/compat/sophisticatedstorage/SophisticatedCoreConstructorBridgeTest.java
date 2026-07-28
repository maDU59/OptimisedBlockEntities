package fr.madu59.obe.client.compat.sophisticatedstorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.minecraft.nbt.CompoundTag;

class SophisticatedCoreConstructorBridgeTest {
    @Test
    void adaptsTheLegacyStorageConstructorToCore1477() {
        var category = SophisticatedCoreConstructorBridge.create(
                () -> null,
                () -> null,
                new CompoundTag(),
                ignored -> {},
                12,
                () -> null);

        assertEquals(12, category.getItemNumberLimit());
        assertTrue(category.canDeselectSlots(), "the removed six-argument constructor allowed slot deselection");
    }
}

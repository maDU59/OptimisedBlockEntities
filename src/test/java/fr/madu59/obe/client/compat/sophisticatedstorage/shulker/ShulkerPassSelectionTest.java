package fr.madu59.obe.client.compat.sophisticatedstorage.shulker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.madu59.obe.client.compat.sophisticatedstorage.shulker.ShulkerMaterialPass.Kind;

class ShulkerPassSelectionTest {
    @Test
    void bothTintsReplaceOnlyTheUntintedBase() {
        assertEquals(List.of(Kind.MAIN_SHELL, Kind.ACCENT_SHELL, Kind.TIER_SHELL),
                ShulkerPassSelection.select(true, true, true));
    }

    @Test
    void eitherMissingTintKeepsTheUntintedBase() {
        assertEquals(List.of(Kind.BASE_SHELL, Kind.MAIN_SHELL),
                ShulkerPassSelection.select(true, false, false));
        assertEquals(List.of(Kind.BASE_SHELL, Kind.ACCENT_SHELL),
                ShulkerPassSelection.select(false, true, false));
    }
}

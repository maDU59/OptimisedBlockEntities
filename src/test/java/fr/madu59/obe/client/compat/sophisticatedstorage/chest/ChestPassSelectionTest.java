package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.madu59.obe.client.compat.sophisticatedstorage.chest.ChestMaterialPass.Kind;

class ChestPassSelectionTest {
    @Test
    void genericWoodWithBothColorsOmitsOnlyTheBasePass() {
        assertEquals(
                List.of(Kind.MAIN_SHELL, Kind.ACCENT_SHELL, Kind.TIER_SHELL, Kind.LATCH, Kind.PACKED_SHELL),
                ChestPassSelection.select(false, true, true, true, true, true)
        );
    }

    @Test
    void oneMissingGenericColorKeepsTheBasePass() {
        assertEquals(
                List.of(Kind.BASE_SHELL, Kind.MAIN_SHELL),
                ChestPassSelection.select(false, true, false, false, false, false)
        );
        assertEquals(
                List.of(Kind.BASE_SHELL, Kind.ACCENT_SHELL),
                ChestPassSelection.select(false, false, true, false, false, false)
        );
    }

    @Test
    void realWoodAlwaysKeepsBaseAndOrdersStaticPassesLikeTargetRenderer() {
        assertEquals(
                List.of(
                        Kind.BASE_SHELL,
                        Kind.MAIN_SHELL,
                        Kind.ACCENT_SHELL,
                        Kind.TIER_SHELL,
                        Kind.LATCH,
                        Kind.PACKED_SHELL
                ),
                ChestPassSelection.select(true, true, true, true, true, true)
        );
    }

    @Test
    void hiddenTierAndFrontDisplayItemExcludeTierAndPhysicalLatch() {
        assertEquals(
                List.of(Kind.BASE_SHELL),
                ChestPassSelection.select(true, false, false, false, false, false)
        );
    }
}

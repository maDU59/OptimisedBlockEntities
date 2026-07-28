package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import java.util.ArrayList;
import java.util.List;

import fr.madu59.obe.client.compat.sophisticatedstorage.chest.ChestMaterialPass.Kind;

/** Mirrors the static-pass ordering in Sophisticated Storage 1.5.70.1941. */
final class ChestPassSelection {
    private ChestPassSelection() {
    }

    static List<Kind> select(boolean hasExplicitWood, boolean hasMainColor, boolean hasAccentColor,
            boolean hasVisibleTier, boolean hasPhysicalLatch, boolean isPacked) {
        List<Kind> passes = new ArrayList<>(6);
        if (hasExplicitWood || !hasMainColor || !hasAccentColor) {
            passes.add(Kind.BASE_SHELL);
        }
        if (hasMainColor) {
            passes.add(Kind.MAIN_SHELL);
        }
        if (hasAccentColor) {
            passes.add(Kind.ACCENT_SHELL);
        }
        if (hasVisibleTier) {
            passes.add(Kind.TIER_SHELL);
        }
        if (hasPhysicalLatch) {
            passes.add(Kind.LATCH);
        }
        if (isPacked) {
            passes.add(Kind.PACKED_SHELL);
        }
        return List.copyOf(passes);
    }
}

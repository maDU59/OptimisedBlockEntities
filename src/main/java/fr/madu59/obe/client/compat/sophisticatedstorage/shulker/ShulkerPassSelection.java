package fr.madu59.obe.client.compat.sophisticatedstorage.shulker;

import java.util.ArrayList;
import java.util.List;

import fr.madu59.obe.client.compat.sophisticatedstorage.shulker.ShulkerMaterialPass.Kind;

final class ShulkerPassSelection {
    private ShulkerPassSelection() {
    }

    static List<Kind> select(boolean hasMainColor, boolean hasAccentColor, boolean hasVisibleTier) {
        List<Kind> passes = new ArrayList<>(4);
        if (!hasMainColor || !hasAccentColor) {
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
        return List.copyOf(passes);
    }
}

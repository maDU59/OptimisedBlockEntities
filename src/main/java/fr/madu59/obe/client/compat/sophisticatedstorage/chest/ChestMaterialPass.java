package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import net.minecraft.resources.ResourceLocation;

/** One immutable texture/tint pass used to bake the static chest shell. */
public record ChestMaterialPass(Kind kind, ResourceLocation texture, int color) {
    public enum Kind {
        BASE_SHELL,
        MAIN_SHELL,
        ACCENT_SHELL,
        TIER_SHELL,
        LATCH,
        PACKED_SHELL
    }
}

package fr.madu59.obe.client.compat.sophisticatedstorage.shulker;

import net.minecraft.resources.ResourceLocation;

public record ShulkerMaterialPass(Kind kind, ResourceLocation texture, int color) {
    public enum Kind {
        BASE_SHELL,
        MAIN_SHELL,
        ACCENT_SHELL,
        TIER_SHELL
    }
}

package fr.madu59.obe.client.compat.sophisticatedstorage.shulker;

import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/** Cache-safe shulker appearance with no world-lifetime references. */
public record SophisticatedShulkerAppearance(
        ResourceLocation blockId,
        Direction facing,
        List<ShulkerMaterialPass> passes,
        boolean ambientOcclusion
) {
    public SophisticatedShulkerAppearance {
        passes = List.copyOf(passes);
    }
}

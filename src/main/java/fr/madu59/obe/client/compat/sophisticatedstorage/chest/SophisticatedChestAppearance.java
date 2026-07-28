package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;

/** Cache-safe chest appearance. Deliberately contains no live game objects. */
public record SophisticatedChestAppearance(
        ResourceLocation blockId,
        ChestType chestType,
        Direction facing,
        String woodType,
        boolean explicitWood,
        List<ChestMaterialPass> passes,
        boolean ambientOcclusion
) {
    public SophisticatedChestAppearance {
        passes = List.copyOf(passes);
    }
}

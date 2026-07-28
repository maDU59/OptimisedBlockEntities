package fr.madu59.obe.client.compat.sophisticatedstorage.shulker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

class SophisticatedShulkerAppearanceTest {
    private static final ShulkerMaterialPass PASS = new ShulkerMaterialPass(
            ShulkerMaterialPass.Kind.BASE_SHELL,
            ResourceLocation.parse("sophisticatedstorage:entity/shulker_box/no_tint"), 0xFFFFFFFF);

    @Test
    void equalityChangesWithFacingTextureOrColor() {
        var first = appearance(Direction.UP, PASS);
        assertEquals(first, appearance(Direction.UP, PASS));
        assertNotEquals(first, appearance(Direction.DOWN, PASS));
        assertNotEquals(first, appearance(Direction.UP, new ShulkerMaterialPass(
                ShulkerMaterialPass.Kind.BASE_SHELL, PASS.texture(), 0xFF112233)));
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void allSixFacingsAreRepresentable(Direction facing) {
        assertEquals(facing, appearance(facing, PASS).facing());
    }

    private static SophisticatedShulkerAppearance appearance(Direction facing, ShulkerMaterialPass pass) {
        return new SophisticatedShulkerAppearance(
                ResourceLocation.parse("sophisticatedstorage:shulker_box"), facing, List.of(pass), false);
    }
}

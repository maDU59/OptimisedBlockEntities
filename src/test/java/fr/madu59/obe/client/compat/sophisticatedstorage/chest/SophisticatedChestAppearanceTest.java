package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.ChestType;

class SophisticatedChestAppearanceTest {
    private static final ResourceLocation BLOCK = ResourceLocation.parse("sophisticatedstorage:chest");
    private static final ChestMaterialPass PASS = new ChestMaterialPass(
            ChestMaterialPass.Kind.BASE_SHELL, ResourceLocation.parse("pack:chest/base"), 0xFFFFFFFF);

    @Test
    void equalityUsesImmutableAppearanceDataAndMaterialIdentity() {
        var first = appearance(ChestType.SINGLE, Direction.NORTH, List.of(PASS));
        assertEquals(first, appearance(ChestType.SINGLE, Direction.NORTH, List.of(PASS)));
        assertNotEquals(first, appearance(ChestType.LEFT, Direction.NORTH, List.of(PASS)));
        assertNotEquals(first, appearance(ChestType.SINGLE, Direction.SOUTH, List.of(PASS)));
        assertNotEquals(first, appearance(ChestType.SINGLE, Direction.NORTH, List.of(
                new ChestMaterialPass(ChestMaterialPass.Kind.BASE_SHELL,
                        ResourceLocation.parse("pack:chest/override"), 0xFFFFFFFF))));
    }

    @ParameterizedTest
    @MethodSource("horizontalFacings")
    void allHorizontalFacingsAreRepresentable(Direction facing) {
        assertEquals(facing, appearance(ChestType.RIGHT, facing, List.of(PASS)).facing());
    }

    private static Stream<Direction> horizontalFacings() {
        return Stream.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    }

    private static SophisticatedChestAppearance appearance(
            ChestType chestType, Direction facing, List<ChestMaterialPass> passes) {
        return new SophisticatedChestAppearance(BLOCK, chestType, facing, "oak", true, passes, false);
    }
}

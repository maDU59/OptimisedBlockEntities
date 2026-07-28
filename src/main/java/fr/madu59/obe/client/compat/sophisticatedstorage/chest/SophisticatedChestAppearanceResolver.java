package fr.madu59.obe.client.compat.sophisticatedstorage.chest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import fr.madu59.obe.client.api.model.SpecialBakedModelContext;
import fr.madu59.obe.client.compat.sophisticatedstorage.chest.ChestMaterialPass.Kind;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.p3pp3rf1y.sophisticatedcore.renderdata.DisplaySide;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.ChestBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.client.StorageTextureManager;
import net.p3pp3rf1y.sophisticatedstorage.client.StorageTextureManager.ChestMaterial;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;

/** Reads only the static shell state consumed by the exact target renderer. */
public final class SophisticatedChestAppearanceResolver {
    private final BooleanSupplier ambientOcclusion;

    public SophisticatedChestAppearanceResolver(BooleanSupplier ambientOcclusion) {
        this.ambientOcclusion = ambientOcclusion;
    }

    public SophisticatedChestAppearance resolve(SpecialBakedModelContext context) {
        if (!(context.blockEntity() instanceof ChestBlockEntity chest)) {
            throw new IllegalArgumentException("Expected Sophisticated Storage chest block entity");
        }

        BlockState state = context.state();
        ChestType chestType = state.getValue(ChestBlock.TYPE);
        Direction facing = state.getValue(ChestBlock.FACING);
        Optional<WoodType> explicitWood = chest.getWoodType();
        WoodType wood = explicitWood.orElse(WoodType.ACACIA);
        Map<ChestMaterial, Material> materials = StorageTextureManager.INSTANCE
                .getWoodChestMaterials(chestType, wood);
        if (materials == null) {
            throw new IllegalStateException("No chest texture definition for " + wood.name() + "/" + chestType);
        }

        StorageWrapper wrapper = chest.getMainStorageWrapper();
        boolean hasMain = wrapper.hasMainColor();
        boolean hasAccent = wrapper.hasAccentColor();
        boolean hasTier = chest.shouldShowTier();
        boolean hasLatch = wrapper.getRenderInfo().getItemDisplayRenderInfo().getDisplayItem()
                .map(item -> item.getDisplaySide() != DisplaySide.FRONT)
                .orElse(true);
        boolean packed = chest.isPacked();

        List<Kind> selected = ChestPassSelection.select(
                explicitWood.isPresent(), hasMain, hasAccent, hasTier, hasLatch, packed);
        ChestMaterial tier = tierMaterial(state.getBlock());
        List<ChestMaterialPass> passes = new ArrayList<>(selected.size());
        for (Kind kind : selected) {
            ChestMaterial source = switch (kind) {
                case BASE_SHELL -> ChestMaterial.BASE;
                case MAIN_SHELL -> ChestMaterial.TINTABLE_MAIN;
                case ACCENT_SHELL -> ChestMaterial.TINTABLE_ACCENT;
                case TIER_SHELL, LATCH -> tier;
                case PACKED_SHELL -> ChestMaterial.PACKED;
            };
            int color = switch (kind) {
                case MAIN_SHELL -> 0xFF000000 | wrapper.getMainColor();
                case ACCENT_SHELL -> 0xFF000000 | wrapper.getAccentColor();
                default -> 0xFFFFFFFF;
            };
            Material material = materials.get(source);
            if (material == null) {
                throw new IllegalStateException("Missing chest material " + source + " for " + wood.name() + "/" + chestType);
            }
            passes.add(new ChestMaterialPass(kind, material.texture(), color));
        }

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return new SophisticatedChestAppearance(
                blockId, chestType, facing, wood.name(), explicitWood.isPresent(), passes, ambientOcclusion.getAsBoolean());
    }

    private static ChestMaterial tierMaterial(Block block) {
        if (block == ModBlocks.COPPER_CHEST.get()) {
            return ChestMaterial.COPPER_TIER;
        }
        if (block == ModBlocks.IRON_CHEST.get()) {
            return ChestMaterial.IRON_TIER;
        }
        if (block == ModBlocks.GOLD_CHEST.get()) {
            return ChestMaterial.GOLD_TIER;
        }
        if (block == ModBlocks.DIAMOND_CHEST.get()) {
            return ChestMaterial.DIAMOND_TIER;
        }
        if (block == ModBlocks.NETHERITE_CHEST.get()) {
            return ChestMaterial.NETHERITE_TIER;
        }
        return ChestMaterial.WOOD_TIER;
    }
}

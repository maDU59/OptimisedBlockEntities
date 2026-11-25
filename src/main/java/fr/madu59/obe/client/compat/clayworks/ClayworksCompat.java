package fr.madu59.obe.client.compat.clayworks;

import com.teamabnormals.clayworks.common.block.TrimmedPot;
import com.teamabnormals.clayworks.core.registry.ClayworksBlocks;
import com.teamabnormals.clayworks.core.registry.ClayworksMaterials;
import fr.madu59.obe.client.api.registry.RegistryApi;
import fr.madu59.obe.client.api.rendering.MeshingPredicatesAPI;
import fr.madu59.obe.client.util.blockentity.DecoratedPotUtil;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;

public class ClayworksCompat {
    public static void init() {
        MeshingPredicatesAPI.registerPredicate(blockEntity -> {
            if(blockEntity instanceof TrimmedPot) {
                return false;
            }
            return true;
        });

        RegistryApi.registerMaterialProvider(BlockEntityType.DECORATED_POT, ClayworksCompat::getDecoratedPotMaterial);
    }

    public static ResourceLocation getDecoratedPotMaterial(BlockState state) {
        DyeColor color = ClayworksBlocks.getDyeColorFromPot(state.getBlock());
        if (color != null) {
            Material material = ClayworksMaterials.getDecoratedPotMaterial(DecoratedPotPatterns.BASE, color);
            if(material != null) {
                return material.texture();
            }
        }

        return DecoratedPotUtil.getMaterial(state);
    }
}

package fr.madu59.obe.client.compat.woodwork;

import com.teamabnormals.blueprint.core.registry.BlueprintBlockEntityTypes;

import fr.madu59.obe.client.registry.Registry;
import fr.madu59.obe.client.registry.SpecialModelGetter;
import fr.madu59.obe.client.registry.SpecialModelGetter.SpecialModelProvider;
import fr.madu59.obe.client.util.blockentity.ChestUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WoodWorkCompat {
    public static void init(){
        Registry.addBlockEntityTypeInGroup("chest", BlueprintBlockEntityTypes.CHEST.get(), BlueprintBlockEntityTypes.TRAPPED_CHEST.get());
        SpecialModelGetter.register(BlueprintBlockEntityTypes.CHEST.get(), new SpecialModelProvider(ChestUtil::getModelLayerLocation, WoodWorkCompat::getChestMaterial, ChestUtil::transform, SpecialModelProvider::getDummyCacheKey));
        SpecialModelGetter.register(BlueprintBlockEntityTypes.TRAPPED_CHEST.get(), new SpecialModelProvider(ChestUtil::getModelLayerLocation, WoodWorkCompat::getChestMaterial, ChestUtil::transform, SpecialModelProvider::getDummyCacheKey));
    }

    public static ResourceLocation getChestMaterial(BlockState state, BlockEntity be) {
        return ResourceLocation.tryParse("TO:DO");
    }
}
